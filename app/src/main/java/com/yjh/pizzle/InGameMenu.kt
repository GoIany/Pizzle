package com.yjh.pizzle

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.ingame_menu.view.*
import java.util.*

class InGameMenu: LinearLayout {
    lateinit var mContext : Context
    lateinit var puzzleView: Array<PuzzlePiece?>
    private var timer: Timer? = null
    var playSec = 0
    var isNumAndInit = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, isNum: Boolean) : super(context){
        init(context, isNum)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(isNumAndInit){
            (this.getChildAt(0) as LinearLayout).layoutParams = LayoutParams((this.getChildAt(0) as LinearLayout).width, (this.getChildAt(0) as LinearLayout).height)
            (numShowBtn.parent as ViewGroup).removeView(numShowBtn)
            isNumAndInit = false
        }

    }

    fun init(context: Context, isNum: Boolean = false){
        mContext = context
        var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.ingame_menu, this, true)
        this.gravity = Gravity.CENTER_VERTICAL
        this.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))

        numShowBtn.setOnClickListener {
            for (i in puzzleView){
                i?.showNum = !(i?.showNum!!)
            }
            numShowBtn.setImageResource(if (puzzleView[0]?.showNum == true) R.drawable.numbox_show_btn else R.drawable.numbox_not_btn)
        }
        isNumAndInit = isNum

    }

    fun getPlaySecEnd(): Int{
        timer?.cancel()
        return playSec
    }

    fun getPlaySecString(end: Boolean = false): String{
        if(end)
            timer?.cancel()
        var min = if (playSec/60/10 <1) "0"+(playSec/60).toString() else (playSec/60).toString()
        var sec = if (playSec%60/10 <1) "0"+(playSec%60).toString() else (playSec%60).toString()
        return "$min : $sec"
    }

    //화면 내려갈시 타이머 정지
    fun pause(){
        timer?.cancel()
    }

    fun resume(setSec: Int?){
        if (setSec != null) {
            playSec = setSec

            timerTv.text = getPlaySecString()
            timer = kotlin.concurrent.timer(period = 1000, initialDelay = 1000, daemon = false) {
                playSec += 1

                Handler(Looper.getMainLooper()).post {
                    timerTv.text = getPlaySecString()
                }
            }
        }
    }


}