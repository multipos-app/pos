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
import cloud.multipos.pos.pricing.*
import cloud.multipos.pos.addons.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.devices.DeviceManager

open class DefaultItem (): FirstItem (), InputListener {

	 var firstItem: Boolean = false
	 var quantity = 1
	 lateinit var item: Item
	 var ticketItem = TicketItem ()
	 	 
	 override fun controlAction (jar: Jar) {
		  
		  var sku = ""
		  
		  jar (jar)
		  
		  if (!jar.has ("update_displays")) {

				jar.put ("update_displays", true)
		  }

		  var itemSelect: String
		  if (jar.has ("sku")) {
				
				itemSelect = "items.sku = '" + jar ().getString ("sku").trim () + "'"
				sku = jar ().getString ("sku").trim ()
		  }
		  else if (jar.has ("item_id")) {

				itemSelect = "items.id = " + jar ().getString ("item_id")
		  }
		  else {
				
				Logger.w ("item sku is null...")
				return
		  }

		  if (mergeItems ()) {
				
				// add to item already in the sale
				// don't update voided items, etc...

				val items = Pos.app.ticket.items.toList ()
				items.forEach {
					 
					 if ((sku.length > 0) &&
							(it.getString ("sku") == sku) &&
							(it.getInt ("complete") == 0) &&
							(it.getInt ("state") == TicketItem.STANDARD)) {
						  
						  Pos.app.ticket.currentItem = it
						  Control.factory ("Quantity").action (Jar ().put ("value", 1))
						  return
					 }
				}
		  }
		  
		  item = Item (jar ())
		  
		  if (item.exists ()) {

				if (!item.getBoolean ("enabled")) {
					 
					 PosDisplays.message (Jar ()
											.put ("prompt_text", Pos.app.getString ("item_disabled"))
											.put ("echo_text", ""))
					 return;
				}
				
				firstItem = false

				if (Pos.app.ticket.has ("items")) {
					 firstItem = Pos.app.ticket.getInt ("items") == 0
				}
				else {
					 
					 Pos.app.ticket.put ("items", 0)
				}
				
				quantity = Pos.app.quantity
				Pos.app.quantity = 1
				if (jar ().has ("quantity")) {
					 
					 quantity = jar ().getInt ("quantity")
				}
				
				ticketItem = TicketItem ()
				Pos.app.ticket.currentItem = ticketItem

				var applyAddons = true
				if (jar ().has ("apply_addons")) {
					 
					 applyAddons = jar ().getBoolean ("apply_addons")
				}

				var state = TicketItem.STANDARD
				if (Pos.app.ticket.getInt ("ticket_type") == Ticket.RETURN_SALE) {
					 
					 state = TicketItem.RETURN_ITEM
					 if (Pos.app.ticket.has ("return_items")) {

						  Pos.app.ticket.put ("return_items",  Pos.app.ticket.getInt ("return_items") + quantity)
					 }
					 else {
						  Pos.app.ticket.put ("return_items",  quantity)
					 }
				}

				var taxAmount = 0
				val taxExempt = if (item.getBoolean ("tax_exempt") || Pos.app.ticket.getInt ("ticket_type") == Ticket.SALE_NONTAX) 1 else 0

				var itemDesc = if (jar ().has ("item_desc")) jar ().getString ("item_desc") else item.getString ("item_desc")
				
				ticketItem
					 .put ("ticket_id", Pos.app.ticket.getInt ("id"))
					 .put ("type", Ticket.ITEM)
					 .put ("item_id", item.getInt ("id"))
					 .put ("department_id", item.getInt ("department_id"))
					 .put ("sku", item.getString ("sku"))
					 .put ("item_desc", itemDesc)
					 .put ("tax_group_id",  item.getInt ("tax_group_id"))
					 .put ("tax_exempt", taxExempt)
					 .put ("tax_incl", if (item.getBoolean ("tax_inclusive")) 1 else 0)
					 .put ("quantity", quantity)
					 .put ("amount", 0)
					 .put ("cost", item.getDouble ("cost"))
					 .put ("state", state)
					 .put ("entry_mode", jar ().getString ("entry_mode"))
					 .put ("flags", 0)
					 .put ("complete", 0)
					 .put ("apply_addons", applyAddons)
				
				if ((taxExempt == 0) && (item.getInt ("tax_group_id") > 0)) {
					 
					 var taxGroup = Pos.app.config.taxMap ().get (Integer.toString (item.getInt ("tax_group_id"))) as Jar
					 ticketItem
						  .put ("tax_amount", ticketItem.tax (taxGroup))
				}

				if (jar ().has ("weight")) {
					 
					 ticketItem
						  .put ("metric", jar.getDouble ("weight"))
				}
				
				if (jar ().has ("data_capture")) {
					 
					 ticketItem
						  .put ("data_capture", jar.get ("data_capture").toString ())
				}

				// update ticket total
				
				Pos.app.ticket.put ("total", Pos.app.ticket.getDouble ("total") + ticketItem.extAmount ())

				// does item have a pre-set amount?
				
				if (jar ().has ("amount")) {
					 
					 ticketItem.put ("amount", jar ().getDouble ("amount") * Pos.app.ticket.multiplier ())
					 complete ()
					 return
				}

				// apply pricing to get the amount for this item
				
				Pricing.factory (item.getString ("class"))
					 .apply (this)
		  }
		  else {

				PosDisplays.alert ("${Pos.app.getString ("item_not_found")}, ${sku}")
				Control.factory ("ItemNotFound").action (Jar ().put ("sku", sku))
		  }
	 }

