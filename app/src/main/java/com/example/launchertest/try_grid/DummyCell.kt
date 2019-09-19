package com.example.launchertest.try_grid

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.example.launchertest.R


class DummyCell : LinearLayout, DragListener {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    lateinit var position: Point
    private val bgcolor = Color.argb(40,0,0,0)
    val slideAnimation = object : Runnable {
        override fun run() {
            println("slideAnimation")
        }
    }

    init {
        clipChildren = false
        setBackgroundColor(bgcolor)
    }


    override fun onDragStarted() {

    }

    override fun onDragEntered() {
        setBackgroundResource(R.drawable.bot_gradient)

        if (childCount != 0) {
            println("anim1_start")
            Handler().postDelayed(slideAnimation, 1500)

//            val anim = ObjectAnimator.ofFloat(this.getChildAt(0), View.TRANSLATION_Y, toPx(-30))
//            anim.duration = 300
//            anim.start()
        }
    }

    override fun onDragLocationChanged(x: Float, y: Float){

    }

    override fun onDragExited() {
        setBackgroundColor(bgcolor)
    }

    override fun onDragEnded() {
        setBackgroundColor(bgcolor)
    }

}