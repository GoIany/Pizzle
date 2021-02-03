package com.yjh.pizzle

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.yjh.pizzle.Record.PlayRecord
import com.yjh.pizzle.Record.RecordDatabase
import java.io.File
import java.io.ObjectOutputStream
import java.util.*

class SlidePuzzleLayout : FrameLayout {

    //var mContext = context
    lateinit var mContext: Context
    lateinit var puzzleView: Array<PuzzlePiece?>
    lateinit var puzzleArray: Array<IntArray>
    var gameData: GameData? = null
    var inGameMenu: InGameMenu? = null
    var selectedPicture: Bitmap? = null

    var isSetPicture = false
    var puzzleCompleted = false
    var puzzleSave = false

    var yNum = 1
    var xNum = 1
    var startX = 0
    var startY = 0
    var pieceWidth = 1

    val pieceEmpty = -1
    val leftTopEmpty = -2
    //이동방향
    val moveNot = -1
    val moveUp = 0
    val moveDown = 1
    val moveLeft = 2
    val moveRight = 3



    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    fun init(context: Context){
        mContext = context
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if(isSetPicture) {
            var location: IntArray = IntArray(2)
            this.getLocationOnScreen(location)
            startX = location[0]
            startY = location[1]

            if(selectedPicture == null){
                inGameMenu = InGameMenu(mContext, true)
            }else {
                inGameMenu = InGameMenu(mContext)
            }
            if(gameData?.isContinue!!){

                this.xNum = gameData?.xNum!!
                this.yNum = gameData?.yNum!!
                pieceWidth = this.width/xNum
                cutPicture(xNum)
                inGameMenu?.puzzleView = puzzleView
                puzzleArray = gameData?.puzzleArray!!
                setPuzzle()

            }else{
                optionDialog()
            }


            isSetPicture = false
        }

    }

    fun setPicture(gameData: GameData, selectedPicture: Bitmap? = null){

        this.gameData = gameData
        this.selectedPicture = selectedPicture
        isSetPicture = true
    }

    fun getPuzzleSize(baseLength: Int, isPuzzleVtical: Boolean = true): Int{
//        var adView = findViewById<AdView>(R.id.adVerticalGameView)
        var height = 0
        var timerHeight = this.width/baseLength
        if((selectedPicture?.height ?: 1) *this.width/ (selectedPicture?.width ?: 1) <= this.height-timerHeight && (selectedPicture != null) ){
            height = selectedPicture!!.height*this.width/selectedPicture!!.width
        }else{
            height = this.height-timerHeight
        }
        return height/(this.width/baseLength)
    }

    fun optionDialog(){
        var minXNum = 3
        var maxXNum = 6
        var xNum = 3

        var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var layout = inflater.inflate(R.layout.dialog_init_options, null)
        var btnMin = layout.findViewById<ImageButton>(R.id.btnInitOptPuzSizeMinus)
        var btnPlu = layout.findViewById<ImageButton>(R.id.btnInitOptPuzSizePlus)
        var tvPuzSiz = layout.findViewById<TextView>(R.id.tvInitOptPuzSize)

        tvPuzSiz.text = "${xNum}x${getPuzzleSize(xNum)}"
        btnMin.setColorFilter(resources.getColor(R.color.optDialogSizeBtnUnClick))
        btnPlu.setColorFilter(resources.getColor(R.color.optDialogSizeBtnClick))

        btnMin.setOnClickListener {
            if(xNum > minXNum){
                xNum -= 1
                tvPuzSiz.text = "${xNum}x${getPuzzleSize(xNum)}"
                btnPlu.setColorFilter(resources.getColor(R.color.optDialogSizeBtnClick))
                if(xNum == minXNum)
                    btnMin.setColorFilter(resources.getColor(R.color.optDialogSizeBtnUnClick))
            }
        }
        btnPlu.setOnClickListener {
            if(xNum < maxXNum){
                xNum += 1
                tvPuzSiz.text = "${xNum}x${getPuzzleSize(xNum)}"
                btnMin.setColorFilter(resources.getColor(R.color.optDialogSizeBtnClick))
                if(xNum == maxXNum)
                    btnPlu.setColorFilter(resources.getColor(R.color.optDialogSizeBtnUnClick))
            }
        }
        var builder = AlertDialog.Builder(mContext, R.style.MyDialogTheme)
        builder.setView(layout)
            .setPositiveButton(mContext.getString(R.string.Ok)){ dialogInterface, i ->

                this.xNum = xNum
                this.yNum = getPuzzleSize(xNum)
                pieceWidth = this.width/xNum
                cutPicture(xNum)
                inGameMenu?.puzzleView = puzzleView
                setPuzzleArray()
                shufflePuzzle()
                setPuzzle()
            }
            .setNegativeButton(mContext.getString(R.string.Cancel)){ dialogInterface, i ->
                (mContext as SlideGameActivity).finish()
            }
        var dialog = builder.create()
        dialog.setOnCancelListener {
            (mContext as SlideGameActivity).finish()
        }
        dialog.show()
    }

