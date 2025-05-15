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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.util.Currency
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.ReportView
import cloud.multipos.pos.views.CustomerSearchView
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.net.Upload

import java.util.Date

open abstract class CompleteTicket (): ConfirmControl () {

	 abstract fun openDrawer (): Boolean
	 abstract fun printReceipt (): Boolean

	 var tenderDesc = ""
	 var tenderType = ""
	 var voidItems = 0
	 var itemCount = 0
	 var discounts = 0.0
	 var taxes = hashMapOf <Int, TicketTax> ()

	 open fun completeTicket (state: Int) {
		  
		  // total the tenders for this ticket, check if tenders >= the ticket
		  		  
		  var lastMessage = ""
		  var tenderTotal = 0.0
		  
		  for (tt in Pos.app.ticket.tenders) {

		  		tenderTotal = Currency.round (tenderTotal + tt.getDouble ("tendered_amount"))
				
		  		if (tenderDesc.length == 0) {
					 
		  			 tenderDesc = tt.getString ("tender_type").toLowerCase ()
		  		}
		  		else {
					 
		  			 tenderDesc = "split"
		  		}
		  }
		  
		  // total the items
		  
		  for (ti in Pos.app.ticket.items) {

				when (ti.getInt ("state")) {
					 
		  			 TicketItem.VOID_ITEM -> {
						  
		  				  voidItems ++
					 }
				}
		  		itemCount ++
				
		  		for (tia in ti.addons) {

		  			 discounts += Currency.round (tia.getDouble ("addon_amount") * tia.getInt ("addon_quantity"))
		  		}
		  }

		  var ticketTax = taxes ()
		  
		  // create the summary
		  
		  Pos.app.ticket.put ("summary", ArrayList <Jar> ())
		  
		  if (Pos.app.config.has ("tax_included")) {

				// compute the included tax, amount - (amount / 1 + tax rate)

				var rate = Pos.app.config.getDouble ("tax_included") / 100.0
				var taxIncAmount = Pos.app.ticket.getDouble ("total") * rate
				
				Pos.app.ticket.getList ("summary").add (Jar ()
					 													  .put ("type", Ticket.TOTAL)
					 													  .put ("total_desc", Pos.app.getString ("tax_total_included") + " " + Pos.app.config.getDouble ("tax_included") + "%")
					 													  .put ("amount", Currency.round (taxIncAmount)))
		  }
		  else {

				if (ticketTax != null) {
					 
					 Pos.app.ticket.getList ("summary").add (Jar ()
																				.put ("type", Ticket.TOTAL)
																				.put ("total_desc", Pos.app.getString ("tax_total"))
																				.put ("amount", Currency.round (ticketTax.getDouble ("tax_amount"))))
				}
		  }
						  
		  var returned = 0.0
		  var prompt = Pos.app.getString ("register_open")
		  if (tenderTotal > Pos.app.ticket.getDouble ("total")) {
				
				prompt = Pos.app.getString ("change_due")
				returned = tenderTotal - Pos.app.ticket.getDouble ("total")
		  }
		  
		  PosDisplays.message (Jar ()
											.put ("prompt_text", prompt)
											.put ("echo_text", returned.currency ()))
		  
		  // generate a receitpt...

		  Pos.app.receiptBuilder.ticket (Pos.app.ticket, PosConst.PRINTER_RECEIPT)
		  
		  var ticketText = Pos.app.receiptBuilder.text ()
		  
		  if (Pos.app.ticket.has ("aux_receipts")) {
				
				ticketText =
					 ticketText +
				Pos.app.ticket.getString ("aux_receipts").replace ('\'', '`') 
				Pos.app.ticket.remove ("aux_receipts")
		  }
		  
		  Pos.app.ticket
				.put ("complete_time", Pos.app.db.timestamp (Date ()))
				.put ("state", state)
				.put ("total", Pos.app.ticket.getDouble ("total"))
				.put ("discounts", discounts)
				.put ("item_count", itemCount)
				.put ("void_items", voidItems)
				.put ("tender_desc", tenderDesc.toLowerCase ())
				.put ("ticket_text", ticketText)
				.put ("ticket_items", Pos.app.ticket.items)
				.put ("ticket_taxes", Pos.app.ticket.taxes)
				.put ("ticket_tenders", Pos.app.ticket.tenders)
				.put ("totals", Pos.app.ticket.totals)
				.put ("ticket_addons", Pos.app.ticket.addons)
				.put ("other", Pos.app.ticket.other)
				.put ("updates", Pos.app.ticket.updates)

		  // update ticket as complete
		  
		  Pos.app.ticket.update ()
		  
		  // display receipt?
		  						
		  if (state == Ticket.COMPLETE) {
				
				when (Pos.app.ticket.getInt ("ticket_type")) {
					 
					 Ticket.SALE,
					 Ticket.BANK -> {
		  
						  if (printReceipt ()) {
								
								Pos.app.receiptBuilder.print ()
						  }
		  				  
						  ReportView (Pos.app.getString ("ticket"),
										  Jar ()
												.put ("ticket", Pos.app.ticket))
					 }
				}
		  }
				
		  DeviceManager.customerDisplay?.update (Pos.app.ticket)

		  // post the ticket to the server

		  when (state) {

				Ticket.SUSPEND -> {

					 // don't upload or total

					 Pos.app.ticket ()  // start a new ticket
				}

				else -> {
					 
					 Upload ()
						  .add (Pos.app.ticket)
						  .exec ()
					 
					 Pos.app.totalsService.q (PosConst.TICKET, Pos.app.ticket, Pos.app.handler)  // queue the ticket to totals

					 PosDisplays.clear ()
					 updateDisplays ()  // update leaves the previous ticket on the displays
					 Pos.app.ticket ()  // start a new ticket
				}
		  }
		  
		  itemCount = 0
		  voidItems = 0
		  discounts = 0.0
		  tenderTotal = 0.0
		  tenderDesc = ""
		  taxes.clear ()
		  
		  if (Pos.app.config.getBoolean ("prompt_customer")) {
				
				PosDisplays.clear ()
				CustomerSearchView ()
		  }
	 }

