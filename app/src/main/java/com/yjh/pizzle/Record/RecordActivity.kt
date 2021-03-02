package com.yjh.pizzle.Record

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yjh.pizzle.R
import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : AppCompatActivity() {
    var db: RecordDatabase? = null
    var recordList = mutableListOf<PlayRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        db = RecordDatabase.getInstance(this)
        val savedRecord = db!!.recordDao().getAll()
        if(savedRecord.isNotEmpty()){
            recordList.addAll(savedRecord)
        }
        var adapter = RcAdapter(recordList)
        recordRv.adapter = adapter

        recordUndoBtn.setOnClickListener {
            this.finish()
        }
    }
}