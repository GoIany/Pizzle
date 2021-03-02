package com.yjh.pizzle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_slide_game.*
import java.io.File
import java.io.ObjectInputStream

class SlideGameActivity() : AppCompatActivity(){

    companion object {
        const val NEW_GAME = 1
        const val CONTINUE_GAME = 2

        const val MODE_NUM = 3
        const val MODE_PIC = 4
    }

    private val IMG_SELECT = 1
    private val IMG_CONTINUE = 2
    private var  selectedPicture : Bitmap? = null
    private lateinit var slicePicture : Array<PuzzlePiece?>
    lateinit var gameData: GameData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_game)

        MobileAds.initialize(this) {}
        adVerticalGameView.loadAd(AdRequest.Builder().build())
        adVerticalGameView.adListener = object: AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
            }
        }

        if(intent.hasExtra("GameState")){

            if (intent.getIntExtra("GameState", NEW_GAME) == Companion.NEW_GAME){

                gameData = GameData()
                if(intent.getIntExtra("GameMode", MODE_NUM) == MODE_NUM){
                    slidePuzzleLayout.setPicture(gameData, selectedPicture)

                }else if(intent.getIntExtra("GameMode", MODE_NUM) == MODE_PIC){
                    lateinit var intent: Intent
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    }else{
                        intent = Intent(Intent.ACTION_GET_CONTENT)
                    }
                    intent.setType("image/*")
                    startActivityForResult(intent, IMG_SELECT)
                }




            }else if(intent.getIntExtra("GameState", NEW_GAME) == Companion.CONTINUE_GAME){

                var file = File(this.filesDir, "GameDataFile")
                //var bInStream = BufferedInputStream(file.inputStream())
                var oInputStream = ObjectInputStream(file.inputStream())
                gameData = oInputStream.readObject() as GameData
                gameData.isContinue = true

                if(gameData.imgUri != null) {
                    Log.d("태그","imgUri != null")
                    var imgUri: Uri = Uri.parse(gameData.imgUri)
                    try {
                        selectedPicture = MediaStore.Images.Media.getBitmap(contentResolver, imgUri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                slidePuzzleLayout.setPicture(gameData, selectedPicture)

            }

        }else{
            Log.d("태그","전달 값 없음")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == IMG_SELECT){
                var imgUrl: Uri? = data?.data
                gameData.imgUri = imgUrl.toString()

                try {
                    selectedPicture = MediaStore.Images.Media.getBitmap(contentResolver, imgUrl)
                    slidePuzzleLayout.setPicture(gameData, selectedPicture)
                }catch (e: Exception){
                    e.printStackTrace()
                }

            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            finish()
        }

    }


    override fun onPause() {
        super.onPause()
        slidePuzzleLayout.pause()
    }

    override fun onResume() {
        super.onResume()
        slidePuzzleLayout.resume()
    }

}