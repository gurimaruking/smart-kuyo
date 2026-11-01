package com.smartkuyo.app.domain.usecase

import com.smartkuyo.app.domain.model.UserPreferences
import com.smartkuyo.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> = preferencesRepository.userPreferences
}
