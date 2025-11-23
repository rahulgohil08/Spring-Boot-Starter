package com.world.spring.features.new_csv.request

/*
 * DTO read by the FlatFileItemReader.
 *
 * Using a DTO keeps the item reader/processor/writer decoupled from the JPA entity,
 * which helps if we want to validate/transform before persistence.
 */
data class CsvPersonDto(
    var name: String? = null,
    var email: String? = null,
    var age: Int? = null,
    var city: String? = null
)
