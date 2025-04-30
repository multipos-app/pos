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
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.views.ReportView

class PrintReceipt (): Control () {

	 override fun controlAction (jar: Jar) {

		  if (Pos.app.ticket.hasItems () ||
				(Pos.app.ticket.getInt ("ticket_type") == Ticket.X_SESSION) ||
				(Pos.app.ticket.getInt ("ticket_type") == Ticket.Z_SESSION)) {
				
				Pos.app.receiptBuilder.ticket (Pos.app.ticket, PosConst.PRINTER_RECEIPT)
				Pos.app.ticket.put ("ticket_text", Pos.app.receiptBuilder.text ())
				ReportView (Pos.app.getString ("ticket"),
								Jar ()
									 .put ("ticket", Pos.app.ticket)
									 .put ("print_opt", true))
		  }
	 }
}
