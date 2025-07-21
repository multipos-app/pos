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
 
package cloud.multipos.pos.models

import cloud.multipos.pos.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.addons.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.ReportView
import cloud.multipos.pos.views.CustomerSearchView
import cloud.multipos.pos.net.Upload

import java.util.Random
import java.util.Date
import android.os.Build

typealias TicketItemMapper <T> = (TicketItem) -> T
typealias TicketTenderMapper <T> = (TicketTender) -> T
typealias TicketTaxMapper <T> = (TicketTax) -> T

class Ticket (var ticketID: Int, state: Int): Jar (), Model {
	 
	 val ticketUpdates = Jar ()
	 
	 @JvmField var currentItem = TicketItem ()
	 @JvmField val items = mutableListOf <TicketItem> ()
	 @JvmField val voidItems = mutableListOf <TicketItem> ()
	 @JvmField val taxes = mutableListOf <TicketTax> ()
	 @JvmField val tenders = mutableListOf <TicketTender> ()
	 @JvmField val addons = mutableListOf <TicketAddon> ()
	 @JvmField val totals = mutableListOf <Jar> ()
	 @JvmField val other = mutableListOf <Jar> ()
	 @JvmField val updates = mutableListOf <Jar> ()
	 
	 var taxMap = hashMapOf <Int, TicketTax> ()

	 init {

		  // check if the last ticket was not completed, if so load it

		  if (ticketID == 0) {
				
				val ticketResult  = DbResult ("select id, state from tickets order by id desc limit 1", Pos.app.db ())
				if (ticketResult.fetchRow ()) {

					 parse (ticketResult.row ().toString ())

		  			 if (getInt ("state") == OPEN) {
						  
		  				  ticketID = getInt ("id")
		  			 }
				}
		  }
		  
		  if (ticketID > 0) {
				
				val ticketResult  = DbResult ("select * from tickets where id = " + ticketID, Pos.app.db ())
				if (ticketResult.fetchRow ()) {

					 var ticket = ticketResult.row ()
					 
					 copy (ticket)

					 addItems (ticketID)
					 addTaxes (ticketID)
					 addTenders (ticketID)
				}
				else {

					 Logger.w ("no ticket found for ${ticketID}")
				}

				put ("ticket_items", items)
				put ("ticket_taxes", taxes)
				put ("ticket_tenders", tenders)
				put ("ticket_addons", addons)
				put ("totals", totals)
				put ("other", other)
				put ("updates", updates)
				
				if (getInt ("customer_id") > 0) {
					 
					 put ("customer", Customer ().select (getInt ("customer_id")))
				}
				
				if (getInt ("clerk_id") > 0) {
					 
					 val clerkResult = DbResult ("select * from employees where id = " + getInt ("clerk_id"), Pos.app.db ())
					 if (clerkResult.fetchRow ()) {
						  
						  put ("clerk", Employee (clerkResult.row ()))
					 }
				}
				
				update ()  // set totals, tax, etc...
		  }
		  else { // no id, start a new ticket

				var employeeID = 0
				employeeID = Pos.app.employee!!.getInt ("id")
				
				put ("id", 0)
					 .put ("pos_session_id", Pos.app.config.getInt ("pos_session_id"))
					 .put ("business_unit_id", Pos.app.buID ())
					 .put ("pos_no", Pos.app.posNo ())
					 .put ("employee_id", employeeID)
					 .put ("ticket_type", SALE)
					 .put ("start_time", Pos.app.db.timestamp (Date ()))
					 .put ("complete_time", Pos.app.db.timestamp (Date ()))
					 .put ("state", OPEN)
					 .put ("flags", 0)
					 .put ("recall_key", recallID ())
					 .put ("uuid", "".uuid ())  // add a uuid
				
				var id = Pos.app.db ().insert ("tickets", this)
				var ticketNo = String.format ("%d", id)
				Pos.app.db ().exec ("update tickets set ticket_no = '" + ticketNo  + "' where id = " + id)
				
				put ("id", id)
					 .put ("ticket_no", ticketNo)
					 .put ("sub_total", 0)
					 .put ("tax_total", 0)
					 .put ("total", 0)
					 .put ("item_count", 0)
		  }
	 }
	 
