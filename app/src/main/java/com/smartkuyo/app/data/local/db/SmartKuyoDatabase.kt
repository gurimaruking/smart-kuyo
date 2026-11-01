package com.smartkuyo.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartkuyo.app.data.local.db.dao.DeceasedDao
import com.smartkuyo.app.data.local.db.dao.PhotoDao
import com.smartkuyo.app.data.local.db.entity.DeceasedEntity
import com.smartkuyo.app.data.local.db.entity.PhotoEntity

@Database(
    entities = [DeceasedEntity::class, PhotoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SmartKuyoDatabase : RoomDatabase() {
    abstract fun deceasedDao(): DeceasedDao
    abstract fun photoDao(): PhotoDao
}
