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
import cloud.multipos.pos.views.ReportView

class ZSession (): SessionManager () {

	 init {
		  
		  confirmed (false)
	 }

	 override fun controlAction (jar: Jar) {
		  
		  if (confirmed ()) {

		  		confirmed (false)
				
				super.controlAction (Jar ())  // build the report

				Pos.app.receiptBuilder ().ticket (Pos.app.ticket, PosConst.PRINTER_REPORT)
				val report = Pos.app.receiptBuilder ().text ()
				// Pos.app.receiptBuilder ().print ()
				
				close (Ticket.Z_SESSION)  // close session and start a new one
	
				// if (Pos.app.config.getBoolean ("session_logout")) {
				if (true) {
					 
					 // log cashier off

					 Pos.app.ticket ()
					 //Control.factory ("LogOut").action (Jar ())
				}
				else {

					 // just show the report and continue
					 
					 ReportView (Pos.app.getString ("ticket"),
									  Jar ()
		  									.put ("report_title", Pos.app.getString ("z_report"))
		  									.put ("report_text", report)
		  									.put ("print_opt", true)
		  									.put ("print_type", "report"))
				}
		  }
		  else {
				
				jar (jar)
				ConfirmView (Pos.app.getString ("confirm_z_session"), this)
		  }
	 }

	 override fun openDrawer (): Boolean { return true }
}
