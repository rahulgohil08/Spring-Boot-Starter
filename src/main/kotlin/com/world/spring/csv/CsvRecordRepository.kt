package com.world.spring.csv

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for managing CSV records in the database
 */
@Repository
interface CsvRecordRepository : JpaRepository<CsvRecord, Long> {
    // Additional custom query methods can be added here if needed
}