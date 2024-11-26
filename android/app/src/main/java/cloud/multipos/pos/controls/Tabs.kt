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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.views.TabsView
import cloud.multipos.pos.views.PosDisplays

class Tabs (): Suspend (), InputListener {

	 override fun controlAction (jar: Jar) {

		  val tabs = ArrayList <Jar> ()

		  var count = 0
		  val tabsResult = DbResult ("select * from tickets where state = ${Ticket.SUSPEND} order by id asc", Pos.app.db)
		  while (tabsResult.fetchRow ()) {
				
				tabs.add (tabsResult.row ())
				count ++
		  }

		  if (count > 0) {
				
				TabsView (this, Pos.app.getString ("open_tabs"), tabs)
		  }
		  else {

				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("no_suspended_tickets"))
												 .put ("echo_text", ""))
		  }
	 }
	 
	 override fun accept (result: Jar) {

		  if (result.has ("ticket")) {

				val ticket = result.get ("ticket")

				Logger.d ("ticket... ${ticket.getInt ("id")}")

				var sel = "select id from tickets where id = " + ticket.getInt ("id")
		  
				val ticketResult = DbResult (sel, Pos.app.db)
				if (ticketResult.fetchRow ()) {
				
					 val t = ticketResult.row ()
								
					 Pos.app.ticket = Ticket (t.getInt ("id"), Ticket.RECALLED)
					 PosDisplays.update ();
				}
		  }
	 }
}
