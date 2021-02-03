package com.yjh.pizzle.Record

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [PlayRecord::class], version = 1, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        private var instance: RecordDatabase? = null

        @Synchronized
        fun getInstance(context: Context): RecordDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordDatabase::class.java,
                    "database-record"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}

