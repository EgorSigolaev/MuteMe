package com.egorsigolaev.muteme.presentation.extensions

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import com.egorsigolaev.muteme.MuteMeApp

fun View.collapse(coefficient: Long = 1, finishAnimFunc: (() -> Unit)? = null) {
    val initialHeight = this.measuredHeight
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                this@collapse.visibility = View.GONE
            } else {
                this@collapse.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                this@collapse.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean = true
    }

    a.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) { finishAnimFunc?.invoke() }
        override fun onAnimationRepeat(animation: Animation?) {}
    })
    // Collapse speed of 1dp/ms [~123 ms default]
    a.duration = (initialHeight / this.context.resources.displayMetrics.density).toInt()
        .toLong() * coefficient
    Log.d(MuteMeApp.TAG, "### collapse.duration=${a.duration}")
    this.startAnimation(a)
}

fun View.expand(coefficient: Long = 1, finishAnimFunc: (() -> Unit)? = null) {
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((this.parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    this.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = this.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    this.layoutParams.height = 1

    this.visibility = View.VISIBLE
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            this@expand.layoutParams.height = if (interpolatedTime == 1f) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                (targetHeight * interpolatedTime).toInt()
            }
            this@expand.requestLayout()
        }

        override fun willChangeBounds(): Boolean = true
    }

    a.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) { finishAnimFunc?.invoke() }
        override fun onAnimationRepeat(animation: Animation?) {}
    })


    // Expansion speed of 1dp/ms
    a.duration = (targetHeight / this.context.resources.displayMetrics.density).toInt()
        .toLong() * coefficient
    this.startAnimation(a)
}

fun EditText.clear(){
    this.setText("")
}