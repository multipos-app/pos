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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.util.SparseArray

class SettleTicketsView (val listener: InputListener, title: String, val tickets: ArrayList <Jar>): PosLayout (Pos.app, null), ThemeListener {

	 lateinit var ticketReceiptText: PosText
	 lateinit var tipInput: PosText
	 lateinit var auxLayout: LinearLayout
	 lateinit var ticketsList: LinearLayout
	 lateinit var layout: LinearLayout
	 
	 var curr = -1
	 var currTip = 0
	 val keyValues = SparseArray <String> ()
	 val ticketViews = mutableListOf <SettleTicketLine> ()
	 
	 init {
		  
		  auxLayout = Pos.app.findViewById (R.id.aux_container) as LinearLayout
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  layout = Pos.app.inflater.inflate (R.layout.settle_tickets_layout, auxLayout) as LinearLayout

		  // get the receipt layout
		  
		  ticketReceiptText = layout.findViewById (R.id.ticket_receipt_text) as PosText
		  ticketReceiptText.setTypeface (Views.receiptFont ())
		  
		  // get the ticket list layout

		  ticketsList = Pos.app.findViewById (R.id.tickets_list) as LinearLayout
		  		  
		  tipInput = layout.findViewById (R.id.settle_ticket_tip_input) as PosText

		  initKeys ()

		  var update = layout.findViewById (R.id.settle_tickets_update) as PosButton
		  update.setOnClickListener {

				render ()
		  }
		  
		  var complete = layout.findViewById (R.id.settle_tickets_complete) as PosButton
		  complete.setOnClickListener {

				Pos.app.auxView.hide ()
				listener.accept (Jar ().put ("tickets", tickets))
		  }
		  
		  // render the tickets
		  		  
		  render ()

		  Themed.add (this)
		  Pos.app.auxView.show ()
	 }

	 // draw the tickets
	 
	 fun render () {

		  ticketsList.removeAllViews ()
		  
		  var i = 0
		  for (ticket in tickets) {

				var ticketLine = SettleTicketLine (ticket, i)

				if (curr == i) {

					 ticketLine.setBackgroundResource (Themed.selectBg)
				}
				
				ticketsList.addView (ticketLine)
				ticketViews.add (ticketLine)
				i ++
		  }
		  
		  tipInput?.text = tip ()
	 }

	 // setup the keyboard keys
	 
	 fun initKeys () {
		  
		  val keys = listOf (R.id.keyboard_1,
									R.id.keyboard_2,
									R.id.keyboard_3,
									R.id.keyboard_4,
									R.id.keyboard_5,
									R.id.keyboard_6,
									R.id.keyboard_7,
									R.id.keyboard_8,
									R.id.keyboard_9,
									R.id.keyboard_0)
				
		  for (k in keys) {
				
				var button = layout.findViewById (k) as PosButton?
				button?.setOnClickListener () {
					 					 
					 if (curr >= 0) {

						  currTip = (currTip * 10) + button.getText ().toString ().toInt ()
						  tip ()
					 }
				}
		  }

		  var del = layout.findViewById (R.id.keyboard_del) as PosButton?
		  del?.setOnClickListener {

				if (currTip > 0) {

					 currTip /= 10
					 tip ()
				}
		  }
				
		  var enter = layout.findViewById (R.id.keyboard_enter) as PosButton?
		  enter?.setOnClickListener {

				if (curr < (tickets.size - 1)) {

					 curr ++
					 currTip = 0
					 render ()
				}
		  }
	 }

	 fun tip (): String {

		  var t = "0.00"
		  
		  if (curr >= 0) {
				
				var t = (currTip.toDouble () / 100.0).currency ()
				
				tickets [curr].put ("tip", t)
				ticketViews [curr].tip.text = t
				tipInput.text = t
		  }
	 
		  return (t)
	 }

	 // ticket line class
	 
	 inner class SettleTicketLine (ticket: Jar, i: Int): LinearLayout (Pos.app) {

		  lateinit var tip: PosText
		  
		  init {
				
				val layout = Pos.app.inflater.inflate (R.layout.settle_ticket_line_layout, this)
				layout.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
				layout.setClickable (true)

				val ticketNo = layout.findViewById (R.id.ticket_no) as PosText?
				val dateTime = layout.findViewById (R.id.ticket_date) as PosText?
				val total = layout.findViewById (R.id.ticket_total) as PosText?
				
				tip = layout.findViewById (R.id.ticket_tip) as PosText

				ticketNo?.text = String.format ("%04d", ticket.getInt ("ticket_no"))
				dateTime?.text = ticket.getString ("date_time").localDate ("M/d hh:mm a")
				total?.text = ticket.getDouble ("total").currency (false)
				tip?.text = ticket.getDouble ("tip").currency (false)

				if (i.odd ()) {

					setBackgroundResource (R.color.light_odd_bg) 
				}
				
				ticketViews.add (this)

				val select = findViewById (R.id.ticket_select) as LinearLayout?
				select?.setOnClickListener {
					 					 
					 ticketReceiptText?.text = ticket.getString ("ticket_text")
					 curr = i
					 currTip = 0
					 render ()
				}
		  }
	 }

	 // theme listener
	 
	 override fun update (theme: Themes) {
		  
	 }
}
