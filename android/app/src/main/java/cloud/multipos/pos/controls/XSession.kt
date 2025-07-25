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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.net.*
import cloud.multipos.pos.views.ReportView

class XSession(): SessionManager () {

	 init {

		  confirmed = true
	 }

	 override fun controlAction (jar: Jar) {
		  
		  super.controlAction (Jar ())  // compile session totals

		  Pos.app.receiptBuilder ().ticket (Pos.app.ticket, PosConst.PRINTER_REPORT)
		  val report = Pos.app.receiptBuilder ().text ()
		  
		  Pos.app.ticket
				.put ("state", Ticket.COMPLETE)
				.put ("ticket_type", Ticket.X_SESSION)
				.put ("complete_time", Pos.app.ticket.getString ("start_time"))
				.put ("ticket_text", report)
				.complete ()

		  // display the report
		  
		  ReportView (Pos.app.getString ("ticket"),
						  Jar ()
		  						.put ("report_title", Pos.app.getString ("x_report"))
		  						.put ("report_text", report)
		  						.put ("print_opt", true)
		  						.put ("print_type", "report"))
	 }
}