	 open fun taxes (): TicketTax? {
		  
		  var ticketTax: TicketTax? = null
		  
		  for (ti in Pos.app.ticket.items) {
				
		  		when (Pos.app.ticket.getInt ("ticket_type")) {

					 Ticket.SALE_NONTAX -> {
					 }
		  			 else -> {
						  
		  				  when (ti.getInt ("state")) {

		  						TicketItem.REFUND_ITEM, TicketItem.STANDARD -> {
									 
		  							 if (ti.getInt ("tax_group_id") > 0) {
										  
		  								  var taxGroup = Pos.app.config.taxes ().get (Integer.toString (ti.getInt ("tax_group_id")))
										  
		  								  if (taxGroup != null) {
												
		  										var tax = ti.tax (taxGroup as Jar)
		  										ticketTax = taxes.get (ti.getInt ("tax_group_id"))
												
		  										if (ticketTax != null) {
													 
		  											 ticketTax.put ("tax_amount", ticketTax.getDouble ("tax_amount") + tax)
		  										}
		  										else {
													 													 
		  											 ticketTax = TicketTax (Jar ()
		  																				 .put ("ticket_id", Pos.app.ticket.getInt ("id"))
		  																				 .put ("tax_group_id", ti.getInt ("tax_group_id"))
		  																				 .put ("tax_incl", ti.getInt ("tax_incl"))
		  																				 .put ("tax_amount", tax)
		  																				 .put ("short_desc", taxGroup.getString ("short_desc")))
													 
		  											 Pos.app.db.insert ("ticket_taxes", ticketTax)
		  											 taxes.put (ti.getInt ("tax_group_id"), ticketTax)
		  										}
		  								  }
		  							 }
								}
						  }
					 }		  
				}
		  }
		  
		  // add the taxes to the ticket 

		  taxes.forEach {(key, tt) -> Pos.app.ticket.taxes.add (tt) }
		  return ticketTax
	 }
}
