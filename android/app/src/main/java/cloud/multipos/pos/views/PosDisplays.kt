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
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.devices.DeviceManager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.view.ViewGroup

class PosDisplays (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {
	 
	 init {

		  for (p in Pos.app.config.getList ("pos_displays")) {
								
				when (p.getString ("type")) {

					 "ticket_display" -> {
					 
						  val ticket = TicketDisplay (context, attrs)
						  displays.add (ticket)
						  addView (ticket)
						  break
					 }
					 
					 "handheld_ticket_display" -> {
					 
						  val ticket = HandheldTicketDisplay (context, attrs)
						  displays.add (ticket)
						  addView (ticket)
						  break
					 }
				}

				if (first == null) {
					 
					  first = p.getString ("name")
				}
		  }
		  		  
		  home ()
	 }

	 companion object {
		  
		  var first: String? = null
		  var displays = mutableListOf <PosDisplay> ()

		  @JvmStatic
		  fun add (posDisplay: PosDisplay) {

				displays.add (posDisplay)
		  }

		  @JvmStatic
		  fun remove (posDisplay: PosDisplay) {

				displays.remove (posDisplay)
		  }

		  @JvmStatic
		  fun clear () {

				for (display in displays) {
					 
					 display.clear ()
				}
		  }
	 
		  fun put (display: PosDisplay) {

				displays.add (display)
		  }
	 	 
		  @JvmStatic
		  fun message (m: String) {
		  
				for (display in displays) {
				
					 display.message (m)
				}
		  }
	 
		  @JvmStatic
		  fun message (value: Jar) {
				
				for (display in displays) {
					 
					 display.message (value)
				}
		  }
		  
		  @JvmStatic
		  fun update () {
				
				for (display in displays) {
					 
					 display.update ()
				}
		  }
		  
		  @JvmStatic
		  fun alert (message: String) {
				
				for (display in displays) {
					 
					 display.alert (message)
				}
		  }
		  		  
		  @JvmStatic
		  fun enter () {
		  
				for (display in displays) {

					 display.enter ()
				}
		  }
		  
		  @JvmStatic
		  fun home () {
				
				// display (first)
		  }
	 }
}
