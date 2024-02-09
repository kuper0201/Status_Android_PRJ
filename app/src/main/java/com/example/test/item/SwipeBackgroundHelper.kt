package com.example.test.item

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.test.R

class SwipeBackgroundHelper {
    companion object {
        @JvmStatic
        fun paintDrawCommandToStart(canvas: Canvas, viewItem: View, @DrawableRes iconResId: Int, backgroundColor: Int, dX: Float) {
            val drawCommand = createDrawCommand(viewItem, iconResId, backgroundColor)
            paintDrawCommand(drawCommand, canvas, dX, viewItem)
        }

        private fun createDrawCommand(viewItem: View, iconResId: Int, backgroundColor: Int): DrawCommand {
            val context = viewItem.context
            var icon = ContextCompat.getDrawable(context, iconResId)
            icon = DrawableCompat.wrap(icon!!).mutate()
            icon.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN)
            val backgroundColor = getBackgroundColor(backgroundColor, viewItem)
            return DrawCommand(icon, backgroundColor)
        }

        private fun getBackgroundColor(firstColor: Int, viewItem: View): Int {
            return ContextCompat.getColor(viewItem.context, firstColor)
        }

        private fun paintDrawCommand(drawCommand: DrawCommand, canvas: Canvas, dX: Float, viewItem: View) {
            drawBackground(canvas, viewItem, dX, drawCommand.backgroundColor)
            drawIcon(canvas, viewItem, dX, drawCommand.icon)
        }

        private fun drawIcon(canvas: Canvas, viewItem: View, dX: Float, icon: Drawable) {
            val topMargin = calculateTopMargin(icon, viewItem)
            icon.bounds = getStartContainerRectangle(viewItem, icon.intrinsicWidth, topMargin, dX)
            icon.draw(canvas)
        }

        private fun getStartContainerRectangle(viewItem: View, iconWidth: Int, topMargin: Int, dx: Float): Rect {
            if(dx < 0) {
                val leftBound = viewItem.right + dx.toInt()
                val rightBound = viewItem.right + dx.toInt() + iconWidth
                val topBound = viewItem.top + topMargin
                val bottomBound = viewItem.bottom - topMargin

                return Rect(leftBound, topBound, rightBound, bottomBound)
            } else {
                val leftBound =  dx.toInt() - iconWidth
                val rightBound =  dx.toInt()
                val topBound = viewItem.top + topMargin
                val bottomBound = viewItem.bottom - topMargin

                return Rect(leftBound, topBound, rightBound, bottomBound)
            }
        }

        private fun calculateTopMargin(icon: Drawable, viewItem: View): Int {
            return (viewItem.height - icon.intrinsicHeight) / 2
        }

        private fun drawBackground(canvas: Canvas, viewItem: View, dX: Float, color: Int) {
            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint.color = color
            val backgroundRectangle = getBackGroundRectangle(viewItem, dX)
            canvas.drawRect(backgroundRectangle, backgroundPaint)
        }

        private fun getBackGroundRectangle(viewItem: View, dX: Float): RectF {
            if(dX < 0) {
                return RectF(viewItem.right.toFloat() + dX, viewItem.top.toFloat(), viewItem.right.toFloat(), viewItem.bottom.toFloat())
            } else {
                return RectF(0f , viewItem.top.toFloat(), dX, viewItem.bottom.toFloat())
            }
        }
    }

    private class DrawCommand internal constructor(internal val icon: Drawable, internal val backgroundColor: Int)

}