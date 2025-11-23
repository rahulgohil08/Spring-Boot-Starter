package com.world.spring.features.new_csv.config

import com.world.spring.features.new_csv.entity.CsvPerson
import com.world.spring.features.new_csv.request.CsvPersonDto
import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.transaction.PlatformTransactionManager
import java.io.File

/*
 * Batch configuration:
 * - Defines FlatFileItemReader<CsvPersonDto>, ItemProcessor (adapter to our CsvPersonProcessor),
 *   and JpaItemWriter<CsvPerson> for persistence.
 * - Uses chunk-oriented processing with a chunk size chosen for balanced memory vs commit frequency.
 *
 * Chunk size rationale:
 * - For a 10k-record CSV, a chunk size of 1_000 commits every 1000 records -> 10 transactions.
 *   This reduces transaction overhead compared to 1-by-1, but avoids storing huge lists in memory.
 * - In production you may tune chunk size based on available memory, DB latency, and throughput needs.
 *
 * Non-obvious choices:
 * - We map CSV by index: 0=name,1=email,2=age,3=city per your specification.
 * - We use a simple BeanWrapperFieldSetMapper to populate CsvPersonDto.
 * - Reader is @StepScope to access jobParameters at execution time, not bean creation time.
 */

@Configuration
class BatchConfig(
    private val csvPersonProcessor: CsvPersonProcessor,
    private val entityManagerFactory: EntityManagerFactory,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    @StepScope
    fun csvPersonReader(
        @Value("#{jobParameters['filePath'] ?: 'src/main/resources/sample.csv'}") filePath: String
    ): FlatFileItemReader<CsvPersonDto> {
        val resource: Resource = FileSystemResource(File(filePath))

        log.info("Configured CSV reader for file: {}", resource.file.absolutePath)
        return FlatFileItemReaderBuilder<CsvPersonDto>()
            .name("csvPersonReader")
            .resource(resource)
            .delimited()
            .names("name", "email", "age", "city")
            .fieldSetMapper { fieldSet ->
                CsvPersonDto(
                    name = fieldSet.readString("name"),
                    email = fieldSet.readString("email"),
                    age = fieldSet.readInt("age"),
                    city = fieldSet.readString("city")
                )
            }
            .strict(false)
            .build()
    }

    @Bean
    fun csvPersonProcessorAdapter(): ItemProcessor<CsvPersonDto, CsvPerson> {
        // adapt our simple CsvPersonProcessor to Spring Batch ItemProcessor interface
        return ItemProcessor<CsvPersonDto, CsvPerson> { dto -> csvPersonProcessor.process(dto) }
    }

    @Bean
    fun csvPersonWriter(): JpaItemWriter<CsvPerson> {
        return JpaItemWriterBuilder<CsvPerson>().entityManagerFactory(entityManagerFactory).build()
    }

    @Bean
    fun importCsvStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        csvPersonReader: FlatFileItemReader<CsvPersonDto>
    ): Step {
        val chunkSize = 1000 // tuned for 10k sample (see comment above)
        val stepBuilder = StepBuilder("importCsvStep", jobRepository)
        return stepBuilder
            .chunk<CsvPersonDto, CsvPerson>(chunkSize, transactionManager)
            .reader(csvPersonReader)
            .processor(csvPersonProcessorAdapter())
            .writer(csvPersonWriter())
            .faultTolerant()
            .skipLimit(1000) // if many bad rows, skip up to this limit (adjust as needed)
            .skip(Exception::class.java)
            .build()
    }

    @Bean
    fun importCsvJob(jobRepository: JobRepository, importCsvStep: Step): Job {
        val jobBuilder = JobBuilder("importCsvJob", jobRepository)
        return jobBuilder
            .incrementer(RunIdIncrementer())
            .start(importCsvStep)
            .build()
    }
}