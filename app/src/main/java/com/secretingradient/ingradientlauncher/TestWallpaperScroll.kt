package com.secretingradient.ingradientlauncher

import android.app.WallpaperManager
import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class TestWallpaperScroll : HorizontalScrollView {
    private var maxScrollX: Int = 0
    private var maxOverscroll = 12

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        maxScrollX = getChildAt(0).width - width
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        WallpaperManager.getInstance(context).setWallpaperOffsets(this.windowToken, (scrollX + maxOverscroll) / (maxScrollX + maxOverscroll * 2f), 0f)
    }
}