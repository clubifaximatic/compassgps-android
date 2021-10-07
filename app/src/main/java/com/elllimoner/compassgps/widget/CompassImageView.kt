package com.elllimoner.compassgps.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator

class CompassImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private var nextRotation: Float = 0f

    fun setCourse(course: Float) {
        nextRotation = (if (course > 180) course - 360 else course).unaryMinus()
        startAnimation()
    }

    private fun startAnimation() {
        val degrees = Math.abs(rotation - nextRotation).toLong()
        if (degrees > 180) {
            rotation = if (rotation > 0) rotation - 360 else rotation + 360
        }

        val animator = ObjectAnimator.ofFloat(this, "rotation", nextRotation)
        animator.duration = 150 + (180 - (degrees % 180))
        animator.interpolator = LinearInterpolator()
        animator.setAutoCancel(true)
        animator.start()
    }
}

