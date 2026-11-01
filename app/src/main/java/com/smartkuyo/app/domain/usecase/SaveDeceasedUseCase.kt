package com.smartkuyo.app.domain.usecase

import com.smartkuyo.app.domain.model.Deceased
import com.smartkuyo.app.domain.repository.DeceasedRepository
import com.smartkuyo.app.domain.repository.PreferencesRepository
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
