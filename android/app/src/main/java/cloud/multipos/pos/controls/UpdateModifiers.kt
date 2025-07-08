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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.views.PosDisplays

class UpdateModifiers (): Control () {

	 override fun controlAction (jar: Jar) {
		  
		  val ti = jar.get ("ticket_item") as TicketItem
		  val modifiers = jar.getList ("modifiers")
		  
		  if (ti.hasAddons ()) {

				// clear previous modifiers
				
				ti.removeAddons (listOf (TicketItemAddon.MODIFIER_PLUS, TicketItemAddon.MODIFIER_MINUS))
		  }
		  
		  for (modifier in modifiers) {
		  
				if (modifier.getInt ("value") != 0) {

					 var addonType = if (modifier.getInt ("value") == 1) TicketItemAddon.MODIFIER_PLUS else TicketItemAddon.MODIFIER_MINUS
		  
					 val tia = TicketItemAddon ()
					 tia
						  .put ("ticket_item_id", ti.getInt ("id"))
						  .put ("addon_id", modifier.getInt ("id"))
						  .put ("addon_type", addonType)
						  .put ("addon_amount", modifier.getDouble ("price"))
						  .put ("addon_quantity", 1)
						  .put ("addon_description", modifier.getString ("item_desc"))
				
					 val id = Pos.app.db.insert ("ticket_item_addons", tia)
					 tia.put ("id", id)
					 ti.addons.add (tia)
				}
				
				Pos.app.ticket.update ()
				updateDisplays ()
		  }
	 }
}
