package com.smartkuyo.app.data.mapper

import com.smartkuyo.app.data.local.db.entity.DeceasedEntity
import com.smartkuyo.app.domain.model.Deceased
import com.smartkuyo.app.domain.model.Denomination
import java.time.Instant
import java.time.LocalDate

fun DeceasedEntity.toDomain(): Deceased = Deceased(
    id = id,
    familyName = familyName,
    givenName = givenName,
    posthumousName = posthumousName,
    denomination = runCatching { Denomination.valueOf(denominationCode) }
        .getOrDefault(Denomination.NONE),
    dateOfDeath = LocalDate.ofEpochDay(dateOfDeath),
    dateOfBirth = dateOfBirth?.let { LocalDate.ofEpochDay(it) },
    ageAtDeath = ageAtDeath,
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun Deceased.toEntity(): DeceasedEntity = DeceasedEntity(
    id = id,
    familyName = familyName,
    givenName = givenName,
    posthumousName = posthumousName,
    denominationCode = denomination.name,
    dateOfDeath = dateOfDeath.toEpochDay(),
    dateOfBirth = dateOfBirth?.toEpochDay(),
    ageAtDeath = ageAtDeath,
    createdAt = createdAt.toEpochMilli()
)
