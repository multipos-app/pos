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
import android.view.View
import java.util.List
import android.view.Gravity
import android.widget.TextView

class HandheldTicketDisplay (context: Context, attrs: AttributeSet): ListDisplay (context, attrs), PosDisplay {
		  		  
	 // lateinit var prompt: RollingTextView?
	 // lateinit var echo: RollingTextView?
	 val ticket = mutableListOf <Jar> ()

	 init {
		  
		  // prompt = findViewById (R.id.ticket_prompt) as RollingTextView
		  // prompt?.layout (R.layout.ticket_prompt)
		  // echo = findViewById (R.id.ticket_echo) as RollingTextView
		  // echo?.layout (R.layout.ticket_echo)

		  // prompt?.setText (Pos.app.getString (R.string.register_open), true)
		  // echo?.setText ("", false)
	 }

	 /**
	  *
	  * ListDislay implementation
	  *
	  */
	 
	 override fun list (): MutableList <Jar> {

		  val items = mutableListOf <Jar> ()

		  for (item in Pos.app.ticket.items) {
				
				items.add (item)
		  }

		  for (tender in Pos.app.ticket.tenders) {

				items.add (tender)
		  }

		  return items
	 }
	 
	 override fun layout (): String {

		  return "ticket"
	 }

	 override fun listEntry (pos: Int, view: View): View {
		  				
		  if (list.get (pos) is Jar) {

					 var p = list.get (pos)
					 
					 when (p) {
						  
						  is TicketItem -> {

								return ItemLine (context, p, pos, this)
						  }
						  
						  is TicketItemAddon -> {
								
								return ItemAddonLine (context, p)
						  }
						  
						  is TicketTender -> {
								
								return TenderLine (context, p, Pos.app.ticket)
						  }
						  
						  else -> { }
					 }
		  }
		  return view
	 }

	 /**
	  *
	  * Dislay implementation
	  *
	  */
	 
	 override fun update () {
		  
		  if (getVisibility () == View.INVISIBLE) {
				
				return
		  }
		  
		  if ((Pos.app.controls.size > 0) && (Pos.app.controls.peek () is InputListener)) {

				val control = Pos.app.controls.peek () as InputListener

				when (control.type ()) {

					 "currency" -> {

						  // echo?.setText (Pos.app.input.getDouble ().currency (true))
					 }
					 
					 else -> { }
				}
		  }
		  else {
								
				if (Pos.app.input.length () > 0) {
					 
					 // echo?.setText (Pos.app.input.getString (), true)
				}
		  }
		  				
		  // update the ticket items
		  
		  updateList ()
	 }
	 
	 override fun clear () {
		  
		  clearSelect ()
		  // prompt?.setText (trunc (Pos.app.getString (R.string.register_open), R.integer.ticket_prompt_max), true)
		  // echo?.setText ("", true)
	 }

	 override fun message (message: String) {
		  
		  // prompt?.setText (trunc (message, R.integer.ticket_prompt_max), false)
	 }
	 
	 override fun message (message: Jar) {
		  
		  // if (message.has ("prompt_text")) prompt?.setText (trunc (message.getString ("prompt_text"), R.integer.ticket_prompt_max), true)
		  // if (message.has ("echo_text")) echo?.setText (trunc (message.getString ("echo_text"), R.integer.ticket_echo_max), true)
	 }

	 fun trunc (text: String, id: Int): String {
				
		  val max = Pos.app.getInt (id)
				
		  if (text.length > max) {
				
				return text.substring (0, max - 3) + "...";
		  }
		  else {
				
				return text
		  }
	 }		
}
