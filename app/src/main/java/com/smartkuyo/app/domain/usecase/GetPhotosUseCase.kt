package com.smartkuyo.app.domain.usecase

import com.smartkuyo.app.domain.model.Photo
import com.smartkuyo.app.domain.repository.PhotoRepository
import com.smartkuyo.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val preferencesRepository: PreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Photo>> =
        preferencesRepository.userPreferences.flatMapLatest { prefs ->
            val id = prefs.activeDeceasedId
            if (id != null) {
                photoRepository.getPhotosForDeceased(id)
            } else {
                flowOf(emptyList())
            }
        }
}
