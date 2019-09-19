package com.example.launchertest.try_grid

import android.content.ClipData
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.view.iterator
import com.example.launchertest.AppInfo
import com.example.launchertest.R
import com.example.launchertest.getAllAppsList
import kotlinx.android.synthetic.main.activity_try_grid.*
import kotlin.random.Random


class TryGridActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener, View.OnLongClickListener {
    lateinit var allApps: ArrayList<AppInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_grid)

        allApps = getAllAppsList(this)

        fillGrid(try_grid)
    }

    private fun fillGrid(grid: LauncherScreenGrid) {
        for (i in 0 until (0.5*grid.childCount).toInt()) {
            val img = ImageView(this)
            img.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            img.setOnLongClickListener(this)
            img.setImageDrawable(getAllAppsList(this)[Random.nextInt(getAllAppsList(this).size)].icon)
            grid.addViewTo(img, i/grid.columnCount, i%grid.columnCount)
        }
    }

/*    fun onDrag(view: View?, event: DragEvent?): Boolean {
        if (view !is DummyCell || event == null)
            return false

        val dummyCell = view
        val shortcut = event.localState as ImageView
//        println("view=${dummyCell?.javaClass?.simpleName} ${dummyCell.hashCode()}, event.action=${event.action} event.loacalState=${event.localState.javaClass.simpleName} ${event.localState.hashCode()}")

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                dummyCell.onDragStarted()
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                dummyCell.onDragLocationChanged(event.x, event.y)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                dummyCell.onDragEntered()
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                dummyCell.onDragExited()
            }
            DragEvent.ACTION_DROP -> {
                moveShortcut(shortcut, dummyCell as ViewGroup)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                // back to default state
                dummyCell.onDragEnded()
                endDrag(shortcut)
            }
        }
        return true
    }*/

    override fun onLongClick(view: View?): Boolean {
//        createPopupMenu(view!!)
        startDrag(view!! as ImageView)
        return true
    }

    private fun startDrag(shortcut: ImageView) {
        println("${shortcut.javaClass.simpleName} ${shortcut.hashCode()}")
        shortcut.visibility = View.INVISIBLE
        shortcut.setColorFilter(Color.rgb(181, 232, 255), PorterDuff.Mode.MULTIPLY)
        val data = ClipData.newPlainText("", "")
        val shadowBuilder = View.DragShadowBuilder(shortcut)
        shortcut.startDrag(data, shadowBuilder, shortcut, 0)
    }

    fun createPopupMenu(view: View) {
        val builder = MenuBuilder(view.context)
        val inflater = MenuInflater(view.context)
        inflater.inflate(R.menu.shortcut_popup_menu, builder)
        for (item in builder.iterator()) {
            item.setOnMenuItemClickListener(this)
        }
        val menuHelper = MenuPopupHelper(view.context, builder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        println(item?.title)
        return true
    }

/*
    override fun onContentChanged() {
        super.onContentChanged()
        println("onContentChanged")
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        println("onAttachedToWindow")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("onSaveInstanceState")
    }
*/
}
