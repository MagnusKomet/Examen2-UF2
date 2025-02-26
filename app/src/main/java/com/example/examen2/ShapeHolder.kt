package com.example.examen2


import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.drawable.ShapeDrawable

/**
 *
 * A data structure that holds a Shape and various properties that can be used to define
 *
 * how the shape is drawn.
 *
 */
class ShapeHolder(var shape: ShapeDrawable) {
    var x: Float = 0f
    var y: Float = 0f

    var color: Int = 0
        set(value) {
            shape.paint.color = value

            field = value
        }

    var gradient: RadialGradient? = null

    private var alpha = 1f

    var paint: Paint? = null

    fun setAlpha(alpha: Float) {
        this.alpha = alpha

        shape.alpha = ((alpha * 255f) + .5f).toInt()
    }

    var width: Float
        get() = shape.shape.width
        set(width) {
            val s = shape.shape

            s.resize(width, s.height)
        }

    var height: Float
        get() = shape.shape.height
        set(height) {
            val s = shape.shape

            s.resize(s.width, height)
        }
}