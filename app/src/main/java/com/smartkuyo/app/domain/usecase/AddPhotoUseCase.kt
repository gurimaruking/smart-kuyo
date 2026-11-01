package com.smartkuyo.app.domain.usecase

import com.smartkuyo.app.domain.model.Photo
import com.smartkuyo.app.domain.repository.PhotoRepository
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
