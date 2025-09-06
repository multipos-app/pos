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

import java.util.ArrayList
import java.math.BigDecimal
import java.math.RoundingMode

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.*

class SaleDiscount (): TicketModifier () {

	 override fun controlAction (jar: Jar) {
		  
		  var percent = 0.0
		  if (jar.has ("percent")) {

				percent = jar.getDouble ("percent") / 100.0
		  }
		  else {
				
				return
		  }
		  
		  var desc = ""
		  if (jar.has ("receipt_text")) {
				
				desc = jar.getString ("receipt_text")
		  }
		  else {
				desc = Pos.app.getString ("discount")
		  }
		  
		  for (ti in Pos.app.ticket.items) {

				if (ti.getBoolean ("apply_addons") == false) {
					 continue
				}
				
				when (ti.getInt ("state")) {

					 TicketItem.STANDARD, TicketItem.RETURN_ITEM -> {
					 
						  var amount = BigDecimal (-1.0 * (ti.getDouble ("amount") * percent)).setScale (2, RoundingMode.HALF_UP).toDouble ()

						  val tia = TicketItemAddon ()
						  
						  tia
								.put ("ticket_item_id", ti.getInt ("id"))
								.put ("addon_amount", amount)
								.put ("addon_quantity", ti.getInt ("quantity"))
								.put ("addon_description", desc)
						  
						  val id = Pos.app.db.insert ("ticket_item_addons", tia)
						  
						  tia
								.put ("id", id)
						  
						  ti.addons.clear ()
						  ti.addons.add (tia)
					 }				 
				}
		  }
				
		  
		  Pos.app.ticket.update ()
		  updateDisplays ()
	 }
}
	 
