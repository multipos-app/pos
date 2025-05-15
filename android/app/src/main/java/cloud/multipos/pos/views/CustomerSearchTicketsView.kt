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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.*

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Filter.FilterResults
import android.widget.AdapterView.OnItemClickListener
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.ArrayList
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Button
import android.graphics.Typeface;

class CustomerSearchTicketsView (customer: Jar): EditView () {
	 
    val list = mutableListOf <CustomerTicketLine> ()
    val ticketList = mutableListOf <Jar> ()
	 lateinit var tickets: LinearLayout
	 protected var selected = 0
	 
    init {
		  
		  var layout = Pos.app.inflater.inflate (R.layout.customer_search_tickets_layout, editLayout) as LinearLayout
		  tickets = layout.findViewById (R.id.customer_tickets_list) as LinearLayout

		  var i = 0
		  val select = "select * from tickets where customer_id = ${customer.getInt ("id")} order by id desc limit 20"
		  val ticketResult = DbResult (select, Pos.app.db)

		  list.add (CustomerTicketLine (Jar ()
														.put ("complete_time", Pos.app.getString ("date_time"))
														.put ("item_count", Pos.app.getString ("items"))
														.put ("total", Pos.app.getString ("total"))
														.put ("state", Pos.app.getString ("status")),
												  i ++,
												  true))
		  
		  while (ticketResult.fetchRow ()) {

				var ticket = ticketResult.row ()
				list.add (CustomerTicketLine (ticket, i ++, false))
				ticketList.add (ticket)
		  }

		  render ()
 		  Pos.app.keyboardView.push (this)
	 }

	 fun render () {

		  tickets.removeAllViews ()
		  tickets.addView (list [0])
		  
		  for (i in 1..list.size - 1) {

				var l = list [i]
				
				if (i == selected) {

					 l.setBackgroundResource (R.drawable.select_border)
				}
				else {
					 
					 if (i.odd ()) l.setBackgroundResource (R.color.light_odd_bg)
					 else l.setBackgroundResource (R.color.light_even_bg)
				}
				
				tickets.addView (l)
		  }
	 }

	 override fun actions () {

		  var layout = Pos.app.inflater.inflate (R.layout.customer_search_tickets_actions_layout, actionsLayout) as LinearLayout

		  var complete = layout.findViewById (R.id.customer_search_tickets_complete) as Button
		  complete.setOnClickListener {
				
				// check if ticket was selected

				if (selected > 0) {

					 selected = selected - 1  // ignore the header
					 					 
					 Pos.app.ticket = Ticket (ticketList [selected].getInt ("id"), Ticket.OPEN)
					 
					 PosDisplays.clear ()
					 PosDisplays.update ()
					 PosDisplays.home ()
					 Pos.app.keyboardView.swipeLeft ()
				}
				
		  }
		  var cancel = layout.findViewById (R.id.customer_search_tickets_cancel) as Button
		  cancel.setOnClickListener {

				Pos.app.keyboardView.swipeLeft ()
		  }
	 }

	 inner class CustomerTicketLine (ticketLine: Jar, i: Int, header: Boolean): LinearLayout (Pos.app) {
		  
		  init {
				
				val layout = Pos.app.inflater.inflate (R.layout.customer_ticket_line_layout, this)
				layout.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
				layout.setClickable (true)
									 			
				if (header) {

					 val date = layout.findViewById (R.id.customer_ticket_date) as PosText?
					 date?.text = ticketLine.getString ("complete_time")
					 val items = layout.findViewById (R.id.customer_ticket_items) as PosText?
					 items?.text = ticketLine.getString ("item_count")
					 val total = layout.findViewById (R.id.customer_ticket_total) as PosText?
					 total?.text = ticketLine.getString ("total")
					 val state = layout.findViewById (R.id.customer_ticket_state) as PosText?
					 state?.text = ticketLine.getString ("state")
					 
					 date?.setTypeface (null, Typeface.BOLD);
					 items?.setTypeface (null, Typeface.BOLD);
					 total?.setTypeface (null, Typeface.BOLD);
					 state?.setTypeface (null, Typeface.BOLD);
				}
				else {
					 
					 val date = layout.findViewById (R.id.customer_ticket_date) as PosText?
					 date?.text = ticketLine.getString ("complete_time").localDate ("MMM. dd, h:m a")
					 val items = layout.findViewById (R.id.customer_ticket_items) as PosText?
					 items?.text = ticketLine.getInt ("item_count").toString ()
					 val total = layout.findViewById (R.id.customer_ticket_total) as PosText?
					 total?.text = ticketLine.getDouble ("total").currency ()
					 val state = layout.findViewById (R.id.customer_ticket_state) as PosText?
					 state?.text = Ticket.getState (ticketLine.getInt ("state"))
				}

				
				val select = layout.findViewById (R.id.customer_ticket_select) as LinearLayout?				
				select?.setOnClickListener {

					 selected = i
					 render ()
				}
		  }
	 }
}
