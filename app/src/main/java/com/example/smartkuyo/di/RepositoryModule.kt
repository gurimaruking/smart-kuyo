package com.example.smartkuyo.di

import com.example.smartkuyo.data.repository.DeceasedRepositoryImpl
import com.example.smartkuyo.data.repository.PhotoRepositoryImpl
import com.example.smartkuyo.data.repository.PreferencesRepositoryImpl
import com.example.smartkuyo.domain.repository.DeceasedRepository
import com.example.smartkuyo.domain.repository.PhotoRepository
import com.example.smartkuyo.domain.repository.PreferencesRepository
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
