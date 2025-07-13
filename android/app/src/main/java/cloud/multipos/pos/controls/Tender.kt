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

abstract class Tender (jar: Jar?): CompleteTicket () {

	 abstract fun tenderType (): String
	 abstract fun tenderDesc (): String
	 
	 open fun tenderState (): Int { return Ticket.COMPLETE }
	 
	 var tendered = 0.0
	 var returned = 0.0
	 var balance = 0.0
	 var total = 0.0
	 var paid = 0.0
	 var roundDiff = 0.0
	 var fees = 0.0
	 
	 protected var authAmount = 0.0
	 protected var dataCapture = Jar ()

	 init {

		  tenderType = "cash"
	 }

	 override fun controlAction (jar: Jar) {
		  				
		  jar (jar)

		  balance () // balance the sale
		  		  
		  if (confirmed ()) {
				
				fees ()     // add any fees for this tender type
				payment ()  // process the payment, if balance == 0 complete the ticket
				
		  		confirmed (false)
				
				if (openDrawer ()) {
					 
					 DeviceManager.printer.drawer ()
				}
		  }
		  else {
				
				TenderView (this)
		  }
	 }

	 /**
	  *
	  * update tender components, total, total paid, balance....
	  *
	  */

	 open fun balance () {
		  
		  total = total ()  // the sale total
		  balance = 0.0     // to be paid
		  returned = 0.0    // returned (change)
		  paid = 0.0        // previously tender
	  
		  // add up any previous tenders
		  
		  for (tt in Pos.app.ticket.tenders) {

				paid += Currency.round (tt.getDouble ("tendered_amount"))
		  }
		  
		  tender ()
	 }

	 /**
	  *
	  * get current tendered amount
	  *
	  */
	 
	 fun tender () {
		  
		  tendered = 0.0
		  
		  balance = Currency.round (total - paid)
		  
		  if (Pos.app.input.hasInput ()) {

				// operator input?

				jar ()
					 .put ("entry_mode", "keyed")
					 .put ("value", Pos.app.input.getInt ())

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
				else {  // exact change
					 
					 tendered = total () - paid
				}
		  }
		  else {

				// assume the remaining amount of the ticket
				
				tendered = Currency.round (total - paid)
		  }
		  
		  balance = total () - paid - tendered

		  if (balance < 0) {
				
				returned = Currency.round (total () - (paid + tendered))
		  }
	 }

	 /**
	  *
	  * process payment
	  *
	  */
	 
	 fun payment () {

		  if (fees () > 0) {

				applyFees ()
		  }

		  var ticketTender = TicketTender (Jar ()
		  													.put ("ticket_id", Pos.app.ticket.getInt ("id"))
		  													.put ("tender_id", jar ().getInt ("tender_id"))
		  													.put ("tender_type", tenderDesc ())
		  													.put ("sub_tender_type", subTenderDesc ())
		  													.put ("amount", total)
		  													.put ("status", TicketTender.COMPLETE)
		  													.put ("returned_amount", Currency.round (returned))
		  													.put ("tendered_amount", Currency.round (tendered))
		  													.put ("complete", 0)
		  													.put ("round_diff", roundDiff)
															.put ("data_capture", dataCapture.toString ()))
		  
		  var id = Pos.app.db.insert ("ticket_tenders", ticketTender)
		  
		  ticketTender.put ("id", id)
		  ticketTender.put ("type", Ticket.TENDER)

		  if (balance > 0.0) {
				
				ticketTender.put ("balance_due", Currency.round (balance))
		  }
		  else {

				ticketTender.put ("balance_due", 0.0)
		  }
		  
		  Pos.app.ticket.tenders.add (ticketTender)
		  		  
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
					 
				Pos.app.ticket.complete (tenderState ())
		  }
		  else {
				
				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("balance_due"))
												 .put ("echo_text", balance.currency ()))
				updateDisplays ()
		  }
		  

		  // reset
		  
		  tendered = 0.0
		  total = 0.0
		  returned = 0.0
		  paid = 0.0
	 }

	 /**
	  *
	  * cancel the payment
	  *
	  */

	 open fun cancel () { }

	 open fun fees (): Double {

		  var fees = 0.0
		  
		  if (Pos.app.config.has ("credit_service_fee")) {
				
				fees = Currency.round (Pos.app.ticket.getDouble ("total") * Pos.app.config.get ("credit_service_fee").getDouble ("fee") / 100.0)
		  }

		  return fees
	 }

	 open fun applyFees () {

		  var control = OpenItem ()
		  var fees = fees ()
		  
		  control.action (Jar ()
		  							 .put ("confirmed", true)
		  							 .put ("sku", Pos.app.config.get ("credit_service_fee").getString ("fee_sku"))
		  							 .put ("amount", fees))
		  
		  control.action (Jar ()
		  							 .put ("confirmed", true)
		  							 .put ("sku", Pos.app.config.get ("credit_service_fee").getString ("fee_refund_sku"))
		  							 .put ("amount", fees * -1))
		  
	 }
	 
	 /**
	  *
	  * default amount to be paid
	  *
	  */
	 
	 open fun total (): Double {

		  return Pos.app.ticket.getDouble ("total")
	 }

	 override fun beforeAction (): Boolean {
		  
		  if (Pos.app.ticket.items.size == 0) {
				
				return false
		  }
		  
		  else if (!Pos.app.config.getBoolean ("confirm_tender")) {
				
		  		confirmed (true)
		  }

		  return super.beforeAction ()
	 }
	 	 
	 open fun subTenderDesc (): String { return "" }

	 override fun toString (): String {
		  
		  return String.format ("""\n{\n
												tender: %5.2f, \n
												sub_total: %5.2f, \n
												returned: %5.2f, \n
												balance: %5.2f, \n
												total: %5.2f, \n
												paid: %5.2f, \n
												ticket_total: %5.2f\n}""",
										tendered,
										Pos.app.ticket.getDouble ("sub_total"),
										returned,
										balance,
										total,
										paid,
										Pos.app.ticket.getDouble ("total"))
	 }
}
