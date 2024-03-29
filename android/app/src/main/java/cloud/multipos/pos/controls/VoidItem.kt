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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.devices.DeviceManager;

import java.util.ArrayList

class VoidItem (): Control () {

	 override fun controlAction (jar: Jar) {

		  if (!Pos.app.ticket.hasItems ()) {
				
				PosDisplays.message (Pos.app.getString ("invalid_operation"))
				return
		  }
	  
		  for (ti in Pos.app.ticket.selectItems ()) {

				Logger.d ("void select items.. " + ti.getString ("item_desc"))
				
				ti.put ("state", TicketItem.VOID_ITEM)
				Pos.app.db.exec ("update ticket_items set state = " + TicketItem.VOID_ITEM + " where id = " + ti.getInt ("id"))
				
				// void the links/ deposits
				
				if (ti.hasLinks ()) {

					 for (link in ti.links) {
						  
						  link.put ("state", TicketItem.VOID_ITEM)
						  Pos.app.db.exec ("update ticket_items set state = " + TicketItem.VOID_ITEM + " where id = " + link.getInt ("id"))
					 }
				}

		  }

			// do some cleanup, clear selection and set the current item to the last non-void item in the ticket
		  
		  for (ti in Pos.app.ticket.items) {
				
				if (ti.getInt ("state") != TicketItem.VOID_ITEM) {
					 
					 Pos.app.ticket.currentItem = ti
				}
		  }
		  
		  Pos.app.selectValues.clear ()
		  Pos.app.ticket.applyAddons (0)				
		  Pos.app.ticket.update ()				
		  updateDisplays ()
		  DeviceManager.customerDisplay?.update (Pos.app.ticket)
	 }
}
