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

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.views.TenderView
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.devices.*
import kotlin.math.abs 

open class Tender (jar: Jar?): ConfirmControl () {

	 open fun tenderType (): String { return "unknown" }
	 open fun tenderDesc (): String { return "unknown" }
	 open fun subTenderDesc (): String { return "unknown" }
	 open fun tenderState (): Int { return Ticket.COMPLETE }
	 
	 var tendered = 0.0
	 var returned = 0.0
	 var balance = 0.0
	 var total = 0.0
	 var paid = 0.0
	 var roundDiff = 0.0
	 var fees = 0.0
	 var tenderType = ""

	 protected var authAmount = 0.0
	 protected var dataCapture = Jar ()

	 init {

		  tenderType = "cash"
	 }

	 override fun controlAction (jar: Jar) {
		  				
		  jar (jar)
		  		  
		  total = Pos.app.ticket.getDouble ("total")
		  
		  fees ()
				.tender ()
				.balance ()

		  TenderView (this)
	 }

	 /**
	  *
	  * get current tendered amount
	  *
	  */
	 
	 open fun tender (): Tender {
		  
		  if (Pos.app.input.hasInput ()) {
				
				tendered = Pos.app.input.getDouble () / 100.0
				Pos.app.input.clear ()
		  }
	 	  else if (jar ().has ("value")) {
							
				// fixed amount in the jar?
				
				if (jar ().getDouble ("value") > 0) {  // tendered amount is under the key
					 
					 tendered = jar ().getDouble ("value") / 100.0
				}
				else if (jar ().getDouble ("value") == 0.0) { // round up to the next ($) amount
					 
					 val round = total - total.toInt ().toDouble ()
					 
					 if (round > 0) {
						  
						  tendered = Currency.round ((balance - round) + 1.0)
					 }
					 else {
						  
						  tendered = balance
					 }
				}
		  }
		  else {

				// remaining amount

				balance ()
				tendered = balance
		  }
		  		  
		  return this
	 }

	 /**
	  *
	  * add fees
	  *
	  */

	 open fun fees (): Tender {

		  var fees = 0.0
		  
		  if (Pos.app.config.has ("credit_service_fee")) {
				
				fees = Currency.round (Pos.app.ticket.getDouble ("total") * Pos.app.config.get ("credit_service_fee").getDouble ("fee") / 100.0)
		  
		  }
		  return this
	 }

	 /**
	  *
	  * update the balance
	  *
	  */

	 open fun balance (): Tender {
		  
		  paid = 0.0  // previously tender
	  
		  // add up any previous tenders
		  
		  for (tt in Pos.app.ticket.tenders) {

				paid += Currency.round (tt.getDouble ("tendered_amount"))
		  }
		  
		  balance = Currency.round (total - paid - tendered)

		  if (tendered > total) {

				balance = 0.0
				returned = tendered - total + paid
		  }
		  
		  return this
	 }

	 /**
	  *
	  * create a  payment
	  *
	  */
	 
	 open fun payment (): Tender {
		  
		  var ticketTender = TicketTender (Jar ()
		  													.put ("ticket_id", Pos.app.ticket.getInt ("id"))
		  													.put ("tender_id", jar ().getInt ("tender_id"))
		  													.put ("tender_type", tenderDesc ())
		  													.put ("sub_tender_type", subTenderDesc ())
		  													.put ("amount", total)
		  													.put ("status", TicketTender.COMPLETE)
		  													.put ("returned_amount", Currency.round (returned))
		  													.put ("tendered_amount", Currency.round (tendered))
		  													.put ("balance", balance)
		  													.put ("complete", 0)
		  													.put ("round_diff", roundDiff)
															.put ("data_capture", dataCapture.toString ()))
		  		  
		  var id = Pos.app.db.insert ("ticket_tenders", ticketTender)
		  Pos.app.ticket.tenders.add (ticketTender)

		  ticketTender.put ("id", id)
		  ticketTender.put ("type", Ticket.TENDER)
		  
		  if (returned > 0) {
				
				Pos.app.ticket.put ("returned", returned)
		  }
		  		  
		  if (balance <= 0.0) {

				if (jar ().has ("entry_mode") && (jar ().getString ("entry_mode") == "keyed")) {

					 // remove any keyed values
					 
					 jar ()
						  .remove ("entry_mode")
						  .remove ("value")
				}

				// complete the ticket
				
				Pos.app.ticket
					 .put ("state", tenderState ())
					 .update ()
					 .complete ()
				
	 			if (jar ().has ("value") && (jar ().getString ("entry_mode") == "keyed")) {

					 jar ()
						  .remove ("value")
						  .remove ("keyed")
				}
		  }
		  else {
				
				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("balance_due"))
												 .put ("echo_text", balance.currency ()))
				updateDisplays ()
		  }
		  
		  // reset
		  
		  clear ()
		  return this
	 }

	 /**
	  *
	  * reset values
	  *
	  */

	 fun clear () {
		  
		  tendered = 0.0
		  returned = 0.0
		  balance = 0.0
		  total = 0.0
		  paid = 0.0
		  roundDiff = 0.0
		  fees = 0.0
	 }
	 
	 /**
	  *
	  * cancel the payment
	  *
	  */

	 open fun cancel () { }
	 
	 /**
	  *
	  * default amount to be paid
	  *
	  */
	 
	 override fun beforeAction (): Boolean {
		  
		  if (Pos.app.ticket.items.size == 0) {
				
				return false
		  }
		  else if (!Pos.app.config.getBoolean ("confirm_tender")) {
				
		  		confirmed (true)
		  }

		  return super.beforeAction ()
	 }
	 	 
	 override fun toString (): String {

		  return Jar ()
				.put ("input", Pos.app.input.getString ())
				.put ("tendered", tendered)
				.put ("sub_total", Pos.app.ticket.getDouble ("sub_total"))
				.put ("returned", returned)
				.put ("balance", balance)
				.put ("total",total )
				.put ("paid", paid).stringify ()
	 }
}
