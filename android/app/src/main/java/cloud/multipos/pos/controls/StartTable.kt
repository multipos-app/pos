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
import cloud.multipos.pos.db.*

import java.util.Date

class StartTable (): Suspend (), InputListener {

	 override fun controlAction (jar: Jar) {

		  Logger.d ("start table... ")
		  
		  jar (jar)
		  // numberDialog = NumberDialog (this, R.string.enter_table_number, NumberDialog.INTEGER, true)
	 }
	 
	 override fun accept (result: Jar) {

		  var tableNo = result.getInt ("value")

		  val tableResult  = DbResult ("select * from tables where table_no = " +  tableNo, Pos.app.db)
		  
		  if (tableResult.fetchRow ()) {
				
				var table =  tableResult.row ()
		  }
		  else {

				var id = Pos.app.db.insert ("tables", Jar ()
																  .put ("status", 1)
																  .put ("table_no", tableNo)
																  .put ("ticket_id", Pos.app.ticket.getInt ("id")))
		  }

	 }
	 
	 override fun printReceipt (): Boolean { return false }
	 override fun openDrawer (): Boolean { return false }
}
