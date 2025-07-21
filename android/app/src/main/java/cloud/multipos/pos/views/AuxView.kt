/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.animation.Animation.AnimationListener

class AuxView (context: Context, attrs: AttributeSet): PosSwipeLayout (context, attrs), ThemeListener {

	 lateinit var auxLayout: LinearLayout

	 companion object {
		  
		  lateinit var auxView: AuxView 
		  lateinit var swipeListener: SwipeListener
	 }

	 init {
		  
		  auxView = this
		  Themed.add (this)
	 }
	 
	 // Swipe impl
	 
	 val duration = 250L

	 fun show () {

		  Pos.app.rootView.setVisibility (View.INVISIBLE)
		  val animate = TranslateAnimation ((Pos.app.config.getInt ("width")).toFloat (),
														 0f,
														 0f,
														 0f)
		  
		  animate.setDuration (duration)
		  
		  this.startAnimation (animate)
		  this.setVisibility (View.VISIBLE)
	 }

	 fun hide () {
		  
		  val animate = TranslateAnimation ((Pos.app.config.getInt ("width")).toFloat (), 
														 0f,
														 0f,
														 0f)
		  
		  animate.setDuration (duration)
		  DialogControl.inProgress = true
	  	  animate.setAnimationListener (AnimListener ())

		  Pos.app.rootView.startAnimation (animate)
		  Pos.app.rootView.setVisibility (View.VISIBLE)
		  setVisibility (View.INVISIBLE)
		  removeAllViews ()
	 }
	 
	 override fun swipeLeft () {

		  hide ()
	 }

	 // Theme impl
	 
	 override fun update (theme: Themes) {
		  
		  when (theme) {
					 
				Themes.Light -> {
						  
					 setBackgroundResource (R.color.white)
				}
					 
				Themes.Dark -> {
						  
					 setBackgroundResource (R.color.dark_bg)
				}
		  }
	 }
	 
}