	 fun addItems (ticketID: Int) {

		  var subTotal = 0.0
		  var itemCount = 0
		  
		  var sel = "select * from ticket_items where ticket_id = " + ticketID + " and ticket_item_id = 0 order by id"
		  val tiResult = DbResult (sel, Pos.app.db ())
		  while (tiResult.fetchRow ()) {

				val ti = TicketItem ()
				ti.copy (tiResult.row ())

				// get addons
				
				val tiaResult  = DbResult ("select * from ticket_item_addons where ticket_item_id = " + ti.getInt ("id"), Pos.app.db ())
				while (tiaResult.fetchRow ()) {
					 
					 var tia = TicketItemAddon ()
					 
					 tia.copy (tiaResult.row ())
					 tia.put ("type", Ticket.ITEM_ADDON)
					 ti.addons.add (tia)
					 put ("total", getDouble ("total") + tia.getDouble ("addon_amount"))
				}
				
				if (ti.getString ("data_capture").length > 0) {

					 other.add (Jar (ti.getString ("data_capture")))
				}

				ti.put ("complete", 1)
				Pos.app.db.update ("ticket_items", ti.getInt ("id"), Jar ().put ("complete", 1))
				items.add (ti as TicketItem)
				currentItem = ti
				
				subTotal += getDouble ("total") + ti.extAmount ()
				itemCount += getInt ("item_count") + ti.getInt ("quantity")
		  }
		  
		  put ("sub_total", subTotal)
		  put ("item_count", itemCount)

		  for (ti in items) {
				
				var sel = "select * from ticket_items where ticket_item_id = " + ti.getInt ("id")
				val tiResult = DbResult (sel, Pos.app.db ())
				while (tiResult.fetchRow ()) {
					 
					 val link = TicketItem ()
					 link.copy (tiResult.row ())
					 
					 ti.links.add (link)
				}
		  }
		  
		  return
	 }
	 
	 fun addTaxes (ticketID: Int) {

		  var taxTotal = 0.0
		  val taxResult = DbResult ("select * from ticket_taxes where ticket_id = " + ticketID, Pos.app.db ())
		  while (taxResult.fetchRow ()) {

				val tt = TicketTax (taxResult.row ())
				taxes.add (tt)
				put ("total", getDouble ("total") + tt.getDouble ("tax_amount"))
		  }
	 }
	 
	 fun addTenders (ticketID: Int) {

		  val ttResult = DbResult ("select * from ticket_tenders where ticket_id = " + ticketID, Pos.app.db ())
		  while (ttResult.fetchRow ()) {

				val tt = (ttResult.row ())
				tt.put ("complete", 1)
				
				tenders.add (TicketTender (tt))
		  }
	 }
	 
	 /**
	  *
	  * clear the summary pieces before update
	  *
	  */
	 
	 fun zero () {
		  
		  put ("sub_total", 0.0)
		  // put ("total", 0.0)
		  put ("tax_total", 0.0)
		  put ("cost", 0.0)
		  put ("balance", 0.0)
		  put ("tender_total", 0.0)
		  put ("item_count", 0)
		  put ("void_items", 0)
	 }
	 
	 /**
	  *
	  * gather totals, tax and tenders and update ticket record
	  *
	  */
	 
