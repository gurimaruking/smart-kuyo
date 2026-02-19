package com.example.smartkuyo.domain.usecase

import com.example.smartkuyo.domain.model.Photo
import com.example.smartkuyo.domain.repository.PhotoRepository
import javax.inject.Inject

class AddPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(deceasedId: Long, uri: String, displayOrder: Int): Long =
        photoRepository.insertPhoto(
            Photo(
                deceasedId = deceasedId,
                uri = uri,
                displayOrder = displayOrder
            )
        )
}
