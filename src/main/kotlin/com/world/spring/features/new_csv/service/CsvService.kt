package com.world.spring.features.new_csv.service

import com.world.spring.features.new_csv.entity.CsvPerson
import com.world.spring.features.new_csv.repository.CsvPersonRepository
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Service class for handling CSV operations including parsing and database operations
 */
@Service
class CsvService(
    val csvPersonRepository: CsvPersonRepository,  // Made public so controller can access it
    private val jobLauncher: JobLauncher,
    private val importCsvJob: Job,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Process a CSV file and save records to the database with single record processing
     */
    fun processCsvFileSingle(multipartFile: MultipartFile): Int {
        var recordCount = 0
        val inputStream = multipartFile.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        // Skip header line
        reader.readLine()

        // Process each line one by one
        reader.useLines { lines ->
            lines.forEach { line ->
                val fields = line.split(",")

                if (fields.size >= 4) { // Ensure we have enough fields
                    val csvPerson = CsvPerson(
                        name = fields[0].trim(),
                        email = fields[1].trim(),
                        age = fields[2].trim().toIntOrNull() ?: 0,
                        city = fields[3].trim()
                    )

                    // Save single record to database
                    csvPersonRepository.save(csvPerson)
                    recordCount++
                }
            }
        }

        return recordCount
    }

    /**
     * Process a CSV file and save records to the database in batches for better performance
     */
    fun processCsvFileBatch(multipartFile: MultipartFile, batchSize: Int = 1000): Int {
        var recordCount = 0
        val inputStream = multipartFile.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        // Skip header line
        reader.readLine()

        val batch = mutableListOf<CsvPerson>()

        reader.useLines { lines ->
            lines.forEach { line ->
                val fields = line.split(",")

                if (fields.size >= 4) { // Ensure we have enough fields
                    val csvPerson = CsvPerson(
                        name = fields[0].trim(),
                        email = fields[1].trim(),
                        age = fields[2].trim().toIntOrNull() ?: 0,
                        city = fields[3].trim()
                    )

                    batch.add(csvPerson)
                    recordCount++

                    // Save batch when it reaches the specified size
                    if (batch.size >= batchSize) {
                        csvPersonRepository.saveAll(batch)
                        batch.clear() // Clear the batch after saving
                    }
                }
            }

            // Save remaining records if any
            if (batch.isNotEmpty()) {
                csvPersonRepository.saveAll(batch)
                batch.clear()
            }
        }

        return recordCount
    }

    /**
     * Process CSV file using Spring Batch
     * This method uses Spring Boot Batch framework for processing large files efficiently
     */
    fun processCsvWithBatch(file: MultipartFile): Map<String, Any> {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        // Validate file type
        if (!isValidCsvFile(file)) {
            throw IllegalArgumentException("File must be a CSV")
        }

        // Write uploaded file to a temp location
        val tempFile = Files.createTempFile("csv-upload-", ".csv").toFile()
        file.inputStream.use { input -> Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING) }

        log.info("Received CSV file: {} (size: {} bytes) saved to {}", file.originalFilename, file.size, tempFile.absolutePath)

        val jobParams = JobParametersBuilder()
            .addString("filePath", tempFile.absolutePath)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters()

        val execution: JobExecution = jobLauncher.run(importCsvJob, jobParams)
        val count = execution.stepExecutions.sumOf { it.writeCount }.toInt()

        return mapOf(
            "status" to true,
            "message" to "Job launched with total $count records",
            "jobExecutionId" to execution.id,
            "exitStatus" to execution.exitStatus.exitCode
        )
    }

    /**
     * count of the records in the DB
     */
    fun fetchCountOfRecordsInTheDB(): Int {
         return csvPersonRepository.count().toInt()
    }

    /**
     * Helper function to validate if the uploaded file is a CSV
     */
    private fun isValidCsvFile(file: MultipartFile): Boolean {
        return file.contentType == "text/csv" ||
                file.contentType == "application/vnd.ms-excel" ||
                file.originalFilename?.endsWith(".csv") == true
    }
}