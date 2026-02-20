package com.example.smartkuyo.domain.usecase

import com.example.smartkuyo.domain.model.UserPreferences
import com.example.smartkuyo.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> = preferencesRepository.userPreferences
}
