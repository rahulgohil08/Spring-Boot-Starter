package com.world.spring.features.new_csv.controller

import com.world.spring.features.new_csv.service.CsvService
import com.world.spring.shared.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Controller for handling all CSV file uploads and processing
 */
@RestController
@RequestMapping("/api/csv")
@Validated
class CsvController(
    private val csvService: CsvService
) {

    /**
     * Upload and process CSV file with single record processing
     * This method processes each record individually, suitable for smaller files
     */
    @PostMapping("/upload-single")
    fun uploadCsvSingle(@RequestParam("file") file: MultipartFile): ResponseEntity<ApiResponse<Unit>> {
        // Validate file type
        if (!isValidCsvFile(file)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error("File must be a CSV"))
        }

        try {
            val recordCount = csvService.processCsvFileSingle(file)
            return ResponseEntity.ok(
                ApiResponse.success("Successfully processed $recordCount records using single processing")
            )
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error processing CSV file: ${e.message}"))
        }
    }

    /**
     * Upload and process CSV file with batch processing
     * This method processes records in batches, suitable for large files for better performance
     */
    @PostMapping("/upload-batch")
    fun uploadCsvBatch(@RequestParam("file") file: MultipartFile): ResponseEntity<ApiResponse<Unit>> {
        // Validate file type
        if (!isValidCsvFile(file)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error("File must be a CSV"))
        }

        try {
            val recordCount = csvService.processCsvFileBatch(file)
            return ResponseEntity.ok(
                ApiResponse.success("Successfully processed $recordCount records using batch processing")
            )
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error processing CSV file: ${e.message}"))
        }
    }

    /**
     * Upload and process CSV file using Spring Boot Batch
     * This method uses Spring Boot Batch framework for processing large files efficiently
     */
    @PostMapping("/upload-and-run", consumes = ["multipart/form-data"])
    fun uploadCsvWithSpringBatch(@RequestParam("file") file: MultipartFile): ResponseEntity<ApiResponse<Any>> {
        try {
            val result = csvService.processCsvWithBatch(file)
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(data = result))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error processing CSV file with batch: ${e.message}"))
        }
    }

    /**
     * Get the total count of CSV records in the database
     */
    @GetMapping("/count")
    fun getCsvRecordCount(): ResponseEntity<ApiResponse<Int>> {
        try {
            val count = csvService.fetchCountOfRecordsInTheDB()
            return ResponseEntity.ok(
                ApiResponse.success(count, "Total CSV records retrieved successfully")
            )
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving record count: ${e.message}"))
        }
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