/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import android.content.res.Resources
import android.widget.LinearLayout
import android.graphics.Color
import android.view.View
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.view.MotionEvent

import java.util.ArrayList

abstract class PosSwipeLayout (context: Context, attrs: AttributeSet?): PosLayout (context, attrs), OnTouchListener {
	 
	 var dir = Gravity.START
	 var edge = Gravity.START
	 var gestureDetector: GestureDetector

	 abstract fun swipeLeft ()
	 abstract fun swipeRight ()
	 abstract fun swipeUp ()
	 abstract fun swipeDown ()

	 var swiper: PosLayout?
	 
	 init {

		  gestureDetector = GestureDetector (Pos.app, GestureListener (this))
		  setOnTouchListener (this)
		  swiper = this
	 }

	 /**
	  *
	  * implement swipe
	  *
	  */
	 
	 override fun onTouch (v: View, event: MotionEvent): Boolean {

        return gestureDetector.onTouchEvent (event)
		  
    }
	 override fun dispatchTouchEvent (ev: MotionEvent): Boolean {
		  
        gestureDetector.onTouchEvent (ev) 
		  return super.dispatchTouchEvent (ev)		  
    }

	 inner class GestureListener (val layout: PosSwipeLayout): SimpleOnGestureListener () {
				
		  val SWIPE_DISTANCE_THRESHOLD = 100
		  val SWIPE_VELOCITY_THRESHOLD = 100

		  init { }

        override fun onDown (e: MotionEvent): Boolean {
				
				return true
        }

        override fun onFling (e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

				var distanceX = e2.getX () - e1.getX ()
				var distanceY = e2.getY () - e1.getY ()
				
            if (Math.abs (distanceX) > Math.abs (distanceY) &&
					 Math.abs (distanceX) > SWIPE_DISTANCE_THRESHOLD &&
					 Math.abs (velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					 
					 if (distanceX > 0.0) {
						  
						  swipeRight ()
					 }
                else {

                    swipeLeft ()
                }

					 return true
				}
					 
            return false
        }
    }

	 fun swipe (next: PosLayout) {
		  
        val scene = Scene (swiper, next)
        val slide = Slide (dir)
        slide.setMode (Fade.MODE_IN)
        val transition = slide
        transition.setDuration (500)
        TransitionManager.go (scene, transition)
        clearAnimation ()
	 }
}
