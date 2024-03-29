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

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.pricing.*
import cloud.multipos.pos.addons.*
import cloud.multipos.pos.views.PosDisplays

/**
 *
 * Special item class for deliveries, price includes tax and delivery costs
 * and tax is assumed inclusive.
 *
 */

class DeliveryItem (): DefaultItem () {
	 
	 init { }
	 
	 override fun controlAction (jar: Jar) {

		  // Logger.d ("delivery item... " + jar)
		  
		  // jar (jar)

		  // // -- get the item
		  // // -- get the deposit
		  // // -- compute deposit amount (price * quantity)
		  // // -- get price for item
		  // // -- subtract deposit amount
		  // // -- set tax included
		  // // -- save item
		  // // -- save deposit

		  // var select = "select items.id, " +
		  // "items.sku, " +
		  // "items.department_id, " +
		  // "items.item_desc, " +
		  // "item_prices.tax_group_id, " +
		  // "item_prices.tax_inclusive, " +
		  // "item_prices.tax_exempt, " +
		  // "item_prices.price, " +
		  // "item_prices.cost, " +
		  // "item_prices.pricing, " +
		  // "departments.is_negative " +
		  // "from departments, item_prices, items " +
		  // "where items.department_id = departments.id and items.id = item_prices.item_id and ";

		  // if (jar.has ("sku")) {

		  // 		select +=
		  // 			 "items.sku = '" + jar ().getString ("sku") + "'"
		  // }
		  // else if (jar.has ("item_id")) {
				
		  // 		select +=
		  // 			 "items.id = " + jar ().getInt ("item_id")
		  // }
		  // else {
		  // 		Logger.w ("delivery item no key " + jar)
		  // 		return
		  // }

		  // var depositPrice = 0.0
		  // var itemResult  = DbResult (select, Pos.app.db)
		  // if (itemResult.fetchRow ()) {
				
		  // 		item = itemResult.row ()
		  // 		pricing = item.get ("pricing")

		  // 		var quantity = Pos.app.vars.getInt ("quantity")
		  // 		if (jar ().has ("quantity")) {
					 
		  // 			 quantity = jar ().getInt ("quantity")
		  // 		}
				
		  // 		if (quantity > 1) {
					 
		  // 			 Pos.app.vars.clear ()
		  // 		}

		  // 		Logger.x ("item... " + item)

		  // 		var linkResult = DbResult ("select i.id from items i, item_links il where i.id = il.item_link_id and il.item_id = " + item.getInt ("id"), Pos.app.db)
		  // 		if (linkResult.fetchRow ()) {
					 
		  // 			 var link = linkResult.row ()
					 
		  // 			 Logger.x ("link... " + link)
					 
		  // 			 // var linkedItem = Item ()
				 	 
		  // 			 var depositResult = DbResult ("select i.sku, ip.* from items i, item_prices ip where i.id = ip.item_id and i.id = " + link.getInt ("id"), Pos.app.db)
		  // 			 if (depositResult.fetchRow ()) {
					 
		  // 				  var deposit = depositResult.row ()
		  // 				  Logger.x ("deposit item... " + deposit)
		  // 				  depositPrice = quantity.toDouble () * deposit.getDouble ("price")
		  // 				  pricing.put ("price", pricing.getDouble ("price") - deposit.getDouble ("price"))
		  // 			 }
		  // 		}

		  // 		ticketItem = TicketItem ()
		  // 		ticketItem
		  // 			 .put ("ticket_id", Pos.app.ticket!!.getInt ("id"))
		  // 			 .put ("type", Ticket.ITEM)
		  // 			 .put ("item_id", item.getInt ("id"))
		  // 			 .put ("department_id", item.getInt ("department_id"))
		  // 			 .put ("sku", item.getString ("sku"))
		  // 			 .put ("item_desc", item.getString ("item_desc"))
		  // 			 .put ("tax_group_id",  item.getInt ("tax_group_id"))
		  // 			 .put ("tax_exempt", 0)
		  // 			 .put ("tax_incl", 1)
		  // 			 .put ("quantity", quantity)
		  // 			 .put ("amount", 0)
		  // 			 .put ("cost", item.getDouble ("cost"))
		  // 			 .put ("state", TicketItem.STANDARD)
		  // 			 .put ("flags", Ticket.DELIVERY_ITEMS)
				
		  // 		Pricing.factory (pricing.getString ("class")).apply (this)

		  // 		Logger.x ("ti after.... " + ticketItem)
		  // }
	 }
}
