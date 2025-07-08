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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.views.ItemModifiersView

class ItemModifiers (): Control (), InputListener {

	 override fun controlAction (jar: Jar) {

		  val mods = mutableListOf <Jar> ()
		  val ti = jar.get ("ticket_item") as TicketItem
		  
		  // get all modifiers
		  
		  val sel = """
		  select i.id, i.sku, i.item_desc, ip.price, ip.cost 
		  from departments d, items i, item_prices ip 
		  where d.department_id = ${ti.getInt ("department_id")} and department_type = ${Department.MODIFIERS} and i.department_id = d.id and i.id = ip.item_id 
		  order by sku asc
		  """
		  
		  val modsResult = DbResult (sel, Pos.app.db)
		  var pos = 0;
		  
		  while (modsResult.fetchRow ()) {

				var mod = modsResult.row ()

				mod.put ("value", 0)
				
				if (ti.hasAddons ()) {
					 
					 for (tia in ti.addons) {

						  if (tia.getInt ("addon_id") == mod.getInt ("id")) {

								when (tia.getInt ("addon_type")) {

									 TicketItemAddon.MODIFIER_PLUS -> {
										  
										  mod.put ("value", 1)
									 }
									 
									 TicketItemAddon.MODIFIER_MINUS -> {
										  
										  mod.put ("value", -1)
									 }
								}
						  }
					 }
				}
		  
				mods.add (mod)
		  }

		  if (mods.size > 0) {
				
				ItemModifiersView (ti, mods)
		  }
	 }
}
