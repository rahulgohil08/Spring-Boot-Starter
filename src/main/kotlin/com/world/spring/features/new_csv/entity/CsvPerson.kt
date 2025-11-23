package com.world.spring.features.new_csv.entity

import jakarta.persistence.*
import java.time.Instant

 

/*
 * CsvPerson entity — maps each CSV row to a persistent table row.
 *
 * Column choices:
 * - name, email, age, city map exactly to CSV fields.
 * - createdAt stores when the record was persisted.
 * - email is not forced unique here to keep behavior simple, but you can add constraints.
 */
@Entity
@Table(name = "csv_person")
data class CsvPerson(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var age: Int = 0,

    @Column(nullable = true)
    var city: String? = null,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
)