	 override fun update (): Ticket {

		  zero ()
		  
		  Logger.d ("update ticket... ${getInt ("state")} ${getDouble ("total")}")
		  
		  for (ti in items) {
								 								 
				when (ti.getInt ("state")) {
									  
					 TicketItem.REFUND_ITEM,
					 TicketItem.STANDARD,
					 TicketItem.PAYOUT -> {

						  // ti.links.asSequence ().map () {
						  // 		tia -> {

						  // 			 put ("discounts", getDouble ("discounts"), tia.get ("addon_amount"))
						  // 		}
						  // }
						  
						  put ("item_count", getInt ("item_count") + ti.getInt ("quantity"))
						  put ("sub_total", Currency.round (getDouble ("sub_total") + ti.extAmount ()))
						  put ("cost", Currency.round (getDouble ("cost") + ti.getDouble ("cost")))
						  
						  if (ti.getInt ("tax_group_id") > 0) {
								
								var taxGroup = Pos.app.config.taxes ().get (ti.getInt ("tax_group_id").toString ()) as Jar
								put ("tax_total", Currency.round (getDouble ("tax_total") + ti.extAmount () * (taxGroup.getDouble ("rate") / 100.0)))
						  }
					 }

					 TicketItem.VOID_ITEM -> {

						  put ("void_items", getInt ("void_items") + ti.getInt ("quantity"))
					 }
				}
		  }

		  if (getInt ("ticket_type") == SALE) {
				
				put ("total", Currency.round (getDouble ("sub_total") + getDouble ("tax_total")))
		  }
		  
		  for (tt in tenders) {

				put ("tender_total", Currency.round (getDouble ("tender_total") + tt.getDouble ("tendered_amount")))
		  }
  
		  put ("balance", Currency.round (getDouble ("total") - getDouble ("tender_total")))
		  		  
		  Pos.app.db.update ("tickets", getInt ("id"), this)  // update the ticket table
		  
		  PosDisplays.update ()  // redraw displays
		  return this
	 }
	 
	 /**
	  *
	  * complete the ticket
	  *
	  * - create tax records
	  * - create the receipt
	  * - send it to the back office
	  * - queue to totals service
	  *
	  */
	 
	 override fun complete (): Jar {
		  
		  if (getDouble ("tax_toatl") > 0) {

		  }

		  taxes ()  // create tax records

		  if (getString ("ticket_text").length == 0) {
				
				Pos.app.receiptBuilder.ticket (this, PosConst.PRINTER_RECEIPT)
		  		put ("ticket_text", Pos.app.receiptBuilder.text ())
		  }
		  
		  put ("complete_time", Pos.app.db.timestamp (Date ()))
				.put ("ticket_items", items)
				.put ("ticket_taxes", taxes)
				.put ("ticket_tenders", tenders)
				.put ("tender_desc", tenderDesc ())
				.put ("totals", totals)
				.put ("ticket_addons", addons)
				.put ("other", other)
				.put ("updates", updates)
				.update ()  // save
		  
		  when (getInt ("state")) {
				
				Ticket.SUSPEND,
				Ticket.JOB_PENDING,
				Ticket.JOB_COMPLETE,
				Ticket.CREDIT_PENDING -> {
					 
					 // don't upload or total

					 Pos.app.ticket ()  // start a new ticket
				}

				else -> {
					 
					 when (Pos.app.ticket.getInt ("ticket_type")) {
					 
						  Ticket.SALE,
						  Ticket.BANK,
						  Ticket.Z_SESSION -> {
								
								if (Pos.app.config.getBoolean ("print_receipt")) {
									 
									 Pos.app.receiptBuilder.print ()
								}
						  		
								ReportView (Pos.app.getString ("ticket"),
												Jar ()
													 .put ("ticket", this))
						  }
					 }

					 // upload to back office
					 
					 Upload ()
						  .add (this)
						  .exec ()
					 
					 // update totals
					 
					 Pos.app.totalsService.q (PosConst.TICKET, this, Pos.app.handler)

					 PosDisplays.update () // update leaves the previous ticket on the displays
					 Pos.app.ticket ()     // start a new ticket
				}
		  }

		  return this
	 }
	 
