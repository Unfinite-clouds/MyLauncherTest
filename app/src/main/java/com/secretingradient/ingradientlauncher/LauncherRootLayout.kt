package com.secretingradient.ingradientlauncher

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.secretingradient.ingradientlauncher.stage.BaseStage
import com.secretingradient.ingradientlauncher.stage.StageAdapter

class LauncherRootLayout : FrameLayout {
    lateinit var launcherViewPager: ViewPager2
        private set
    lateinit var launcherRecyclerView: RecyclerView
        private set
    val stages = mutableListOf<BaseStage>()
    var dispatchToCurrent = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun initViewPager(viewPager: ViewPager2) {
        launcherViewPager = viewPager
        launcherViewPager.adapter = StageAdapter(this)
        launcherRecyclerView = launcherViewPager[0] as RecyclerView
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // dispatch to proper itemView - it will current viewHolder.itemView
        if (dispatchToCurrent) {
            // attention! when we do that way - we ignore launcherVP scroll listener (for scroll up/down)
            return getCurrentItemView().stageRootLayout.dispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun getCurrentItemView(): StageAdapter.StageHolder {
        return launcherRecyclerView.findViewHolderForLayoutPosition(launcherViewPager.currentItem) as StageAdapter.StageHolder
    }
}