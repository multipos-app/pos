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
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.*
import cloud.multipos.pos.db.DB
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.models.TicketTender
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.NumberInputView

import java.util.Date

class Bank (): CompleteTicket (), InputListener {
	 	 	 
	 override fun controlAction (jar: Jar) {

		  if (Pos.app.ticket.hasItems ()) {

				// bank not allowed in the middle of a sale
				
				PosDisplays.message (Pos.app.getString ("invalid_operation"))
				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("invalid_operation"))
												 .put ("echo_text", ""))
				return
		  }

		  jar (jar)

		  val floatVal: Double = if (jar.has ("float_total")) jar.get ("float_total").getDouble ("amount") else 0.0
		  
		  NumberInputView (this,
								 Pos.app.getString (R.string.open_amount),
								 Pos.app.getString (R.string.enter_open_amount),
								 InputListener.CURRENCY,
								 0)
	 }
	 
	 override fun accept (result: Jar) {
		  
		  var amount = result.getDouble ("value") / 100.0
		  
		  if (jar ().has ("float_total")) {  // remove float total
				
				Pos.app.db.exec ("delete from pos_session_totals where id = " + jar ().get ("float_total").getInt ("id"))
		  }
		  
		  if (jar ().has ("type")) {
								
		  		when (jar ().getString ("type")) {

		  			 "cash_drop", "paid_out" -> {
						  
		  				  amount *= -1.0
					 }
				}
		  }
		  
		  Pos.app.ticket
		  		.put ("ticket_type", Ticket.BANK)
		  		.put ("state", Ticket.COMPLETE)
		  		.put ("tender_desc", jar ().getString ("type"))
		  		.put ("sub_total", amount)
		  		.put ("total", amount)
		  		.put ("start_time", Pos.app.db.timestamp (Date ()))
		  		.put ("complete_time", Pos.app.db.timestamp (Date ()))
		  
		  Pos.app.db.exec ("update tickets set start_time = '" + Pos.app.db.timestamp (Date ()) + "', " +
		  								 "complete_time = '" + Pos.app.db.timestamp (Date ()) + "', " +
		  								 "state = " + Ticket.COMPLETE + ", " +
		  								 "ticket_type = " + Ticket.BANK + ", " +
		  								 "tender_desc = '" + jar ().getString ("type") + "' " +
										 "where id = " + Pos.app.ticket.getInt ("id"))
		  
		  var tt = TicketTender (Jar ()
		  									  .put ("ticket_id", Pos.app.ticket.getInt ("id"))
		  									  .put ("tender_id", 0)
		  									  .put ("tender_type", "cash")
		  									  .put ("sub_tender_type", jar ().getString ("type"))
		  									  .put ("amount", amount)
		  									  .put ("status", TicketTender.COMPLETE)
		  									  .put ("returned_amount", 0)
		  									  .put ("tendered_amount", amount)
		  									  .put ("locale_language", Pos.app.config.getString ("language"))
		  									  .put ("locale_country", Pos.app.config.getString ("country"))
		  									  .put ("locale_variant", "")
		  									  .put ("data_capture", "{}"))

		  var id = Pos.app.db.insert ("ticket_tenders", tt)
		  Pos.app.ticket.tenders.add (tt)
		  
		  completeTicket (Ticket.COMPLETE)
	 }
	 
	 override fun openDrawer (): Boolean { return true }
	 override fun printReceipt (): Boolean { return jar ().getBoolean ("print_receipt") }
}
