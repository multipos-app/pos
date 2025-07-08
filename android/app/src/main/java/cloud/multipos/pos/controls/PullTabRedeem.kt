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
import cloud.multipos.pos.net.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.receipts.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.ReportView
import cloud.multipos.pos.views.ConfirmView

import java.util.Date

class PullTabRedeem (): ConfirmControl (), InputListener {
	 
	 var departmentID = 0
	 
	 override fun controlAction (jar: Jar) {

		  jar (jar)
		  if (departmentID == 0) {
		  
				departmentID = jar ().getInt ("department_id")
		  }
		  
		  Logger.d ("redeem... ${jar ()}")
		  if (!Pos.app.ticket.hasItems ()) {

				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("invalid_operation"))
												 .put ("echo_text", ""))
				return
		  }

		  if (confirmed ()) {

				var itemCount = 0

				Logger.d ("redeem... ${jar ()}")
				
				for (ti in Pos.app.ticket.items) {

					 ti
						  .put ("state", TicketItem.REDEEM)
						  .put ("amount", 0.0)
					 	  .put ("department_id", departmentID)

					 val update = """
					 update ticket_items set 
					 state = ${TicketItem.REDEEM}, 
					 amount = 0.0, 
					 department_id = ${departmentID} 
					 where id = ${ti.getInt ("id")}
					 """

					 Pos.app.db.exec (update)
				}
				
				Pos.app.ticket
					 .put ("ticket_type", Ticket.REDEEM)
					 .put ("complete_time", Pos.app.db.timestamp (Date ()))
					 .put ("state", Ticket.COMPLETE)
					 .put ("item_count", itemCount)
					 .put ("total", 0.0)
					 .put ("sub_total", 0.0)
					 .put ("ticket_items", Pos.app.ticket.items)
				
				Pos.app.receiptBuilder.ticket (Pos.app.ticket, PosConst.PRINTER_RECEIPT)
				val ticketText = Pos.app.receiptBuilder.text ()
				
				Pos.app.ticket
					 .put ("ticket_text", ticketText)
				
				Pos.app.ticket.update ()
				
				Logger.x ("redeem... ${Pos.app.ticket}")
				
				ReportView (Pos.app.getString ("ticket"),
								Jar ()
									 .put ("ticket", Pos.app.ticket))
				
				Upload ()
					 .add (Pos.app.ticket)
					 .exec ()
				
				Pos.app.totalsService.q (PosConst.TICKET, Pos.app.ticket, Pos.app.handler)  // queue the ticket to totals
				
				Pos.app.ticket ()  // start a new ticket
				confirmed (false)
				PosDisplays.clear ()
		  }
		  else {

				var prompt = Pos.app.getString ("pull_tab_redeem_prompt") + "?\n"
				
				for (ti in Pos.app.ticket.items) {

					 prompt += "\n${ti.getString ("item_desc")}"
				}

				Logger.d ("redeem prompt... ${prompt}")
				
				ConfirmView (prompt, this)
		  }
	 }
}
