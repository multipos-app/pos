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

import java.util.Date

open class Suspend (): TicketModifier () {

	 override fun controlAction (jar: Jar) {

		  if (!Pos.app.ticket.hasItems ()) {  // nothing to suspend
															 
				return
		  }

		  var state = Ticket.SUSPEND

		  when (Pos.app.ticket.getInt ("state")) {

				Ticket.JOB_PENDING,
				Ticket.JOB_COMPLETE -> state = Pos.app.ticket.getInt ("state")
		  }

		  Pos.app.ticket
				.put ("state", state)
				.update ()
				.complete ()  // suspend and clear the displays
		  
		  Control.factory ("LoadTicket").action (Jar ().put ("params", Jar ().put ("dir", 0)))  // load home ticket
	 }

	 override fun openDrawer (): Boolean { return false }
	 override fun printReceipt (): Boolean { return false }
}