	 /**
	  *
	  * tender description... cash, credit, split....
	  *
	  */
 
	 fun tenderDesc (): String {

		  var tenderDesc = ""
		  
		  for (tt in tenders) {
				
		  		if (tenderDesc.length == 0) {
					 
		  			 tenderDesc = tt.getString ("tender_type").lowercase ()
		  		}
		  		else {
					 
		  			 tenderDesc = "split"
		  		}
		  }
		  
		  return tenderDesc
	 }
	 
	 /**
	  *
	  * create tax records
	  *
	  */
	 
	 fun taxes () {

		  val taxMap = mutableMapOf <Int, TicketTax> ()
		  
		  for (ti in items) {
				
				if (ti.getInt ("tax_group_id") > 0) {

			  		 var taxGroup = Pos.app.config.taxes ().get (Integer.toString (ti.getInt ("tax_group_id"))) as Jar
					 var tax = ti.getDouble ("amount") * taxGroup.getDouble ("rate") / 100.0
					 
					 if (taxMap.contains (ti.getInt ("tax_group_id"))) {
						  
						  var tt = taxMap.get (ti.getInt ("tax_group_id"))
						  tt!!.put ("tax_amount", tt!!.getDouble ("tax_amount") + tax)
					 }
					 else {
						  
						  
						  taxMap.put (ti.getInt ("tax_group_id"), TicketTax (Jar ()
		  																							.put ("ticket_id", getInt ("id"))
		  																							.put ("tax_group_id", ti.getInt ("tax_group_id"))
		  																							.put ("tax_incl", ti.getInt ("tax_incl"))
		  																							.put ("tax_amount", tax)
		  																							.put ("short_desc", taxGroup.getString ("short_desc").uppercase ())))
					 }
				}
		  }

		  // save the taxes
		  
		  Logger.x ("taxes... ${taxMap}")

		  taxMap.forEach {
				
				for ((key, tt) in taxMap) {	

														Logger.x ("taxes... ${tt}")
		  												//Pos.app.db.insert ("ticket_taxes", tt)
														taxes.add (tt)
				}
		  }
	 }
	 

	 fun hasItems (): Boolean {

		  return items.size > 0
	 }
	 
	 fun selectItems (): MutableList <TicketItem> {

		  val selectItems = mutableListOf <TicketItem> ()
		  
		  if (hasItems ()) {

		  		if (Pos.app.selectValues.size > 0) {
					 
		  			 for (i in Pos.app.selectValues) {
						  
		  				  selectItems.add (items.get (i.toInt ()))
		  			 }
		  		}
		  		else {
					 
		  			 selectItems.add (currentItem)
		  		}
		  }
		  
		  return selectItems
	 }
	 
	 /**
	  *
	  * add, subtract or zero amount from sale
	  *
	  */
	 
	 fun multiplier (): Double {

		  when (getInt ("ticket_type")) {

				RETURN_SALE, CREDIT_REVERSE, CREDIT_REFUND -> return -1.0
				COMP_SALE -> return 0.0
				else -> return 1.0
		  }
	 }

	 /**
	  *
	  * generate a unique id
	  *
	  * - a uuid
	  * - or just digits for printers that can't print a qrcode
	  *
	  */
	 
	 private fun recallID (): String {

		  // this device cannot print a qrcode, create a random string of digits
		  
		  if (Build.MODEL == "Elo-PP3-13") {
				
				val random = Random ()
				val sb = StringBuilder ()
				var even = 0
				var odd = 0
				
				for (i in 0 until 10) {
					 
					 val n = random.nextInt (ALLOWED_CHARACTERS.length)
					 
					 if ((i % 2) == 0) {
						  
						  even += n.toInt ()
					 }
					 else {
						  
						  odd += n.toInt ()
					 }
					 
					 sb.append (ALLOWED_CHARACTERS [n])
				}
				
				odd *= 3
				var m = even % 10
				var checkDigit = "0"
				
				if (m > 0) {
					 
					 checkDigit += (10 - m).toString ()
				}
				
				sb.append ("0")
				return sb.toString ()
		  }
		  else {
				
				return "".uuid ()
		  }
	 }

