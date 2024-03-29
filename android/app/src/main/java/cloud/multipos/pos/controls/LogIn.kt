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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.*

import java.util.Date

class LogIn (): CompleteTicket () {

	 override fun controlAction (jar: Jar) {
		  
		  Pos.app.login ()

		  Pos.app.employee = Employee (jar)

		  if (Pos.app.ticket!!.hasItems ()) {

				return;
		  }

		  Pos.app.ticket!!
				.put ("ticket_type", Ticket.LOGIN)
				.put ("state", Ticket.COMPLETE)
				.put ("star_time", Pos.app.db.timestamp (Date ()))
				.put ("complete_time", Pos.app.db.timestamp (Date ()))
		  
		  Pos.app.db.exec ("update tickets set start_time = '" + Pos.app.db.timestamp (Date ()) + "', " +
		  								 "complete_time = '" + Pos.app.db.timestamp (Date ()) + "', " +
		  								 "state = " + Ticket.COMPLETE + ", " +
		  								 "ticket_type = " + Ticket.VOID + " where id = " + Pos.app.ticket!!.getInt ("id"))
		  
		  
		  completeTicket (Ticket.COMPLETE)
		 
		  while (Pos.app.controls.size > 0) {
				
				var control = Pos.app.controls.removeFirst ()
		  		control.action (control.jar ())
		  }
	 }
	 
	 override fun openDrawer (): Boolean { return false; }
	 override fun printReceipt (): Boolean { return false }
}
