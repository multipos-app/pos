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

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.*

class EnterClerk (): TicketModifier (), InputListener {
	 
	 override fun controlAction (jar: Jar?) {

		  jar (jar)
		  // numberDialog = NumberDialog (this, R.string.enter_clerk_number, NumberDialog.INTEGER, true)
	 }

	 override fun accept (result: Jar) {

		  var clerkNo = result.getInt ("value")													 
		  
		  if (clerkNo > 0) {
				
				val employeeResult  = DbResult ("select * from employees where username = '" +  clerkNo + "'", Pos.app.db)
				if (employeeResult.fetchRow ()) {
				
					 var clerk = Employee (employeeResult.row ())
					 					 
		  			 Pos.app.ticket.put ("clerk_id", clerk.getInt ("id"))
		  			 Pos.app.ticket.put ("clerk", clerk)
					 Pos.app.db.update ("tickets", Pos.app.ticket.getInt ("id"), Jar ().put ("clerk_id", clerk.getInt ("id")))
					 Pos.app.receiptBuilder.ticket (Pos.app.ticket, PosConst.PRINTER_RECEIPT)
					 Pos.app.ticket.put ("ticket_text", Pos.app.receiptBuilder.text ())

					 for (p in 1..jar ().getInt ("print_receipt")) {
						  
						  Pos.app.receiptBuilder ().print ();
					 }

					 Control.factory ("Suspend").action (Jar ())
					 return
				}
				else {
					 
		  			 // numberDialog.prompt (Pos.app.getString ("invalid_clerk_number"))
				}

				updateDisplays ()
		  }
	 }
}
