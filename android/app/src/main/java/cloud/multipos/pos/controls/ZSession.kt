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
import cloud.multipos.pos.devices.DeviceManager
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.ConfirmView
import cloud.multipos.pos.views.ReportView
import cloud.multipos.pos.views.DialogControl

class ZSession (): SessionManager () {

	 init {
		  
		  confirmed (false)
	 }

	 override fun controlAction (jar: Jar) {
		  
		  if (confirmed ()) {

		  		confirmed (false)
				setJar (Jar ().put ("print_report", true))
				
				super.controlAction (jar ())  // build the report

				Pos.app.ticket.put ("ticket_type", Ticket.Z_SESSION)
				Pos.app.receiptBuilder ().ticket (Pos.app.ticket, PosConst.PRINTER_REPORT)

				complete (Ticket.Z_SESSION)
								
				if (Pos.app.config.getBoolean ("session_logout")) {
					 
					 // log cashier off
					 
					 Pos.app
						  .ticket ()
						  .logout ()
				}
		  }
		  else {
				
				jar (jar)
				ConfirmView (Pos.app.getString ("confirm_z_session"), this)
		  }
	 }

	 override fun openDrawer (): Boolean { return true }
}
