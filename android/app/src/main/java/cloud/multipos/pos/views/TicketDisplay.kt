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
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.View
import java.util.List
import android.view.Gravity
import android.widget.TextView
import android.graphics.Color

class TicketDisplay (context: Context, attrs: AttributeSet): ListDisplay (context, attrs), PosDisplay, ThemeListener {
		  		  
	 var prompt: RollingTextView?
	 var echo: RollingTextView?
	 var summary: LinearLayout?
	 
	 lateinit var summarySubtotalDesc: PosText
	 lateinit var summarySubtotal: PosText
	 lateinit var summaryTaxDesc: PosText
	 lateinit var summaryTax: PosText
	 lateinit var summaryTotalDesc: PosText
	 lateinit var summaryTotal: PosText

	 val ticket = mutableListOf <Jar> ()
	 val textViews = mutableListOf <PosText> ()

	 init {
		  
		  summary = findViewById (R.id.ticket_summary) as LinearLayout

		  when (Pos.app.config.getString ("locale")) {

				"en_US", "US" -> {
				
		  			 Pos.app.inflater.inflate (R.layout.ticket_summary_tax, summary)
					 
					 summarySubtotalDesc = findViewById (R.id.summary_subtotal_desc) as PosText
					 textViews.add (summarySubtotalDesc)
					 summarySubtotal = findViewById (R.id.summary_subtotal) as PosText
					 textViews.add (summarySubtotal)
					 summaryTaxDesc = findViewById (R.id.summary_tax_desc) as PosText
					 textViews.add (summaryTaxDesc)
					 summaryTax = findViewById (R.id.summary_tax) as PosText
					 textViews.add (summaryTax)
					 summaryTotalDesc = findViewById (R.id.summary_total_desc) as PosText
					 textViews.add (summaryTotalDesc)
					 summaryTotal = findViewById (R.id.summary_total) as PosText
					 textViews.add (summaryTotal)
				}
				else -> {
				
		  			 Pos.app.inflater.inflate (R.layout.ticket_summary_vat, summary)
					 
					 summaryTaxDesc = findViewById (R.id.summary_tax_desc) as PosText
					 textViews.add (summaryTaxDesc)
					 summaryTax = findViewById (R.id.summary_tax) as PosText
					 textViews.add (summaryTax)
					 summaryTotalDesc = findViewById (R.id.summary_total_desc) as PosText
					 textViews.add (summaryTotalDesc)
					 summaryTotal = findViewById (R.id.summary_total) as PosText
					 textViews.add (summaryTotal)
				}
		  }
				
		  prompt = findViewById (R.id.ticket_prompt) as RollingTextView
		  prompt?.layout (R.layout.ticket_prompt)
		  echo = findViewById (R.id.ticket_echo) as RollingTextView
		  echo?.layout (R.layout.ticket_echo)

		  prompt?.setText (Pos.app.getString (R.string.register_open), true)
		  echo?.setText ("", false)
		  
		  Themed.add (this)
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
								
								return TenderLine (context, p)
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

						  echo?.setText (Strings.currency (Pos.app.input.getDouble (), true), true)
					 }
					 
					 else -> { }
				}
		  }
		  else {
								
				if (Pos.app.input.length () > 0) {
					 
					 echo?.setText (Pos.app.input.getString (), true)
				}
		  }
		  				
		  summarySubtotal?.setText (Strings.currency (Pos.app.ticket.getDouble ("sub_total"), false))
		  if (Pos.app.ticket.getDouble ("tax_total_inc") != 0.0) {
				
				summaryTax?.setText (Strings.currency (Pos.app.ticket.getDouble ("tax_total_inc"), false))
		  }
		  else {
					 
				summaryTax?.setText (Strings.currency (Pos.app.ticket.getDouble ("tax_total"), false))
		  }
		  				
		  summaryTotal?.setText (Strings.currency (Pos.app.ticket.getDouble ("total"), false))

		  // update the ticket items
		  
		  updateList ()
	 }
	 
	 override fun view (): View { return this }

	 override fun clear () {
		  
		  clearSelect ()
		  prompt?.setText (trunc (Pos.app.getString (R.string.register_open), R.integer.ticket_prompt_max), true)
		  echo?.setText ("", true)
	 }

	 override fun message (message: String) {
		  
		  prompt?.setText (trunc (message, R.integer.ticket_prompt_max), false)
	 }
	 
	 override fun message (message: Jar) {
		  
		  if (message.has ("prompt_text")) prompt?.setText (trunc (message.getString ("prompt_text"), R.integer.ticket_prompt_max), true)
		  if (message.has ("echo_text")) echo?.setText (trunc (message.getString ("echo_text"), R.integer.ticket_echo_max), true)
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

	 override fun update (theme: Themes) {

		  for (view in textViews) {

				view.setTextColor (Color.parseColor (Pos.app.getString (Themed.fg)))
		  }
		  
		  update ()  // force list redraw
	 }
}
