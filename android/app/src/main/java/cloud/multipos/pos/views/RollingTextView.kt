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

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.View
import java.util.List
import android.widget.TextView
import android.view.Gravity
import android.transition.Scene
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager

class RollingTextView (context: Context, attrs: AttributeSet): LinearLayout (context, attrs) {

	 val textViews = mutableListOf <TicketPromptText> ()
	 val dir = Gravity.BOTTOM
	 val edge = Gravity.TOP
	 
	 var index = 0

	 init {

		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  textViews.add (TicketPromptText (Pos.app))
		  textViews.add (TicketPromptText (Pos.app))
	 }

	 fun layout (layoutID: Int) {

		  for (tv in textViews) {

				tv.layout (layoutID)
		  }
	 }

	 fun setText (text: String, roll: Boolean) {

		  if (roll) {
				
				index = (index + 1) % textViews.size		  		
				textViews.get (index).setText (text)
				
				val end = textViews.get (index)
				val scene = Scene (this, end)
				val slide = Slide (dir)
				slide.setMode (Fade.IN)
				val transition = slide
				transition.setDuration (150)
				TransitionManager.go (scene, transition)
				clearAnimation ()
		  }
		  else {

				textViews.get (index).setText (text)
	
		  }
	 }

	 fun getMaxWidth (): Int {
		  
		  return textViews.get (index).getMaxWidth ()
	 }

	 inner class TicketPromptText (context: Context): LinearLayout (context) {

		  lateinit var textView: TextView

		  init {

				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  }
		  
		  fun layout (layoutID: Int) {
				
				Pos.app.inflater.inflate (layoutID, this)
				textView = findViewById (R.id.pos_text)
		  }

		  fun setText (text: String) {

				textView.setText (text)
		  }
		  
		  fun getMaxWidth (): Int {

				return textView.getMaxWidth ()
		  }
	 }
}
