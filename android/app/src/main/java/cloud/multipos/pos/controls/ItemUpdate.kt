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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.Item
import cloud.multipos.pos.models.TicketItem
import cloud.multipos.pos.models.Department
import cloud.multipos.pos.views.ItemEditView

open class ItemUpdate (): ConfirmControl () {

	 override fun controlAction (jar: Jar) {
		  		  
		  if (jar.has ("item")) {

				val item = update (jar.get ("item"))  // save the item and add it to the current ticket
				
				if (jar.has ("ticket_item_index") && (jar.getInt ("ticket_item_index") >= 0)) {
					 
					 Pos.app.selectValues.clear ()
					 Pos.app.selectValues.add (jar.getInt ("ticket_item_index"))

					 Control.factory ("VoidItem").action (Jar ()
																			.put ("sku", jar.get ("item").getString ("sku"))
																			.put ("data_capture", Jar ()
																						 .put ("void_reason", "item_update")
																						 .toString ()))
				}
				
				// add the new item to the current ticket
				
				Control.factory ("DefaultItem").action (Jar ()
																		  .put ("sku", jar.get ("item").getString ("sku")))
		  }
	 }

	 fun update (item: Jar) {
						  
		  // build the item and item price

		  for (key in listOf ("price", "cost")) {

				if (item.getString (key).length == 0) {

					 item.put (key, 0.0)
				}
		  }

		  var item = Jar ()
				.put ("type", "items")
				.put ("id", 0)
				.put ("sku", item.getString ("sku"))
				.put ("item_desc", item.getString ("item_desc"))
				.put ("department_id", item.getInt ("department_id"))
				.put ("locked", 0)
				.put ("enabled", 1)
				.put ("uuid", "".uuid ())
				.put ("deposit_item_id", item.getInt ("deposit_item_id"))
				.put ("item_price", Jar ()
							 .put ("id", 0)
							 .put ("business_unit_id", Pos.app.buID ())
							 .put ("tax_group_id", item.getInt ("tax_group_id"))
							 .put ("tax_inclusive", 0)
							 .put ("tax_exempt", if (item.getInt ("tax_group_id") > 0) 0 else 1)
							 .put ("pricing", Jar ()
										  .put ("price", item.getDouble ("price"))
										  .put ("cost", item.getDouble ("cost"))
										  .toString ())
							 .put ("class", "standard")
							 .put ("price", item.getDouble ("price"))
							 .put ("cost", item.getDouble ("cost")))
		  		  
		  Item.update (item)
	 }
}
