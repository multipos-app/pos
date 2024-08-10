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
import cloud.multipos.pos.addons.*

import java.util.UUID
import java.util.Random
import java.util.Date
import android.os.Build

typealias TicketItemMapper <T> = (TicketItem) -> T
typealias TicketTenderMapper <T> = (TicketTender) -> T
typealias TicketTaxMapper <T> = (TicketTax) -> T

class Ticket (var ticketID: Int, state: Int): Jar (), Model  {
	 
	 val updates = Jar ()
	 
	 @JvmField var currentItem = TicketItem ()
	 @JvmField val items = mutableListOf <TicketItem> ()
	 @JvmField val voidItems = mutableListOf <TicketItem> ()
	 @JvmField val taxes = mutableListOf <TicketTax> ()
	 @JvmField val tenders = mutableListOf <TicketTender> ()
	 @JvmField val addons = mutableListOf <TicketAddon> ()
	 @JvmField val totals = mutableListOf <Jar> ()
	 @JvmField val other = mutableListOf <Jar> ()

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
				
				var ticketTotal = 0;
				val select = "select * from tickets where id = " + ticketID
				
				val ticketResult  = DbResult ("select * from tickets where id = " + ticketID, Pos.app.db ())
				if (ticketResult.fetchRow ()) {

					 var ticket = ticketResult.row ()
					 
					 copy (ticket)
					 
					 put ("summary",  mutableListOf <Jar> ())
						  .put ("sub_total", 0)
						  .put ("tax_total", 0)
						  .put ("total", 0)
						  .put ("item_count", 0)

					 addItems (ticketID)
					 addTaxes (ticketID)
					 addTenders (ticketID)
				}

				put ("ticket_items", items)
				put ("ticket_taxes", taxes)
				put ("ticket_tenders", tenders)
				put ("ticket_addons", addons)
				put ("totals", totals)
				
				if (getInt ("customer_id") > 0) {
					 
					 val customerResult = DbResult ("select name, fname, lname, phone, email from customers where id = " + getInt ("customer_id"), Pos.app.db ())
					 if (customerResult.fetchRow ()) {
						  
						  put ("customer", Customer (customerResult.row ()))
					 }
				}
				
				if (getInt ("clerk_id") > 0) {
					 
					 val clerkResult = DbResult ("select * from employees where id = " + getInt ("clerk_id"), Pos.app.db ())
					 if (clerkResult.fetchRow ()) {
						  
						  put ("clerk", Employee (clerkResult.row ()))
					 }
				}