    fun cutPicture(xNum:Int){
//        pictureSelected = true

        var yNum = getPuzzleSize(xNum)
        puzzleView = arrayOfNulls<PuzzlePiece>(xNum*yNum)

        var sliceWidth = 0
        var startX = 0
        var startY = 0

        if(selectedPicture != null) {
            sliceWidth = selectedPicture!!.width / xNum
            startY = if (selectedPicture!!.height * (this.width / selectedPicture!!.width) <= this.height-(this.width/xNum))
                (selectedPicture!!.height - yNum * sliceWidth) / 2
            else (selectedPicture!!.height * this.width / selectedPicture!!.width - yNum * sliceWidth) / 2 * selectedPicture!!.width / this.width
        }


        for (yCount in 0 until yNum){
            for(xCount in 0 until xNum){
                puzzleView[xNum*yCount+xCount] = PuzzlePiece(mContext)
                var bitmap : Bitmap? = null
                if(selectedPicture != null){ bitmap = Bitmap.createBitmap(selectedPicture!!, xCount*sliceWidth+startX,yCount*sliceWidth+startY, sliceWidth, sliceWidth)
                }
                puzzleView[xNum*yCount+xCount]?.setPiece(xNum*yCount+xCount+1, bitmap)
            }
        }

        for (i in puzzleView){
            i?.showNum = gameData?.showNum!!
        }

    }

    fun shufflePuzzle(){
        var random = Random()
        // emptyX : 0 ~ xNum-1      emptyY : 0 ~ yNum
        var emptyX = xNum-1
        var emptyY = 0
        var direction = 0
        var shuffleCount = 0
        var shuffle = true

        while (shuffleCount < 500){
            direction = random.nextInt(4)
            when(direction){
                moveUp -> if(emptyY > 0){
                    if(puzzleArray[emptyY-1][emptyX] != leftTopEmpty){
                        puzzleArray[emptyY][emptyX] = puzzleArray[emptyY-1][emptyX]
                        emptyY -= 1
                        shuffleCount += 1
                    }
                }
                moveDown -> if(emptyY < yNum){
                    puzzleArray[emptyY][emptyX] = puzzleArray[emptyY+1][emptyX]
                    emptyY += 1
                    shuffleCount += 1

                }
                moveLeft -> if(emptyX > 0){
                    if(puzzleArray[emptyY][emptyX-1] != leftTopEmpty) {
                        puzzleArray[emptyY][emptyX] = puzzleArray[emptyY][emptyX - 1]
                        emptyX -= 1
                        shuffleCount += 1
                    }
                }
                moveRight -> if (emptyX < xNum-1) {
                    puzzleArray[emptyY][emptyX] = puzzleArray[emptyY][emptyX + 1]
                    emptyX += 1
                    shuffleCount += 1

                }
            }
        }



        while (emptyX < xNum-1){
            puzzleArray[emptyY][emptyX] = puzzleArray[emptyY][emptyX + 1]
            emptyX += 1
            puzzleArray[emptyY][emptyX] = pieceEmpty
        }

        while (emptyY > 0){
            puzzleArray[emptyY][emptyX] = puzzleArray[emptyY-1][emptyX]
            emptyY -= 1
            puzzleArray[emptyY][emptyX] = pieceEmpty
        }

    }

    fun setPuzzle(){

        var frameLayoutParams = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pieceWidth*(yNum+1))
        this.layoutParams = frameLayoutParams

        //var leftTopBackground = View(mContext)
        //leftTopBackground.setBackgroundColor( ContextCompat.getColor(mContext,R.color.slide_puzzle_layout_padding))
        //inGameMenu = InGameMenu(mContext)

        var menuParms = FrameLayout.LayoutParams(pieceWidth*(xNum-1), pieceWidth)
        menuParms.topMargin = 0
        menuParms.leftMargin = 0
        this.addView(inGameMenu, menuParms)

        for (forY in 0..yNum){

            for(forX in 0 until xNum){
                if(puzzleArray[forY][forX] != leftTopEmpty && puzzleArray[forY][forX] != pieceEmpty) {

                    puzzleView[puzzleArray[forY][forX]]?.setOnClickListener {
                        puzzleOnClick(it)
                    }
                    var layoutParams = FrameLayout.LayoutParams(pieceWidth, pieceWidth)
                    layoutParams.topMargin = pieceWidth * forY
                    layoutParams.leftMargin = pieceWidth * forX

                    this.addView(puzzleView[puzzleArray[forY][forX]], layoutParams)
                }
            }
        }

        inGameMenu?.resume(gameData?.playSec)

