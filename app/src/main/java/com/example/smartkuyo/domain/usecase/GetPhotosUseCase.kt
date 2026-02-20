package com.example.smartkuyo.domain.usecase

import com.example.smartkuyo.domain.model.Photo
import com.example.smartkuyo.domain.repository.PhotoRepository
import com.example.smartkuyo.domain.repository.PreferencesRepository
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
