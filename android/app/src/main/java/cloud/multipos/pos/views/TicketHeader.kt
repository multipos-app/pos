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
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import android.transition.Scene
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.graphics.Color;

class TicketHeader (context: Context, attrs: AttributeSet): LinearLayout (context, attrs), PosDisplay, ThemeListener {

	 val headerPrompt = TicketHeaderPrompt ()
	 val headerAlert = TicketHeaderAlert ()
	 var alert = true
	 
	 init {

		  Pos.app.inflater.inflate (R.layout.ticket_header_layout, this);
		  
		  PosDisplays.add (this)

		  message (Jar ()
							.put ("prompt_text", Pos.app.getString ("register_open"))
							.put ("echo_text", ""))
		  
		  Themed.add (this)
	 }

	 override fun message (jar: Jar) {
		  
		  if (alert) {
				
				val scene = Scene (this, headerPrompt)
				val transition = Fade ()
				transition.setDuration (150)
				TransitionManager.go (scene, transition)
				clearAnimation ()
				alert = false
		  }
		  
		  headerPrompt.update (jar)
	 }
	 
	 override fun clear () {
		  
		  animate (headerPrompt)
		  alert = false
		  headerPrompt.clear ()
	 }

	 fun animate (layout: LinearLayout) {
		  
		  val scene = Scene (this, layout)
		  val transition = Fade ()
		  transition.setDuration (150)
		  TransitionManager.go (scene, transition)
		  clearAnimation ()
	 }
	 
	 // Theme impl
	 
	 override fun update (theme: Themes) {

		  headerPrompt.update (theme)
	 }

	 // PosDisplay impl
	 
	 override fun alert (message: String) {

		  if (!alert) {
				
				val scene = Scene (this, headerAlert)
				val transition = Fade ()
				transition.setDuration (150)
				TransitionManager.go (scene, transition)
				clearAnimation ()
				alert = true

				headerAlert.update (message)
		  }
	 }

	 override fun update () {
		  
		  		  message (Jar ()
							.put ("prompt_text", "")
							.put ("echo_text", Pos.app.input.getString ()))
	 }
	 
	 inner class TicketHeaderPrompt (): LinearLayout (Pos.app) {

		  var left: PosText
		  var right: PosText

		  init {

				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
				Pos.app.inflater.inflate (R.layout.ticket_header_prompt, this)
				left = findViewById (R.id.ticket_header_left) as PosText
				right = findViewById (R.id.ticket_header_right) as PosText
		  }

		  fun update (message: Jar) {

				if (message.has ("prompt_text") &&
					 (message.getString ("prompt_text").length > 0)) left.setText (message.getString ("prompt_text").trunc (R.integer.ticket_prompt_max))
				
				if (message.has ("echo_text") &&
					 (message.getString ("echo_text").length > 0)) right.setText (message.getString ("echo_text").trunc (R.integer.ticket_echo_max))
		  }
		  
		  fun update (theme: Themes) {

				var color = Color.BLACK
				if (theme == Themes.Dark) {
					 
					 color = Color.WHITE
				}
					 
				left.setTextColor (color)
				right.setTextColor (color)
		  }

		  fun clear () {
				
		  		left.setText (Pos.app.getString ("register_open"))
				right.setText ("")
		  }
	 }
	 
	 inner class TicketHeaderAlert (): LinearLayout (Pos.app) {

		  var text: PosText

		  init {

				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
				Pos.app.inflater.inflate (R.layout.ticket_header_alert, this)
				text = findViewById (R.id.ticket_header_alert) as PosText
		  }

		  fun update (message: String) {
				
				text.setText (message)
		  }
	 }
}
