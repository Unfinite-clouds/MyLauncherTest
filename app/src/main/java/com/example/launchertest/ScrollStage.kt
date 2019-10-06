package com.example.launchertest

import android.content.ClipData
import android.content.Context
import android.graphics.PointF
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.launchertest.RecyclerViewScroll.Companion.SCROLL_ZONE

class ScrollStage(context: Context) : BaseStage(context), View.OnLongClickListener, View.OnDragListener {
    val FLIP_ZONE = toPx(40).toInt()

    var apps = AppManager.mainScreenApps
    override val stageLayoutId = R.layout.stage_0_main_screen
    lateinit var recyclerView: RecyclerViewScroll
    var widthCell = getPrefs(context).getInt(Preferences.MAIN_SCREEN_WIDTH_CELL, -1)
    var heightCell = getPrefs(context).getInt(Preferences.MAIN_SCREEN_HEIGHT_CELL, -1)

    override fun inflateAndAttach(rootLayout: ViewGroup) {
        super.inflateAndAttach(rootLayout)
        recyclerView = rootLayout.findViewById(R.id.stage_0_recycler)
        recyclerView.adapter = RecyclerListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.apps = apps
        rootLayout.setOnDragListener(this)
    }

    inner class RecyclerListAdapter : RecyclerView.Adapter<AppShortcutHolder>() {
        override fun getItemCount() = apps.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppShortcutHolder {
            val holder = AppShortcutHolder(LayoutInflater.from(context).inflate(R.layout.stage_0_vh, parent, false))
            holder.cell.apply {
                setOnDragListener(this@ScrollStage)
                layoutParams = LinearLayout.LayoutParams(widthCell,heightCell)
            }
            holder.cell.shortcut?.apply {
                setOnLongClickListener(this@ScrollStage)
                setPadding(0)
            }
            return holder
        }

        override fun onBindViewHolder(holder: AppShortcutHolder, position: Int) {
            holder.cell.shortcut?.appInfo = AppManager.getApp(apps[position])!!
            holder.cell.shortcut?.translationX = 0f
        }
    }

    class AppShortcutHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cell = itemView as DummyCell
    }

    override fun onLongClick(v: View?): Boolean {
        if (v is AppShortcut) {
            v.showMenu()
            startDrag(v)
        }
        return true
    }

    private var dragShortcut: AppShortcut? = null
    private var isEnded = false
    private var hasFocus = false
    private var isFirstDrag = true

    private fun startDrag(v: AppShortcut) {
        v.visibility = View.INVISIBLE
        dragShortcut = v
        v.startDrag(ClipData.newPlainText("",""), v.createDragShadow(), Pair(null, v), 0)
    }

    private fun onFocused(event: DragEvent) {
        // it's time to handle this drag event
        hasFocus = true
        isEnded = false
        isFirstDrag = true

        if (dragShortcut != null) {
            // we have started the drag event
            recyclerView.dragStarted(dragShortcut!!.parent as DummyCell)
        } else {
            // drag becomes from other stage
            val state = event.localState as Pair<*, *>
            dragShortcut = state.second as AppShortcut
            recyclerView.dragStartedWithNew(dragShortcut!!.appInfo.id)
        }
    }

    private fun onFocusLost() {
        hasFocus = false
        recyclerView.resetTranslate()
        recyclerView.stopDragScroll()
        dragShortcut?.visibility = View.VISIBLE
        saveData()
        updateView()
    }

    private fun endDrag() {
        isEnded = true
        dragShortcut = null
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        if (v == null)
            return false

        when (event?.action) {

            DragEvent.ACTION_DRAG_STARTED -> {}

            DragEvent.ACTION_DRAG_ENTERED -> {
                if (!hasFocus)
                    onFocused(event)

                if (v is DummyCell) {
                    recyclerView.handleTranslate(v)
                } else if (v is FrameLayout) {
                    recyclerView.resetTranslate()
                }
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                if (isFirstDrag) isFirstDrag = false else dragShortcut?.dismissMenu()

                if (v is DummyCell) {
                    recyclerView.checkAndScroll(toParentCoords(v, event))
                } else if (v is FrameLayout) {
                    // v is root - FrameLayout
                    when {
                        event.x > v.width - SCROLL_ZONE -> recyclerView.startDragScroll(+1)
                        event.x < SCROLL_ZONE -> recyclerView.startDragScroll(-1)
                        event.y > v.height - FLIP_ZONE -> {
                            onFocusLost()
                            launcherViewPager.currentItem = 1
                        }
                        else -> recyclerView.stopDragScroll()
                    }
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {}

            DragEvent.ACTION_DROP -> {
                if (v is DummyCell) {
                    resolvePositions(recyclerView.startPos, recyclerView.destPos)
                } else if (v is FrameLayout) {
                    if (recyclerView.startPos < apps.size)
                        removeApp(recyclerView.startPos)
                }
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                if (!isEnded) {
                    onFocusLost()
                    endDrag()
                }
            }
        }
        return true
    }

    private fun toParentCoords(v: View, event: DragEvent): PointF {
        return PointF(v.left + event.x, v.top + event.y)
    }

    private fun resolvePositions(startPos: Int, destPos: Int) {
        if (startPos == destPos || destPos == -1)
            return
        val direction = if (startPos < destPos) 1 else -1
        val temp = apps[startPos]
        var pos = startPos
        while (pos != destPos) {
            apps[pos] = apps[pos + direction]
            pos+=direction
        }
        apps[destPos] = temp
    }

    private fun removeApp(startPos: Int) {
        apps.removeAt(startPos)
    }

    private fun saveData() {
        AppManager.applyMainScreenChanges(context, apps)
    }

    private fun updateView() {
        recyclerView.adapter?.notifyDataSetChanged()
    }
}