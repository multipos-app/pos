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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.views.PosDisplays

import java.util.Date

class Recall (): Control () {

	 override fun controlAction (jar: Jar) {

		  jar (jar)

		  if (jar.has ("id"))  {
		  
				Pos.app.ticket = Ticket (jar.getInt ("id"), Ticket.SUSPEND)
				PosDisplays.update ()
		  }
		  else {
				
				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("invalid_ticket_id"))
												 .put ("echo_text", ""))
		  }
	 }
}
