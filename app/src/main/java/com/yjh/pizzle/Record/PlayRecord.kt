package com.yjh.pizzle.Record

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_record")
data class PlayRecord(
    @PrimaryKey(autoGenerate = true) val recordKey:Long,
    var recordName: String = "사진",
    var recordSec: Int
)
