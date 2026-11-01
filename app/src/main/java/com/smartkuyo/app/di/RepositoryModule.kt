package com.smartkuyo.app.di

import com.smartkuyo.app.data.repository.DeceasedRepositoryImpl
import com.smartkuyo.app.data.repository.PhotoRepositoryImpl
import com.smartkuyo.app.data.repository.PreferencesRepositoryImpl
import com.smartkuyo.app.domain.repository.DeceasedRepository
import com.smartkuyo.app.domain.repository.PhotoRepository
import com.smartkuyo.app.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeceasedRepository(impl: DeceasedRepositoryImpl): DeceasedRepository

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(impl: PhotoRepositoryImpl): PhotoRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
