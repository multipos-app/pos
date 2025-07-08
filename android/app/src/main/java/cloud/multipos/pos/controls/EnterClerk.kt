/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, softwar
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.net.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.views.EnterClerkView

class EnterClerk (): Control (), InputListener {
	 
	 override fun controlAction (jar: Jar?) {
		  
		  jar (jar)
		  
		  EnterClerkView (this,
								Pos.app.getString (R.string.clerk_desc),
								Pos.app.getString (R.string.enter_clerk_number))
	 }

	 override fun accept (clerk: Jar) {
		  					 					 
		  Pos.app.ticket
				.put ("clerk_id", clerk.getInt ("id"))
				.put ("clerk", clerk)
		  
		  // update the server
		  
		  val update = Jar ()
				.put ("clerk_id", clerk.getInt ("id"));
		  
		  if (Pos.app.config.getBoolean ("track_jobs")) {
				
				Pos.app.ticket
					 .put ("state", Ticket.JOB_PENDING)

				update
					 .put ("state", Ticket.JOB_PENDING)
		  }

		  Logger.d ("clerk update... ${update}")
		  
		  Pos.app.db.update ("tickets", Pos.app.ticket.getInt ("id"), update);

		  update
				.put ("action", "clerk_update")
				.put ("uuid", Pos.app.ticket.getString ("uuid"))
		  		  
		  Post ("pos/update-ticket")
				.add (update)
				.exec (fun (result: Jar): Unit { })
		  
		  return
	 }
}