				put ("state", state)
				update ()  // set totals, tax, etc...
		  }
		  else {

				// else start a new ticket

				if (has ("id")) {
					 
					 remove ("id")
				}
				
				var employeeID = 0
				employeeID = Pos.app.employee!!.getInt ("id")
				
				put ("pos_session_id", Pos.app.config.getInt ("pos_session_id"))
					 .put ("business_unit_id", Pos.app.buID ())
					 .put ("pos_no", Pos.app.posNo ())
					 .put ("employee_id", employeeID)
					 .put ("ticket_type", SALE)
					 .put ("start_time", Pos.app.db.timestamp (Date ()))
					 .put ("complete_time", Pos.app.db.timestamp (Date ()))
					 .put ("state", OPEN)
					 .put ("flags", 0)
					 .put ("recall_key", recallID ())
					 .put ("uuid", UUID.randomUUID ().toString ())  // add a uuid
				
				var id = Pos.app.db ().insert ("tickets", this)
				var ticketNo = String.format ("%d", id)
				Pos.app.db ().exec ("update tickets set ticket_no = '" + ticketNo  + "' where id = " + id)
				
				put ("id", id)
					 .put ("ticket_no", ticketNo)
					 .put ("summary",  mutableListOf <Jar> ())
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
					 
					 Logger.x ("link... " + link)

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
	 
	 override fun update () {
		  
		  var itemCount = 0
		  var subTotal = 0.0
		  var taxTotal = 0.0
		  var taxTotalInc = 0.0
		  var tenderTotal = 0.0
		  var total = 0.0
		  var addonTotal = 0.0
		  var totalProfit = 0.0
		  var totalWithoutTax = 0.0
		  var totalCost = 0.0
		  var voidItems = 0

		  updates.clear ()
		  
		  put ("summary",  mutableListOf <Jar> ())

		  // val tmp = mutableListOf <TicketItem> ()
		  
		  for (item in items) {
				
				if (item.hasLinks ()) {
					 
					 item.links.forEach {
						  
						  link ->
								
								if (link.getInt ("link_type") == TicketItem.DEPOSIT_LINK) {
									 
									 // tmp.add (link)  // Count deposits
								}
					 }
				}

				var multiplier = 1.0
				when (item.getInt ("state")) {

					 TicketItem.REFUND_ITEM, TicketItem.STANDARD -> {

						  var amount = item.extAmount () * multiplier
						  var addonAmount = 0.0

						  for (tia in item.addons) {
								
								addonAmount += tia.extAmount ()
						  }

						  amount += addonAmount
						  addonTotal += addonAmount
						  
						  for (link in item.links) {
								
								amount += link.extAmount ()
								itemCount += item.getInt ("quantity")
						  }
						  
						  itemCount += item.getInt ("quantity")
						  
						  subTotal += Currency.round (amount)
						  total += amount
						  totalProfit += item.profit ()
						  totalWithoutTax += item.amountWithoutTax () + addonAmount
						  totalCost += item.getDouble ("cost")
						  
						  if ((Pos.app.config.taxes () != null) && (item.getInt ("tax_group_id") > 0)) {
								
								var taxGroup = Pos.app.config.taxes ().get (Integer.toString (item.getInt ("tax_group_id"))) as Jar
								if (taxGroup != null) {

									 var tax = item.tax (taxGroup)
									 if (item.getInt ("tax_incl") == 1) {

										  taxTotalInc += tax
									 }
									 else {
										  taxTotal += tax
									 }
									 item.put ("tax_amount", tax)
								}
						  }
						  else {
						  }
					 }
					 
					 TicketItem.VOID_ITEM -> {

						  voidItems ++
					 }
	 
				}
		  }
		  
		  subTotal = Currency.round (subTotal)
		  taxTotal = Currency.round (taxTotal)
		  totalProfit = Currency.round (totalProfit)
		  total = Currency.round (total + taxTotal)

		  if (totalWithoutTax > 0) {
				put ("total_profit_percent", (totalProfit / totalWithoutTax) * 100.0)
		  }
		  
		  var tenderDesc = ""
		  for (tt in tenders) {
				
				if (tenderDesc.length == 0) {
					 
					 tenderDesc = tt.getString ("tender_type")
				}
				else {
					 
					 tenderDesc = "split"
				}
				
				tt.put ("type", Ticket.TENDER)
				getList ("summary").add (tt)
				tenderTotal += tt.getDouble ("tendered_amount")
		  }

		  if (tenderTotal > total) {
				
				var change = tenderTotal - total
				var e = Jar ()
					 .put ("type", Ticket.TOTAL)
					 .put ("total_desc", Pos.app.getString ("change_due"))
					 .put ("amount", change)
					 .put ("tendered_amount", tenderTotal)
				
				getList ("summary").add (e)
		  }
		  
		  put ("tendered_amount", tenderTotal)
		  
		  putUpdate ("state", getInt ("state"))
				.putUpdate ("item_count", itemCount)
				.putUpdate ("sub_total", subTotal)
				.putUpdate ("tax_total", taxTotal)
				.putUpdate ("total_profit", totalProfit)
				.putUpdate ("total", Currency.round (total))
				.putUpdate ("tendered_amount", Currency.round (tenderTotal))
				.putUpdate ("discounts", Currency.round (addonTotal))
				.putUpdate ("void_items", voidItems)
				.putUpdate ("tender_desc", tenderDesc)
				.putUpdate ("tax_total", Currency.round (taxTotal))
				.putUpdate ("tax_total_inc", Currency.round (taxTotalInc))
				.putUpdate ("ticket_text", getString ("ticket_text"))
				.putUpdate ("employee_id", getInt ("employee_id"))
				.putUpdate ("clerk_id", getInt ("clerk_id"))
				.putUpdate ("customer_id", getInt ("customer_id"))
				.putUpdate ("table_id", getInt ("table_id"))
				.putUpdate ("recall_key", getString ("recall_key"))
				.putUpdate ("uuid", getString ("uuid"))
		  
		  Pos.app.db.update ("tickets", getInt ("id"), updates)  // update the ticket table

		  // dump the items...
		  
		  // var sel = "select * from ticket_items where ticket_id = " + getInt ("id")
		  // val tiResult = DbResult (sel, Pos.app.db ())
		  // while (tiResult.fetchRow ()) {
				
		  // 		Logger.x ("ticket item... " + tiResult.row ())
		  // }
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

	 fun balance (): Double {

		  fun List<TicketTender>.total (fn: TicketTenderMapper <Double>): Double = fold (0.0) { total, tt -> total + fn (tt) }
		  var tendered = tenders.total { it.getDouble ("amount") }
		  return getDouble ("total") - tendered
	 }
	 
	 fun itemMultiplier (): Double {

		  when (getInt ("ticket_type")) {

				RETURN_SALE, CREDIT_REVERSE, CREDIT_REFUND -> return -1.0
				
				COMP_SALE -> return 0.0

				else -> return 1.0
		  }
	 }

	 fun applyAddons (addonID: Int) {

		  items.forEach {

				when (it.getInt ("state")) {
					 
					 TicketItem.STANDARD -> {
						  
						  if (it.hasAddons ()) {
								
								it.addons.forEach { tia ->
																
																if (tia.has ("addon")) {
																	 
																	 tia.put ("addon_amount", 0.0)
																	 val t = tia.getObj ("addon") as Addon
																	 t.apply (it, tia.get ("addon_jar"))
																}
								}
						  }
					 }
		  		}
		  }
	 }

	 fun state (state: Int) {

		  put ("state", state)
		  Pos.app.db ().exec ("update tickets set state = " + state + " where id = " + getInt ("id"))
	 }

	 fun state (): Int {

		  return getInt ("state")
	 }

	 fun type (type: Int) {

		  put ("type", type)
		  Pos.app.db ().exec ("update tickets set ticket_type = " + type + " where id = " + getInt ("id"))
	 }

	 fun type (): Int {

		  return getInt ("type")
	 }

	 fun table (): String {

		  return "tickets"
	 }

	 fun setCurrentItem (pos: Int) {

		  currentItem = items.get (pos)
	 }

	 fun fold (): Ticket {

		  val tmp = mutableListOf <TicketItem> ()
		  for (ti in items) {

				tmp.add (ti)
				
				for (link in ti.links) {
					 
					 tmp.add (link)
				}

				if (ti.hasAddons ()) {

					 ti
						  .put ("ticket_item_addons", ti.addons)
				}
				
				if (ti.hasMods ()) {

					 ti
						  .put ("ticket_item_mods", ti.mods)
				}
		  }
		  
		  items.clear ()
		  tmp.forEach {
				
				items.add (it)
		  }

		  this.put ("version", "1.0")
 
		  return this
	 }

	 private fun recallID (): String {
		  
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
				
				return UUID.randomUUID ().toString ()  // standard uuid
		  }
	 }

	 fun putUpdate (key: String, value: Any): Ticket {

		  super.put (key, value)
		  updates.put (key, value)
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
	 }
}
