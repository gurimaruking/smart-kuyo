package com.smartkuyo.app.data.repository

import com.smartkuyo.app.data.local.db.dao.DeceasedDao
import com.smartkuyo.app.data.mapper.toDomain
import com.smartkuyo.app.data.mapper.toEntity
import com.smartkuyo.app.domain.model.Deceased
import com.smartkuyo.app.domain.repository.DeceasedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeceasedRepositoryImpl @Inject constructor(
    private val deceasedDao: DeceasedDao
) : DeceasedRepository {

    override fun getAllDeceased(): Flow<List<Deceased>> =
        deceasedDao.getAllDeceased().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getDeceasedById(id: Long): Flow<Deceased?> =
        deceasedDao.getDeceasedById(id).map { it?.toDomain() }

    override suspend fun insertDeceased(deceased: Deceased): Long =
        deceasedDao.insertDeceased(deceased.toEntity())

    override suspend fun updateDeceased(deceased: Deceased) =
        deceasedDao.updateDeceased(deceased.toEntity())

    override suspend fun deleteDeceased(deceased: Deceased) =
        deceasedDao.deleteDeceased(deceased.toEntity())
}
