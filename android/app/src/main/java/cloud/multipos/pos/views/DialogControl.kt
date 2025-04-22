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
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.animation.Animation.AnimationListener
import java.util.Stack

class DialogControl (): PosDisplay {
	 	 
	 init {

		  dialog = Pos.app.findViewById (R.id.dialog_container)
		  PosDisplays.add (this)
	 }

	 companion object {

		  val duration = 500L
		  val views = Stack <View> ()
		  var inProgress = false
		  
		  lateinit var dialog: LinearLayout
		  
	 	  fun addView (view: View) {

				Logger.d ("dialog control, add view... ${inProgress}")
				
				if (inProgress) {
					 
					 views.push (view)
					 return;
				}
				
				if (dialog.getChildCount () > 0) {
					 
					 dialog.removeAllViews ();
				}

				dialog.addView (view);
				open ()
		  }

		  fun open () {
				
				val animate = TranslateAnimation ((Pos.app.config.getInt ("width") / 2).toFloat (),   // half screen
															 0f,
															 0f,
															 0f)
		  
				animate.setDuration (duration)
				
				dialog.startAnimation (animate)
				dialog.setVisibility (View.VISIBLE)
		  }
		  
		  fun close () {

				inProgress = true
								
				val animate = TranslateAnimation (0f,
															 (Pos.app.config.getInt ("width") / 2).toFloat (),   // half screen
															 0f,
															 0f)
				
				animate.setDuration (duration)
				animate.setAnimationListener (AnimListener ())
				
				dialog.startAnimation (animate)
				dialog.setVisibility (View.INVISIBLE)
		  }
	 
		  fun clear () {
		  
				dialog.removeAllViews ();
				inProgress = false
		  
				while (views.size > 0) {
					 
					 views.pop ()
				}
		  }
	 }
}
