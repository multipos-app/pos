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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.views.AddItemView

class AddItem (): Control () {

	 val departments: MutableList <Jar> = mutableListOf ()
	 val deposits: MutableList <Jar> = mutableListOf ()
	 val taxes: MutableList <Jar> = mutableListOf ()
	 
	 init {
		  
		  deposits.add (Jar ()
								  .put ("id", 0)
								  .put ("item_desc", Pos.app.getString (R.string.none)))
		  
		  val deptsResult = DbResult ("select id, department_desc, department_type from departments order by department_desc" , Pos.app.db)
		  
		  departments.add (Jar ()
									  .put ("id", 0)
									  .put ("department_desc", Pos.app.getString (R.string.department)))
								 
		  while (deptsResult.fetchRow ()) {

				var department = deptsResult.row ()
				departments.add (department)
				
				if (department.getInt ("department_type") == 4) {
					 
					 val depositResult = DbResult ("select id, item_desc from items where department_id = " + department.getInt ("id") + " order by item_desc" , Pos.app.db)
					 while (depositResult.fetchRow ()) {

						  deposits.add (depositResult.row ())
					 }
				}
		  }

		  for ((key, tax) in Pos.app.config.taxes ()) {

				val t = tax as Jar
				t.put ("tax_group_id", key)
				taxes.add (t)
		  }

		  taxes.add (Jar ()
							  .put ("tax_group_id", "0")
							  .put ("short_desc", Pos.app.getString (R.string.add_item_non_tax)))
	 }
	 
	 override fun controlAction (jar: Jar) {

		  if (jar.getBoolean ("complete")) {
				
				var id = Pos.app.db.insert ("pos_updates", Jar ()
																  .put ("status", 0)
																  .put ("update_table", "items")
																  .put ("update_id", Pos.app.cloudService.downloadCount () + 5))
				var itemID = id
				
				var item = Jar ()
					 .put ("id", id)
					 .put ("sku", jar.getString ("sku"))
					 .put ("item_desc", jar.getString ("item_desc"))
					 .put ("department_id", jar.getInt ("department_id"))
					 .put ("price",  jar.getDouble ("price"))
					 .put ("is_negative", false)
					 .put ("deposit_id", jar.getInt ("deposit_id"))
				
				Pos.app.db.insert ("items", item)
				
				var pricing = Jar ()
					 .put ("id", id)
					 .put ("class", "standard")
					 .put ("amount",jar.getDouble ("price"))
					 .put ("price",jar.getDouble ("price"))
					 .put ("cost",  0)
					 .toString ()
				
				id = Pos.app.db.insert ("pos_updates", Jar ()
															 .put ("status", 0)
															 .put ("update_table", "item_prices")
															 .put ("update_id", Pos.app.cloudService.downloadCount () + 5))
				var itemPrice = Jar ()
					 .put ("id", id)
					 .put ("business_unit_id", Pos.app.buID ())
					 .put ("tax_group_id", jar.getInt ("tax_group_id") )
					 .put ("tax_exepmpt", jar.getBoolean ("tax"))
					 .put ("tax_inclusive", false)
					 .put ("item_id", itemID)
					 .put ("price",  jar.getDouble ("price"))
					 .put ("cost",  0)
					 .put ("pricing", pricing)

				item.put ("item_price", itemPrice)
				Pos.app.db.insert ("item_prices", itemPrice)

				if (jar.getInt ("deposit_id") > 0) {

					 id = Pos.app.db.insert ("pos_updates", Jar ()
																  .put ("status", 0)
																  .put ("update_table", "item_links")
																  .put ("update_id", Pos.app.cloudService.downloadCount () + 5))
					 var itemLink = Jar ()
						  .put ("id", id)
						  .put ("item_id", itemID)
						  .put ("item_link_id", jar.getInt ("deposit_id"))
						  .put ("link_type", 0)
					 
					 Pos.app.db.insert ("item_links", itemLink)
				}
				
				
				Control.factory ("Item").action (Jar ()
																 .put ("sku", item.getString ("sku"))
																 .put ("add_item", jar))
				
		  }
		  else {
				
				AddItemView (this, jar.getString ("sku"), departments, deposits, taxes)
		  }
	 }
}
