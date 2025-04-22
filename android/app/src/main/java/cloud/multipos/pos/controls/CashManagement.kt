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
import cloud.multipos.pos.views.CashManagementView
import cloud.multipos.pos.views.PosDisplays

class CashManagement (): Control () {

	 lateinit var sessionManager: SessionManager

	 init {
	 }
	 
	 override fun controlAction (jar: Jar) {
 
		  var control = 0.0
		  val sessionID = Pos.app.config.getInt ("pos_session_id")
		  sessionManager = SessionManager ()
		  
		  // get/setup any currnent counts
		  
		  if (Pos.app.drawerCounts.size == 0) {
				
				val denomResult  = DbResult ("select cd.* from currencies c, currency_denoms cd " +
													  "where c.id = cd.currency_id and c.default_currency = 'true' order by denom", Pos.app.db)
				
				while (denomResult.fetchRow ()) {

					 var denom = denomResult.row ()

					 var count: Jar
					 
					 val countResult =  DbResult ("select * from pos_session_counts " +
															"where pos_session_id = " + Pos.app.ticket.getInt ("session_id") + " and denom_id = " + denom.getInt ("id"), Pos.app.db)
					 if (countResult.fetchRow ()) {
						  
						  count = countResult.row ()
						  count.put ("denom", denom)
						  Pos.app.drawerCounts.add (count)
						  control += (count.getInt ("denom_count") * count.get ("denom").getDouble ("denom")) / 100.0
					 }
					 else {
						  
						  count =  Jar ()
								.put ("pos_session_id", sessionID)
								.put ("denom_id", denom.getInt ("id"))
								.put ("denom_count", 0)
						  
						  Pos.app.db.insert ("pos_session_counts", count)
						  count.put ("denom", denom)
						  Pos.app.drawerCounts.add (count)
					 }
				}
		  }
		  else {

				for (c in Pos.app.drawerCounts) {
					 
					 control += (c.getInt ("denom_count") * c.get ("denom").getDouble ("denom")) / 100.0
				}
		  }

		  if (Pos.app.drawerCounts.size == 0) {

				PosDisplays.message (Pos.app.getString ("no_currency_denominations"))
				return
		  }

		  sessionManager.action (Jar ().put ("report_only", true))
		  CashManagementView (sessionManager)
	 }
}

