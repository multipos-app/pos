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

import cloud.multipos.pos.Pos
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*

class SplitTicket (): Control () {

	 override fun controlAction (p: Jar) {

		  Pos.app.ticket!!.state (Ticket.SUSPEND)

		  var split = Pos.app.ticket!!
		  var splitItems: MutableList <TicketItem> = mutableListOf ()

		  for (index in Pos.app.selectValues) {
				
		  		Logger.x ("copy... " + index)
		  		splitItems.add (split.items.get (index))
		  }
		  
		  Pos.app.ticket ()

		  for (ti in splitItems) {
		
		  		split.items.remove (ti)
				
		  		Logger.x ("move... " + ti.getInt ("id") + " " + ti.getString ("item_desc"))
				
		  		Pos.app.ticket!!.items.add (ti)
		  		Pos.app.db.exec ("update ticket_items set ticket_id = " +
											  Pos.app.ticket!!.getInt ("id") +
											  " where id = " + ti.getInt ("id"))
		  }

		  Pos.app.ticket!!.update ()
		  updateDisplays ()
	 }
}
