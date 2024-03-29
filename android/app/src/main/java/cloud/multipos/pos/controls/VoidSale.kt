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
import cloud.multipos.pos.views.ConfirmView
import cloud.multipos.pos.db.DB
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.devices.DeviceManager;



class VoidSale (): CompleteTicket () {

	 override fun controlAction (jar: Jar?) {
		  
		  if (confirmed ()) {

				val timeStamp = Pos.app.db.timestamp (java.util.Date ())

				DeviceManager.customerDisplay?.clear ()  // clear the customer display
				
				Pos.app
					 .ticket
		  			 .put ("ticket_type", Ticket.VOID)
		  			 .put ("state", Ticket.COMPLETE)
		  			 .put ("star_time", timeStamp)
		  			 .put ("complete_time", timeStamp)
				
				Pos.app.db.exec ("update tickets set start_time = '" + timeStamp + "', " +
											  "complete_time = '" + timeStamp + "', " +
											  "ticket_type = " + Ticket.VOID + ", " +
											  "state = " + Ticket.COMPLETE + " " +
											  "where id = " + Pos.app.ticket.getInt ("id"))

		  		confirmed (false)
				
		  		completeTicket (Ticket.COMPLETE)
		  		Pos.app.ticket ()
		  }
		  else {

				ConfirmView (Pos.app.getString ("void_sale"), this)
		  }
	 }
	 
	 override fun printReceipt (): Boolean { return false }
	 override fun openDrawer (): Boolean { return false }
	 
}
