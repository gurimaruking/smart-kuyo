package com.example.smartkuyo.domain.usecase

import com.example.smartkuyo.domain.model.Deceased
import com.example.smartkuyo.domain.repository.DeceasedRepository
import com.example.smartkuyo.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveDeceasedUseCase @Inject constructor(
    private val deceasedRepository: DeceasedRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(deceased: Deceased): Long {
        val id = if (deceased.id == 0L) {
            deceasedRepository.insertDeceased(deceased)
        } else {
            deceasedRepository.updateDeceased(deceased)
            deceased.id
        }
        preferencesRepository.updateActiveDeceasedId(id)
        return id
    }
}
