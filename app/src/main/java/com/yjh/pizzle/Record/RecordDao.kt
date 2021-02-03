package com.yjh.pizzle.Record

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {
    @Query("SELECT * FROM play_record ORDER By recordSec ASC ")
    fun getAll(): List<PlayRecord>

    @Insert
    fun insertAll(vararg record: PlayRecord)

    @Delete
    fun delete(record: PlayRecord)
}