        puzzleSave = true
    }

    fun puzzleOnClick(puzzleImgView: View){
        //절대좌표 -> 논리좌표
        var loc: IntArray = IntArray(2)
        this.getLocationOnScreen(loc)


        var location = IntArray(2)
        puzzleImgView.getLocationOnScreen(location)
        var x = (location[0]-startX)/pieceWidth
        var y = (location[1]-startY)/pieceWidth
        var direction = checkToMove(x, y)

        if(direction != moveNot) {
            pieceMove(direction, x, y)
            if(!puzzleCompleted) {
                if (checkIsCompleted()) {
                    puzzleCompleted()
                }
            }
        }
    }

    fun checkToMove(x: Int, y: Int): Int{
        //  x: 0 ~ xNum-1     y: 0 ~ yNum
        if(y > 0){
            if(puzzleArray[y-1][x] == pieceEmpty){
                return moveUp
            }
        }
        if(y < yNum){
            if(puzzleArray[y+1][x] == pieceEmpty){
                return moveDown
            }
        }
        if(x > 0){
            if(puzzleArray[y][x-1] == pieceEmpty){
                return moveLeft
            }
        }
        if(x < xNum-1){
            if(puzzleArray[y][x+1] == pieceEmpty){
                return moveRight
            }
        }
        return moveNot
    }

    fun pieceMove(direction: Int, x: Int, y: Int){

        //퍼즐 이미지뷰 이동
        var animDirection = "x"//"translationX"
        var animPosition = 0F

        if(direction == moveUp || direction == moveDown){
            animDirection = "y"//"translationY"
            animPosition = y*pieceWidth.toFloat()
        }else{
            animPosition = x*pieceWidth.toFloat()
        }

        if(direction == moveUp || direction == moveLeft){
            animPosition -= pieceWidth
        }else{
            animPosition += pieceWidth
        }

        ObjectAnimator.ofFloat( puzzleView[puzzleArray[y][x]], animDirection, animPosition).apply {
            duration = 200
            start()
        }

        //퍼즐 배열 이동
        when (direction){
            moveUp ->  puzzleArray[y-1][x] = puzzleArray[y][x]
            moveDown -> puzzleArray[y+1][x] = puzzleArray[y][x]
            moveLeft -> puzzleArray[y][x-1] = puzzleArray[y][x]
            moveRight -> puzzleArray[y][x+1] = puzzleArray[y][x]
        }
        puzzleArray[y][x] = -1

    }

    fun checkIsCompleted(): Boolean{

        for(forY in 1..yNum){
            for(forX in 0 until xNum){
                if(puzzleArray[forY][forX] != forX + (forY-1)*xNum)
                    return false
            }
        }
        puzzleCompleted = true
        return true
    }

    fun puzzleCompleted(){

        var builder = AlertDialog.Builder(mContext, R.style.MyDialogTheme)

        var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var layout = inflater.inflate(R.layout.game_complete_dialog, null)
        var recordSec = layout.findViewById<TextView>(R.id.recordSecTv)
        var recordName = layout.findViewById<EditText>(R.id.recordNameEt)

        recordSec.text = resources.getText(R.string.DialogCompleteRecord).toString() + " " + inGameMenu!!.getPlaySecString(true)

        var file = File(mContext.filesDir, "GameDataFile")
        if(file.exists()){
            file.delete()}

        builder.setView( layout )
            .setPositiveButton(mContext.getString(R.string.Ok)) { dialogInterface, i ->
                if(recordName.text.toString() == ""){
                    recordName.setText("No Name")
                }
                var db = RecordDatabase.getInstance(mContext)
                var record = PlayRecord(
                    0,
                    recordName = recordName.text.toString(),
                    recordSec = inGameMenu!!.playSec
                )
                db?.recordDao()?.insertAll(record)

                var recordList = mutableListOf<PlayRecord>()
                val savedRecord = db!!.recordDao().getAll()
                if(savedRecord.isNotEmpty()){
                    recordList.addAll(savedRecord)
                }

            }
            .setNegativeButton(mContext.getString(R.string.Cancel)) { dialogInterface, i ->
            }
            .show()
    }


    fun setPuzzleArray(){
        //배열 y: 0 ~ yNum   x: 0 ~ xNum-1
        //퍼즐 위치 배열 생성 (여백 = -2)
        puzzleArray = Array<IntArray>(yNum+1) { IntArray(xNum){leftTopEmpty} }

        //비어있는 퍼즐칸 배열 -1로 초기화
        puzzleArray[0][xNum-1] = pieceEmpty
        //퍼즐 1~마지막 초기화
        for(forY in 1..yNum){
            for(forX in 0 until xNum){
                puzzleArray[forY][forX] = forX + (forY-1)*xNum
            }
        }
//        for (forY in 0..yNum){
//            for(forX in 0 until xNum){
//                Log.d("태그", "x$forX , y$forY : ${puzzleArray[forY][forX] }")
//            }
//        }
    }

    fun pause(){
        inGameMenu?.pause()

        if(puzzleSave && !puzzleCompleted) {
            gameData?.puzzleArray = puzzleArray
            gameData?.playSec = inGameMenu?.playSec
            gameData?.xNum = xNum
            gameData?.yNum = yNum
            gameData?.showNum = puzzleView[0]?.showNum!!

            var file = File(mContext.filesDir, "GameDataFile")
            if (!file.exists()) {
                file.createNewFile()
            }
            var oOutputStream = ObjectOutputStream(file.outputStream())
            oOutputStream.writeObject(gameData)
        }
    }

    fun resume(){
//        if(inGameMenu != null)
        inGameMenu?.resume(gameData?.playSec)

//        if(gameData != null) {
//            inGameMenu?.resume(gameData?.playSec)
//        }
    }

}


