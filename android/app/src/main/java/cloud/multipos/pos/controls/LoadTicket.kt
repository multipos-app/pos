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

				ticketID = ticketID + jar.getInt ("dir")
				
				val ticketTypes =
					 "(" +
					 Ticket.SALE + "," +
					 Ticket.VOID + "," +
					 Ticket.NO_SALE + "," +
					 Ticket.COMP_SALE + "," +
					 Ticket.RETURN_SALE + "," +
					 Ticket.SALE_NONTAX + "," +
					 Ticket.BANK + "," +
					 Ticket.CREDIT_REFUND + "," +
					 Ticket.CREDIT_REVERSE +
					 ")"
					 
				val ticketStates =
					 "(" +
					 Ticket.OPEN + "," +
					 Ticket.SUSPEND +
					 ")"
				
				var ticketResult: DbResult? = null
				val sel = "select * from tickets where ticket_type in " + ticketTypes + " and id = " + ticketID + " order by id desc"
				
				ticketResult = DbResult (sel, Pos.app.db)
				if (ticketResult.fetchRow ()) {

					 val t = ticketResult.row ()
				}
				else {

					 Logger.w ("attempt to load unknown ticket... " + ticketID)
					 return
				}
				
				Pos.app.ticket = Ticket (ticketID, Ticket.RECALLED)
		  }
		  else if (jar.has ("id")) {

				Pos.app.ticket = Ticket (jar.getInt ("id"), Ticket.RECALLED)
		  }
		  else {

				Pos.app.ticket = Ticket (0, Ticket.OPEN)
		  }

		  if (Pos.app.ticket.has ("customer")) {
				
				Pos.app.posAppBar.customer (Customer (Pos.app.ticket.get ("customer")).display ())
		  }
		  
		  PosDisplays.update ()
		  PosDisplays.home ()
	 }
}
