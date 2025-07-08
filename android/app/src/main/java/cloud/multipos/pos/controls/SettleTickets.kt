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
import cloud.multipos.pos.models.*
import cloud.multipos.pos.views.SettleTicketsView
import cloud.multipos.pos.views.PosDisplays

class SettleTickets (): Control (), InputListener {

	 override fun controlAction (jar: Jar) {

		  val tickets = ArrayList <Jar> ()

		  var count = 0
		  var select = """
		  select 
		  id, 
		  ticket_no, 
		  complete_time as date_time,
		  state,
		  uuid,
		  total,
		  ticket_text
		  from 
		  tickets
		  where 
		  state = ${Ticket.CREDIT_PENDING} 
		  order by id asc
		  """
		  
		  val ticketsResult = DbResult (select, Pos.app.db)
		  
		  while (ticketsResult.fetchRow ()) {

				var ticket = ticketsResult.row ()
				
				tickets.add (ticket)
				count ++
		  }

		  if (count > 0) {

				SettleTicketsView (this, "", tickets)
		  }
		  else {

				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("no_open_tickets"))
												 .put ("echo_text", ""))
		  }
	 }

	 override fun accept (result: Jar) {

		  val tickets = result.getList ("tickets")

		  for (t in tickets) {
				
				if (t.getDouble ("tip") > 0) {
					 
					 Logger.d ("settle... ${t.getInt ("id")} ${t.getDouble ("total")} ${t.getDouble ("tip")}")
					 
				}
		  }
	 }
}
