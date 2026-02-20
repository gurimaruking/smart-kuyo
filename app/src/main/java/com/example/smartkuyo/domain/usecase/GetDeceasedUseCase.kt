package com.example.smartkuyo.domain.usecase

import com.example.smartkuyo.domain.model.Deceased
import com.example.smartkuyo.domain.repository.DeceasedRepository
import com.example.smartkuyo.domain.repository.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetDeceasedUseCase @Inject constructor(
    private val deceasedRepository: DeceasedRepository,
    private val preferencesRepository: PreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Deceased?> =
        preferencesRepository.userPreferences.flatMapLatest { prefs ->
            val id = prefs.activeDeceasedId
            if (id != null) {
                deceasedRepository.getDeceasedById(id)
            } else {
                flowOf(null)
            }
        }
}
