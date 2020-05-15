package ru.skillbranch.gameofthrones.ui.custom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

// 32:52
class ItemDivider : RecyclerView.ItemDecoration() {
    companion object {
        private val DIVIDER_COLOR = Color.parseColor("#E1E1E1")
    }

    private val _paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = DIVIDER_COLOR
        // 0f значит, что при любой плотности пикселей, ширина линии будет всегда составлять 1 пиксель
        strokeWidth = 0f
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // отступ, чтобы divider не проходил под аватаром
        val left = parent.context.dpToPx(72)
        val right = parent.right.toFloat()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val bottom = child.bottom.toFloat()
            c.drawLine(left, bottom, right, bottom, _paint)
        }
    }
}