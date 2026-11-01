package com.smartkuyo.app.domain.repository

import com.smartkuyo.app.domain.model.Deceased
import kotlinx.coroutines.flow.Flow

interface DeceasedRepository {
    fun getAllDeceased(): Flow<List<Deceased>>
    fun getDeceasedById(id: Long): Flow<Deceased?>
    suspend fun insertDeceased(deceased: Deceased): Long
    suspend fun updateDeceased(deceased: Deceased)
    suspend fun deleteDeceased(deceased: Deceased)
}
