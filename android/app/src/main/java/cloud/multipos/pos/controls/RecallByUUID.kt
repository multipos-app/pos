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
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.views.PosDisplays;

import java.util.Date

class RecallByUUID (): Control () {

	 override fun controlAction (jar: Jar) {

		  jar (jar)

		  val date = Date ()
		  val since: Long = date.getTime () - (120 * 24 * 60 * 60 * 1000).toLong () // last 60 days
		  var sel = "select id from tickets where recall_key = '" + jar.getString ("recall_key") + "' and start_time > '" + Pos.app.db.timestamp (Date (since)) + "'"

		  Logger.x ("recall... <$sel>")
		  
		  val ticketResult = DbResult (sel, Pos.app.db)
		  if (ticketResult.fetchRow ()) {
				
				val t = ticketResult.row ()
								
				Pos.app.ticket = Ticket (t.getInt ("id"), Ticket.RECALLED)
				PosDisplays.update ();
		  }
		  else {
		  
				PosDisplays.message (Pos.app.getString ("ticket_not_found"))
		  }
	 }
}
