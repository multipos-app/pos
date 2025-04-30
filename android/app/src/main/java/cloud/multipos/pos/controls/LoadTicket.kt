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
import cloud.multipos.pos.views.*
import cloud.multipos.pos.db.*

import java.util.ArrayList

class LoadTicket (): Control () {

	 override fun controlAction (jar: Jar) {
		  
		  var ticketID = Pos.app.ticket.getInt ("id")
		  
		  if (jar.has ("dir")) {

				ticketID = Pos.app.ticket.getInt ("id")
				var sel = ""
				
				when (jar.getInt ("dir")) {

					 0 -> { sel = "select id from tickets where id = (select max(id) from tickets)" }
					 -1 -> { sel = "select id from tickets where ticket_type = ${Ticket.SALE} and id < ${ticketID} order by id desc" }
					 1 -> { sel = "select id from tickets where ticket_type = ${Ticket.SALE} and id > ${ticketID} order by id desc" }
				}
				
				val ticketResult = DbResult (sel, Pos.app.db)
				if (ticketResult.fetchRow ()) {

					 val t = ticketResult.row ()
					 ticketID = t.getInt ("id")
					 
					 Logger.d ("load ticket... ${ticketID} ${jar.getInt ("dir")} ${sel}");
					 
				}
				else {

					 Logger.w ("attempt to load unknown ticket... " + ticketID)
					 return
				}
				
				Pos.app.ticket = Ticket (ticketID, Ticket.RECALLED)
		  }

		  PosDisplays.clear ()
		  PosDisplays.update ()
		  PosDisplays.home ()
	 }
}
