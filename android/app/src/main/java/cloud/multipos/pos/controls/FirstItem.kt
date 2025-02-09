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
import cloud.multipos.pos.views.ReportView
import cloud.multipos.pos.devices.DeviceManager;
import cloud.multipos.pos.views.PosDisplays

import java.util.Date

open abstract class FirstItem (): TicketModifier () {
	 
	 override fun beforeAction (): Boolean {
		  
		  if (!Pos.app.ticket.hasItems ()) {
									 				
				DeviceManager.customerDisplay?.clear ()  // clear the previous sale from the customer display
				PosDisplays.clear ()

				// update ticket start time to first item entered
				
				Pos.app.ticket.put ("start_time", Pos.app.db.timestamp (Date ()))
				Pos.app.db.exec ("update tickets set start_time = '" + Pos.app.db.timestamp (Date ()) + "' " + " where id = " + Pos.app.ticket.getInt ("id"))
		  
				if (Pos.app.config.getBoolean ("enter_clerk")) {
				
					 Control.factory ("EnterClerk").action (Jar ())
				}	
		  }

		  return super.beforeAction () 
	 }
}
