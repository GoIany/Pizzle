package com.yjh.pizzle

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.yjh.pizzle.Record.RecordActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity() : AppCompatActivity() {
    private val IMG_SELECT = 1
    private lateinit var  selectedPicture : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GameStartBtn.setOnClickListener {
            var slideGameIntent = Intent(this,SlideGameActivity::class.java)
            slideGameIntent.putExtra("GameState",SlideGameActivity.NEW_GAME)

            var inflater: LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var layout = inflater.inflate(R.layout.dialog_choice_num_pic, null)
            var btnNum = layout.findViewById<ImageButton>(R.id.btnSelectNum)
            var btnPic = layout.findViewById<ImageButton>(R.id.btnSelectPicture)

            var builder = AlertDialog.Builder(this,R.style.MyDialogTheme)
            builder.setView(layout)
                .setNegativeButton(this.getString(R.string.Cancel)){ dialogInterface, i ->
                }
            var dialog = builder.create()

            dialog.show()

            btnNum.setOnClickListener {
                dialog.dismiss()
                slideGameIntent.putExtra("GameMode",SlideGameActivity.MODE_NUM)
                startActivity(slideGameIntent)
            }
            btnPic.setOnClickListener {
                dialog.dismiss()
                slideGameIntent.putExtra("GameMode",SlideGameActivity.MODE_PIC)
                startActivity(slideGameIntent)
            }
        }

        GameContinueBtn.setOnClickListener {
            var slideGameIntent = Intent(this,SlideGameActivity::class.java)
            slideGameIntent.putExtra("GameState",SlideGameActivity.CONTINUE_GAME)
            startActivity(slideGameIntent)
        }

        RecordBtn.setOnClickListener {
            startActivity( Intent(this,
                RecordActivity::class.java) )
        }

        ExitBtn.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        var file = File(this.filesDir, "GameDataFile")
        if(!file.exists()){
            GameContinueBtn.isClickable = false
            GameContinueBtn.setTextColor(resources.getColor(R.color.noContinue))
        }else{
            GameContinueBtn.isClickable = true
            GameContinueBtn.setTextColor(resources.getColor(R.color.basicTextColor))
        }
    }

}