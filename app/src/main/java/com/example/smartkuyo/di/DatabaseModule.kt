package com.example.smartkuyo.di

import android.content.Context
import androidx.room.Room
import com.example.smartkuyo.data.local.db.SmartKuyoDatabase
import com.example.smartkuyo.data.local.db.dao.DeceasedDao
import com.example.smartkuyo.data.local.db.dao.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartKuyoDatabase =
        Room.databaseBuilder(
            context,
            SmartKuyoDatabase::class.java,
            "smart_kuyo.db"
        ).build()

    @Provides
    fun provideDeceasedDao(database: SmartKuyoDatabase): DeceasedDao =
        database.deceasedDao()

    @Provides
    fun providePhotoDao(database: SmartKuyoDatabase): PhotoDao =
        database.photoDao()
}