	 fun putUpdate (key: String, value: Any): Ticket {

		  super.put (key, value)
		  ticketUpdates.put (key, value)
		  return this
	 }
	 
	 companion object {
		  
		  // Ticket states

		  const val OPEN                  = 0
		  const val COMPLETE              = 1
		  const val ERROR                 = 2
		  const val SUSPEND               = 3
		  const val CREDIT_PENDING        = 4
		  const val KITCHEN_PENDING       = 5
		  const val VOIDED                = 6
		  const val REFUNDED              = 7
		  const val REVERSED              = 8
		  const val RECALLED              = 9
		  const val JOB_PENDING           = 10
		  const val JOB_COMPLETE          = 11

		  // Ticket types

		  const val SALE                  =  0
		  const val VOID                  =  1
		  const val NO_SALE               =  2
		  const val COMP_SALE             =  3
		  const val LOGIN                 =  4
		  const val LOGOUT                =  5
		  const val X_SESSION             =  6
		  const val Z_SESSION             =  7
		  const val RETURN_SALE           =  8
		  const val SALE_NONTAX           =  9
		  const val BANK                  = 10
		  const val DRAWER_COUNT          = 11
		  const val SETTLE_SUMMARY        = 12
		  const val SETTLE_DETAIL         = 13
		  const val REMOTE_SALE           = 14
		  const val REMOTE_RETURN         = 15
		  const val CREDIT_REFUND         = 16
		  const val CREDIT_REVERSE        = 17
		  const val CREDIT_PARTIAL_REFUND = 18
		  const val VOUCHER               = 19
		  const val REPRINT               = 20
		  const val OPEN_AMOUNT           = 21
 		  const val MANAGER_OVERRIDE      = 22
 		  const val ORDER_ITEMS           = 23
		  const val REFUND                = 24
		  const val WEIGHT_ITEMS          = 25
		  const val REDEEM                = 26
		  const val PAYOUT                = 27

		  // Ticket line item types

		  const val ITEM                  =  1
		  const val ITEM_ADDON            =  2
		  const val TAX                   =  3
		  const val TENDER                =  4
		  const val TOTAL                 =  5
		  const val CUSTOMER              =  6
 		  const val CENTER                =  7
 		  const val SESSION_TOTAL         =  8
 		  const val SETTLE_SUMMARY_LINE   =  9
 		  const val SETTLE_DETAIL_LINE    = 10
 		  const val CREDIT_RECALL         = 11
 		  const val LINK                  = 12

		  // flags
		  
 		  const val EXCHANGE_ITEMS        = 0x1
 		  const val DELIVERY_ITEMS        = 0x2
		  
		  private val ALLOWED_CHARACTERS = "0123456789"

		  fun getState (state: Int): String {
				
				when (state) {
					 
		 			 OPEN -> { return Pos.app.getString ("ticket_open") }
					 COMPLETE -> { return Pos.app.getString ("ticket_complete") }
					 ERROR -> { return Pos.app.getString ("ticket_error") }
					 SUSPEND -> { return Pos.app.getString ("ticket_suspend") }
					 CREDIT_PENDING -> { return Pos.app.getString ("ticket_credit_pending") }
					 KITCHEN_PENDING -> { return Pos.app.getString ("ticket_kitchen_pending") }
					 VOIDED -> { return Pos.app.getString ("ticket_voided") }
					 REFUNDED -> { return Pos.app.getString ("ticket_refunded") }
					 REVERSED -> { return Pos.app.getString ("ticket_reversed") }
					 RECALLED -> { return Pos.app.getString ("ticket_recalled") }
				}
				return ""
		  }
	 }
}
