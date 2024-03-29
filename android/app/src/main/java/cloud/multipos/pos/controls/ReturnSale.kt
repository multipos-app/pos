/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.ConfirmView
import cloud.multipos.pos.views.PosDisplays

class ReturnSale (): ConfirmControl () {

	 init { }
	 
	 override fun controlAction (jar: Jar) {

		  confirmed (true)

		  if (confirmed ()) {

				var ticketType = Ticket.SALE
				if (Pos.app.ticket.getInt ("ticket_type") == Ticket.SALE) {

					 ticketType = Ticket.RETURN_SALE
				}
				
				Pos.app.ticket
					 .put ("startime_time", Pos.app.db.timestamp (java.util.Date ()))
					 .put ("ticket_type", ticketType)
				
				Pos.app.db.update ("tickets", Pos.app.ticket)
					 
				confirmed (false)
								
				if (Pos.app.ticket.getInt ("ticket_type") == Ticket.RETURN_SALE) {

					 Logger.d ("return sale on... ")
					 PosDisplays.message (Pos.app.getString ("return_sale"))
				}
				else {

					 Logger.d ("return sale off... ")
					 PosDisplays.message ("")
				}
		  }
		  else {

				setJar (jar)
				ConfirmView (Pos.app.getString ("return_sale"), this)
		  }
	 }
}
