package com.world.spring.features.new_csv.config

import com.world.spring.features.new_csv.entity.CsvPerson
import com.world.spring.features.new_csv.request.CsvPersonDto
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.util.*

/*
 * ItemProcessor that:
 * - validates required fields (name & email)
 * - normalizes email (to lowercase and trimmed)
 * - coerces age to 0 if null or negative
 *
 * Non-obvious choices:
 * - We return null to indicate the item should be filtered/skipped (e.g. invalid/missing required fields).
 * - We log at DEBUG for skipped rows so you can inspect issues without failing the job.
 */

@Component
 class CsvPersonProcessor : ItemProcessor<CsvPersonDto, CsvPerson> {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(item: CsvPersonDto): CsvPerson? {
        val name = item.name?.trim().orEmpty()
        val email = item.email?.trim()?.lowercase(Locale.getDefault()).orEmpty()
        var age = item.age ?: 0
        val city = item.city?.trim().takeIf { it?.isNotEmpty() == true }

    
        if(name.isEmpty() || email.isEmpty()) {
            log.debug("Skipping row: ${item.name}, ${item.email}")
            return null
        }

        if(age < 0) {
            log.debug("Negative age encountered for email {} — coercing to 0", email)
            age = 0
        }   
  
        return CsvPerson(
            name = name,
            email = email,
            age = age,
            city = city
        )
    }
}