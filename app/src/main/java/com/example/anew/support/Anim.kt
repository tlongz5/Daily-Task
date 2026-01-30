package com.example.anew.support

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator

fun animProgress(
    progress: CircularProgressIndicator, tvPercent: TextView?, oldValue: Int,
    newValue: Int
){
    val animation = ObjectAnimator.ofInt(progress,"progress",oldValue,newValue)
    animation.apply {
        duration = 1500
        interpolator = DecelerateInterpolator()
        if(tvPercent!=null){
            animation.addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                tvPercent.text = "$value%"
            }
        }

        start()
    }
}


fun View.animCb(isChecked: Boolean){
    if (isChecked) {
        //zoom and show
        this.animate()
            .scaleX(1f).scaleY(1f)
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(OvershootInterpolator(2f))
            .start()
    } else {
        // zoom and hide
        this.animate()
            .scaleX(0f).scaleY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(AnticipateInterpolator(2f))
            .start()
    }
}