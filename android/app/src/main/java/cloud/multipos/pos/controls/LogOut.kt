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
import cloud.multipos.pos.views.PosDisplays;

import java.util.Date

class LogOut (): TicketModifier () {

	 override fun controlAction (jar: Jar?) {
		  
		  if (Pos.app.ticket.hasItems ()) {
				
				PosDisplays.message (Pos.app.getString ("complete_ticket"))
				return
		  }

		  Pos.app.ticket
				.put ("ticket_type", Ticket.LOGOUT)
				.put ("state", Ticket.COMPLETE)
				.put ("star_time", Pos.app.db.timestamp (Date ()))
				.put ("complete_time", Pos.app.db.timestamp (Date ()))
				.put ("data_capture", jar.toString ())
				.update ()
				.complete ()
		  
		  Pos.app.employee = Employee (Jar ())
		  Pos.app.logout ();
	 }
}