	 fun complete () {
		  		  
		  var negative = 1.0
		  val update = Jar ()
		  
		  if (jar ().has ("add_item")) {

				ticketItem.put ("add_item", jar ().get ("add_item"))
		  }
		  
		  if (item.has ("is_negative")) {

				if (item.getString ("is_negative").equals ("true")) {
					 negative = -1.0
				}
				else {
					 if (item.getBoolean ("is_negative")) {
						  negative = -1.0
					 }
				}
		  }

		  // some items require receipts
		  
		  if (item.has ("print_receipt")) {

				if (!Pos.app.ticket.has ("print_receipts")) {

					 Pos.app.ticket.put ("print_receipts", 0)
				}
				
				Pos.app.ticket.put ("print_receipts", Pos.app.ticket.getInt ("print_receipts") + 1)
		  }

		  if (jar ().has ("amount")) {

				ticketItem.put ("amount", jar ().getDouble ("amount") * negative)
		  }
		  else {
				
				ticketItem.put ("amount", ticketItem.getDouble ("amount") * negative)
		  }

		  update.put ("amount", ticketItem.getDouble ("amount"))
		  
		  if (jar ().has ("parent")) {

				// attach it to the parent item

				val parent = jar ().getObj ("parent") as TicketItem
				parent.links.add (ticketItem)
				ticketItem.put ("ticket_item_id", parent.getInt ("id"))
		  }
		  else {
				
				Pos.app.ticket.items.add (ticketItem)
		  }

		  Pos.app.ticket.update ()
		  
		  if (ticketItem.getInt ("id") > 0) {
				
				Logger.w ("def item complete... " + ticketItem.getInt ("id") + " " + update)
				// Pos.app.db.update ("ticket_items", ticketItem.getInt ("id"), update)
		  }
		  else {

				Pos.app.db.insert ("ticket_items", ticketItem)
		  }

		  /**

			* check for addons on standard sales only
			*/

		  if (Pos.app.ticket.getInt ("ticket_type") == Ticket.SALE) {

				var addonAmount = 0
				if (item.has ("addon_id") && (item.getInt ("addon_id") > 0)) {
					 
					 var addonResult  = DbResult ("select * from addons where id = " + item.getInt ("addon_id"), Pos.app.db)
					 
					 while (addonResult.fetchRow ()) {
						  
						  var addon = addonResult.row () as Jar
						  val a = LoadClass.get ("cloud.multipos.pos.addons." + addon.getString ("class")) as Addon
						  a.apply (ticketItem, addon)
					 }
				}
		  }
		  
		  /**
			* check for linked items (deposits, packages... )
			*/

		  if (!jar ().has ("item_link_id")) {

				val itemLinkResult  = DbResult ("select i.sku, il.item_link_id from items i, item_links il where i.id = il.item_link_id and il.item_id = " +
														  item.getInt ("id"), Pos.app.db)
				
				while (itemLinkResult.fetchRow ()) {
					 
					 val itemLink = itemLinkResult.row () as Jar
					 ticketItem.put ("item_link_id", itemLink.getInt ("item_link_id"))
					 
					 val linkedItem = DefaultItem ()
				 
					 val l = Jar ()
						  .put ("sku", itemLink.getString ("sku"))
						  .put ("ticket_item_id", ticketItem.getInt ("id"))
						  .put ("link_type", itemLink.getInt ("link_type"))
						  .put ("apply_addons", itemLink.getBoolean ("apply_addons"))
						  .put ("entry_mode", "linked")
						  .put ("parent", ticketItem)
						  .put ("quantity", quantity)
						  .put ("update_displays", false)
					 
					 linkedItem.action (l)
				}
		  }
		  
		  // clean things up
		  
		  Pos.app.input.clear ()  // clear input values
		  jar ().remove ("amount")  // remove the amount from the jar

		  // save the new ticket info
		  	  	  
		  DeviceManager.customerDisplay?.update (Pos.app.ticket)  // send it to the customer display if no links
		  Pos.app.ticket.currentItem = ticketItem
		  
		  PosDisplays.update ()
		  PosDisplays.message (Jar ()
											.put ("prompt_text", ticketItem.getString ("item_desc"))
											.put ("echo_text", ticketItem.getDouble ("amount").currency ()))
	 }

	 fun ticketItem (): TicketItem { return ticketItem }
	 fun pricing (): Jar { return item.get ("pricing") }

	 open fun mergeItems (): Boolean {

		  if (jar.has ("merge_like_items")) {

				return jar.getBoolean ("merge_like_items")
		  }
		  else {
				
				return Pos.app.config.getBoolean ("merge_like_items")
		  }
	 }
	 
	 override fun accept (jar: Jar) {
	 }
}
