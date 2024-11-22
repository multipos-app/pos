/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.util.extensions.*

class PriceCheck (): Control () {
		  
	 override fun controlAction (jar: Jar) {

		  var input = Pos.app.input.getString ()
						  
		  if (input.length > 0) {
	 				
				var select =
					 "select items.id, " +
					 "items.sku, " +
					 "items.department_id, " +
					 "items.item_desc, " +
					 "item_prices.*, " +
					 "departments.is_negative, " +
					 "addon_links.* " +
					 "from departments, item_prices, items left join addon_links on items.id = addon_links.item_id " +
					 "where items.department_id = departments.id and items.id = item_prices.item_id and sku = '" + input + "'"
				
				val itemResult  = DbResult (select, Pos.app.db)
				if (itemResult.fetchRow ()) {
					 
					 var item = itemResult.row ()
					 if (item.getDouble ("price") != 0.0) {

						  PosDisplays.message (item.getString ("sku") + " " +
													  item.getString ("item_desc") + " " +
													  item.getDouble ("price").currency ())
					 }
				}
				else {
					 PosDisplays.message (Pos.app.getString ("item_not_found"))
				}
		  }
		  else {
				
				if (Pos.app.controls.size == 0) {
					 Pos.app.controls (this)
					 PosDisplays.message (Pos.app.getString ("scan_or_enter_sku"))
				}
		  }
	 }
}
