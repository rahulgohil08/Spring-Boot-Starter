package com.world.spring.features.new_csv.repository

import com.world.spring.features.new_csv.entity.CsvPerson
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/*
 * Spring Data repository for CsvPerson.
 * This provides convenient methods for integration tests and querying data.
 */

@Repository
interface CsvPersonRepository : JpaRepository<CsvPerson, Long>