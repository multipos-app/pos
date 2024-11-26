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
						  
		  if (jar.has ("scan")) {
	 								
				var select = "select i.id, i.sku, i.item_desc, ip.* " +
				"from " +
				"items i, item_prices ip " +
				"where i.id = ip.item_id " +
				"and i.sku = '" + jar.getString ("scan") + "'"
				
				val itemResult  = DbResult (select, Pos.app.db)
				if (itemResult.fetchRow ()) {
					 
					 var item = itemResult.row ()
					 
					 if (item.getDouble ("price") != 0.0) {
						  
						  PosDisplays.message (Jar ()
															.put ("prompt_text", item.getString ("item_desc"))
															.put ("echo_text", item.getDouble ("price").currency ()))
					 }
				}
				else {

					 Pos.app.controls.pop ()
					 PosDisplays.message (Jar ()
													  .put ("prompt_text", Pos.app.getString ("item_not_found"))
													  .put ("echo_text", ""))
				}
		  }
		  else {

				if (Pos.app.controls.size == 0) {
					 
					 Pos.app.controls.push (this)
					 PosDisplays.message (Jar ()
													  .put ("prompt_text", Pos.app.getString ("scan_or_enter_sku"))
													  .put ("echo_text", ""))
				}
		  }
	 }
}
