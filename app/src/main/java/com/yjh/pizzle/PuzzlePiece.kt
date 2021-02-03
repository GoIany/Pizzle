package com.yjh.pizzle

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity


class PuzzlePiece : androidx.appcompat.widget.AppCompatTextView {

    var piecePaint = Paint()
    var numPaint = Paint()
    var numBoxPaint = Paint()
    var bitmap: Bitmap? = null
    var initRect = true
    private lateinit var drawRect: Rect
    private lateinit var numBoxRect: Rect

    var rectX = 1F
    var rectY = 1F
    lateinit var number: String
    var showNum = false
        @SuppressLint("RestrictedApi")
        set(value) {
            if(field != value){
                field = value
                invalidate()
            }
        }


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)

    }

    fun init(context: Context){
        //this.isDrawingCacheEnabled = true
        piecePaint.maskFilter = EmbossMaskFilter(FloatArray(3) { i -> 1F} , 0.5f, 5F, 5F);
        numPaint.isAntiAlias = true
        numBoxPaint.maskFilter = EmbossMaskFilter(FloatArray(3) { i -> 1F} , 0.5f, 5F, 5F);

    }

    @SuppressLint("RestrictedApi")
    fun setPiece(n: Int, bitmap: Bitmap? = null){
        this.bitmap = bitmap
        number = n.toString()
        if(bitmap == null){
            this.text = number
            setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM)
            this.gravity = Gravity.CENTER
            numBoxPaint.color = context.resources.getColor(R.color.pieceNum)
        }else{
            numBoxPaint.color = context.resources.getColor(R.color.pieceNumBox)
        }
    }

    override fun onDraw(canvas: Canvas?) {

        if(initRect){
            drawRect = Rect(0, 0, this.width, this.height)
            numBoxRect = Rect()
            var numRect = Rect()
            numPaint.textSize = this.width.toFloat() / 2

            numPaint.getTextBounds("88", 0, "88".length, numBoxRect)
            numPaint.getTextBounds(number, 0, number.length, numRect)
            var margin = 10F
            rectX = (this.width-numBoxRect.width()/2-numRect.width()/2)-margin
            rectY = this.height-margin

            numBoxRect.set((this.width-numBoxRect.width()-margin*2).toInt(), (this.height-numBoxRect.height()-margin*2).toInt(), this.width, this.height)


            initRect = false
        }

        if (bitmap != null){

            canvas?.drawBitmap(bitmap!!, null, drawRect, piecePaint)

            if(showNum){
                canvas?.drawRect(numBoxRect, numBoxPaint)
                canvas?.drawText(number, rectX, rectY, numPaint)
            }
        }else{
            canvas?.drawRect(drawRect, numBoxPaint)
        }
        super.onDraw(canvas)
    }

}