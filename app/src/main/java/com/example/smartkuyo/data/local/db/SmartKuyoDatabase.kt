package com.example.smartkuyo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartkuyo.data.local.db.dao.DeceasedDao
import com.example.smartkuyo.data.local.db.dao.PhotoDao
import com.example.smartkuyo.data.local.db.entity.DeceasedEntity
import com.example.smartkuyo.data.local.db.entity.PhotoEntity

@Database(
    entities = [DeceasedEntity::class, PhotoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SmartKuyoDatabase : RoomDatabase() {
    abstract fun deceasedDao(): DeceasedDao
    abstract fun photoDao(): PhotoDao
}
