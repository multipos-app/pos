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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.net.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.receipts.*
import cloud.multipos.pos.views.PullTabsWeightView
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.ReportView

import java.util.Date

class PullTabsWeight (): Control (), InputListener {

	 override fun controlAction (jar: Jar) {
		  
		  val pullTabsInPlay = ArrayList <Jar> ()
		  val pullTabsPending = ArrayList <Jar> ()

		  var select = """
		  select i.id, i.sku, i.item_desc, i.department_id, i.enabled, i.status 
		  from items i, item_prices ip 
		  where i.enabled = 'true' and status = 0 and i.id = ip.item_id and i.department_id = ${jar.getInt ("department_id")} 
		  order by item_desc
		  """
		  
		  val pullTabsInPlayResult = DbResult (select, Pos.app.db)
		  while (pullTabsInPlayResult.fetchRow ()) {

				var ptab = pullTabsInPlayResult.row ()
				pullTabsInPlay.add (ptab)
		  }
		  
		  select = """
		  select i.id, i.sku, i.item_desc, i.department_id, i.enabled , i.status 
		  from items i, item_prices ip 
		  where i.enabled = 'true' and status = 1 and i.id = ip.item_id and i.department_id = ${jar.getInt ("department_id")} 
		  order by item_desc
		  """
		  
		  val pullTabsPendingResult = DbResult (select, Pos.app.db)
		  while (pullTabsPendingResult.fetchRow ()) {

				var ptab = pullTabsPendingResult.row ()
				pullTabsPending.add (ptab)
		  }

			PullTabsWeightView (pullTabsInPlay, pullTabsPending, this)
	 }
	 
	 override fun accept (results: Jar) {

		  var tareWeight = 0.0
		  var weight = 0.0
		  var quantity = 0
		  var itemCount = 0
		  val consolidated = mutableListOf <Jar> ()
		  
		  // first consolidate multiple weights of the same set of tickets
		  
		  for (tickets in results.getList ("results")) {

				Logger.x ("weight results... ${tickets}")

				if (tickets.has ("id")) {

					 if (consolidated.size > 0) {

						  var add = true
						  for (c in consolidated) {

								if (c.getInt ("id") == tickets.getInt ("id")) {

									 c.put ("weight", c.getDouble ("weight") + tickets.getDouble ("weight"))
									 add = false
									 break;
								}
						  }
						  
						  if (add) {
								
								consolidated.add (tickets)
						  }
					 }
					 else {

						  consolidated.add (tickets)
					 }
				}
				else if (tickets.getString ("item_desc") == "TARE") {

					 consolidated.add (tickets)
				}
		  }

		  // create the ticket items
		  
		  for (tickets in consolidated) {
					 
				var dataCapture = ""
					 
				if (tickets.getString ("item_desc") == "TARE") {
						  
					 tareWeight = tickets.getDouble ("weight")
				}
				else {
					 
					 quantity = tickets.getDouble ("weight").scale () - tareWeight.scale ()
					 itemCount += quantity

					 Logger.x ("weight consolidated... ${tickets} ${quantity} ${itemCount}")
					 
					 if (tickets.getInt ("status") == 1) {

						  // move these tickets in play, set status to zero and tell the server
						  
						  Pos.app.db.update ("items", tickets.getInt ("id"), Jar ().put ("status", 0));

						  val actions = mutableListOf <Jar> ()
						  val updates = mutableListOf <Jar> ()
						  updates.add (Jar ().put ("status", 0))
						  
						  actions.add (Jar ()
												 .put ("action", "update")
												 .put ("table", "items")
												 .put ("id", tickets.getInt ("id"))
												 .put ("updates", updates))
						  
						  dataCapture = Jar ()
								.put ("actions", actions)
								.toString ()
					 }
				}

				var ti = TicketItem ()
				ti
					 .put ("ticket_id", Pos.app.ticket.getInt ("id"))
					 .put ("metric_type", TicketItem.WEIGHT_ITEM)
					 .put ("item_id", tickets.getInt ("id"))
					 .put ("department_id", 0)
					 .put ("sku", tickets.getString ("sku"))
					 .put ("item_desc", tickets.getString ("item_desc"))
					 .put ("metric", tickets.getDouble ("weight"))
					 .put ("tax_group_id", 0)
					 .put ("tax_exempt", true)
					 .put ("tax_incl", false)
					 .put ("quantity", quantity)
					 .put ("amount", 0)
					 .put ("cost", 0)
					 .put ("state", 0)
					 .put ("flags", 0)
					 .put ("data_capture", dataCapture)
					 .put ("complete", 1)
					 .put ("apply_addons", false)
				
				Pos.app.ticket.items.add (ti)
				Pos.app.db.insert ("ticket_items", ti)
		  }
		  
		  Pos.app.ticket
				.put ("ticket_type", Ticket.WEIGHT_ITEMS)
				.put ("complete_time", Pos.app.db.timestamp (Date ()))
				.put ("state", Ticket.COMPLETE)
				.put ("item_count", itemCount)
				.put ("ticket_items", Pos.app.ticket.items)
		  
		  val receiptBuilder = PullTabsReceiptBuilder ()
		  receiptBuilder.ticket (Pos.app.ticket, PosConst.PRINTER_RECEIPT)
		  val ticketText = receiptBuilder.text ()

		  Pos.app.ticket
				.put ("ticket_text", ticketText)
  
		  Pos.app.ticket.update ()
		  
		  ReportView (Pos.app.getString ("ticket"),
						  Jar ()
								.put ("ticket", Pos.app.ticket))
		  
		  Upload ()
				.add (Pos.app.ticket)
				.exec ()
		  
		  Pos.app.ticket ()  // start a new ticket
		  PosDisplays.clear ()
	 }

	 fun Double.scale (): Int {

		  return (this * 1000.0).toInt ()
	 }

		  
}
