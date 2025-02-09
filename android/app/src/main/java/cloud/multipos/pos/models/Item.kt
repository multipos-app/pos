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
 
package cloud.multipos.pos.models;

import cloud.multipos.pos.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.util.extensions.*;
import cloud.multipos.pos.pricing.*

class Item (jar: Jar) {

	 var exists = false
	 lateinit var item: Jar
	 
	 init {
		  
		  var select = ""
		  if (jar.has ("sku")) {
				
				select = "items.sku = '" + jar.getString ("sku").trim () + "'"
		  }
		  else if (jar.has ("item_id")) {
				
				select = "items.id = " + jar.getString ("item_id")
		  }
		  
		  select = "select " +
		  "items.id, " +
		  "items.sku, " +
		  "items.department_id, " +
		  "items.item_desc, " +
		  "item_prices.id as item_price_id, " +
		  "item_prices.tax_group_id, " +
		  "item_prices.tax_inclusive, " +
		  "item_prices.tax_exempt, " +
		  "item_prices.price, " +
		  "item_prices.cost, " +
		  "item_prices.class, " +
		  "item_prices.pricing, " +
		  "departments.is_negative, " +
		  "departments.department_type, " +
		  "addon_links.addon_id " +
		  "from departments, item_prices, items left join addon_links on items.id = addon_links.addon_link_id " +
		  "where items.department_id = departments.id and items.id = item_prices.item_id and " +
		  select
		  
		  val itemResult = DbResult (select, Pos.app.db)
		  if (itemResult.fetchRow ()) {
				
				item = itemResult.row ()

				exists = true				
				item.put ("pricing", item.get ("pricing"))
				item.put ("item_desc", item.getString ("item_desc").trim ().uppercase ());
		  }
		  else {

				exists = false
		  }
	 }

	 companion object {

		  fun update (update: Jar): Boolean {
				
				val item = Item (Jar ().put ("sku", update.getString ("sku")))
				
				if (item.exists ()) {
					 
					 Pos.app.db.update ("items", item.getInt ("id"), Jar ()
													.put ("sku", update.getString ("sku"))
													.put ("item_desc", update.getString ("item_desc"))
													.put ("department_id", update.getInt ("department_id")))

					 Pos.app.db.update ("item_prices", item.getInt ("item_price_id"), Jar ()
													.put ("tax_group_id", update.get ("item_price").getInt ("tax_group_id"))
													.put ("pricing", update.get ("item_price").get ("pricing").toString ())
													.put ("price", update.get ("item_price").getDouble ("price"))
													.put ("cost", update.get ("item_price").getDouble ("cost")))
				}
				else {
					 					 
					 var itemPrice = update.get ("item_price")
						  .put ("item_id", Pos.app.db ().insert ("items", update))

					 Pos.app.db ().insert ("item_prices", itemPrice)
				}

				// add the update to the current sale, this will update the back office
				
				Pos.app.ticket.updates.add (update)

				return true;
		  }		  
	 }
	 
	 fun exists (): Boolean { return exists }
	 fun price (): String {

		  return item.getDouble ("amount").currency ()
	 }

	 fun getString (key: String): String {

		  return item.getString (key)
	 }
	 
	 fun getInt (key: String): Int {

		  return item.getInt (key)
	 }
	 
	 fun getDouble (key: String): Double {

		  return item.getDouble (key)
	 }
	 
	 fun getBoolean (key: String): Boolean {

		  return item.getBoolean (key)
	 }

	 fun get (key: String): Jar {

		  return item.get (key)
	 }
	 
	 fun has (key: String): Boolean {

		  return item.has (key)
	 }
	 
	 fun put (key: String, any: Any) {

		  item.put (key, any)
	 }

	 override fun toString (): String {

		  return item.toString ()
	 }
}
