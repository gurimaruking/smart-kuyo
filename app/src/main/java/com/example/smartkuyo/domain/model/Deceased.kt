package com.example.smartkuyo.domain.model

import java.time.Instant
import java.time.LocalDate

data class Deceased(
    val id: Long = 0,
    val familyName: String,
    val givenName: String,
    val posthumousName: String? = null,
    val denomination: Denomination,
    val dateOfDeath: LocalDate,
    val dateOfBirth: LocalDate? = null,
    val ageAtDeath: Int? = null,
    val createdAt: Instant = Instant.now()
) {
    val fullName: String get() = "$familyName $givenName"
}
