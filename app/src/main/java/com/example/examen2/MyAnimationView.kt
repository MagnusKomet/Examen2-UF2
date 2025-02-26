package com.example.examen2


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import kotlin.random.Random

class MyAnimationView(context: Context?) : View(context) {
    val balls: ArrayList<ShapeHolder> = ArrayList()
    var animation: AnimatorSet? = null

    init {
        // Animate background color
        // Note that setting the background color will automatically invalidate the
        // view, so that the animated color, and the bouncing balls, get redisplayed on
        // every frame of the animation.
        val colorAnim: ValueAnimator =
            ObjectAnimator.ofInt(this, "backgroundColor", MainActivity.GREEN, MainActivity.CYAN)
        colorAnim.setDuration(3000)
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN &&
            event.action != MotionEvent.ACTION_MOVE
        ) {
            return false
        }
        val newBall: ShapeHolder = addBall(event.x, event.y)
        // Bouncing animation with squash and stretch
        val startY: Float = height - 50f
        val endY = height - height + 50f
        val h = height.toFloat()
        val duration = 1000
        val bounceAnim: ValueAnimator = ObjectAnimator.ofFloat(newBall, "y", startY, endY)
        bounceAnim.setDuration(duration.toLong())
        bounceAnim.interpolator = AccelerateInterpolator()

        val squashAnim2: ValueAnimator = ObjectAnimator.ofFloat(
            newBall,
            "width",
            newBall.width,
            newBall.width + 50
        )
        squashAnim2.setDuration((duration / 4).toLong())

        val stretchAnim1: ValueAnimator = ObjectAnimator.ofFloat(
            newBall, "y", endY,
            endY + 25f
        )
        stretchAnim1.setDuration((duration / 4).toLong())
        stretchAnim1.repeatCount = 1
        stretchAnim1.interpolator = DecelerateInterpolator()
        stretchAnim1.repeatMode = ValueAnimator.REVERSE

        // Sequence the down/squash&stretch/up animations
        val bouncer = AnimatorSet()
        bouncer.play(bounceAnim).before(squashAnim2)

        // Fading animation - remove the ball when the animation is done
        val fadeAnim: ValueAnimator = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f)
        fadeAnim.setDuration(250)
        fadeAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                balls.remove((animation as ObjectAnimator).target)
            }
        })
        // Sequence the two animations to play one after the other
        val animatorSet = AnimatorSet()
        animatorSet.play(bouncer).before(fadeAnim)
        // Start the animation
        animatorSet.start()
        return true
    }

    private fun addBall(x: Float, y: Float): ShapeHolder {
        val circle = OvalShape()
        circle.resize(50f, 50f)
        val drawable = ShapeDrawable(circle)
        val shapeHolder: ShapeHolder = ShapeHolder(drawable)
        shapeHolder.x = (x - 25f)
        shapeHolder.y = (y - 25f)
        val paint = drawable.paint //new Paint(Paint.ANTI_ALIAS_FLAG);

        val color : Int
        val darkColor : Int

        if(Random.nextBoolean()){
            color = -0x1000000 or (255 shl 16) or 0 or 0
            darkColor = -0x1000000 or (255 / 4 shl 16) or 0 or 0
        }
        else{
            color = -0x1000000 or 0 or 0 or 255
            darkColor = -0x1000000 or 0 or 0 or 255 / 4
        }

        val gradient = RadialGradient(
            37.5f, 12.5f,
            50f, color, darkColor, Shader.TileMode.CLAMP
        )
        paint.setShader(gradient)
        shapeHolder.paint = paint
        balls.add(shapeHolder)
        return shapeHolder
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("OnDraw", "OnDraw")
        for (i in balls.indices) {
            val shapeHolder: ShapeHolder = balls[i] as ShapeHolder
            canvas.save()
            canvas.translate(shapeHolder.x, shapeHolder.y)
            shapeHolder.shape.draw(canvas)
            canvas.restore()
        }
    }

}
