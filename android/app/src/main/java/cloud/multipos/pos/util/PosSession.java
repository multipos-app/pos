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
 
package cloud.multipos.pos.util;

import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.db.*;

public class PosSession extends Jar {

	 public PosSession () {

		  int tabs = 0;
		  ResultSet ticketResult = Pos.app.db.query ("select id, tag from tickets where state = " + Ticket.SUSPEND);
		  while (ticketResult.next ()) {
				
				tabs ++;
		  }

		  put ("session_id", Pos.app.config.getInt ("pos_session_id"));
		  put ("open_tabs", tabs);
	 }
}
