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
 
package cloud.multipos.pos.controls;

import cloud.multipos.pos.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.util.*;

import java.util.Date;

public class CompSale extends ConfirmControl {

	 public CompSale () { super (); }

	 public void controlAction (Jar e) {

		  if (Pos.app.ticket.items.size () != 0) {
				return;
		  }

		  if (confirmed ()) {

				Pos.app.ticket.
					 put ("startime_time", Pos.app.db.timestamp (new Date ())).
					 put ("ticket_type", Ticket.COMP_SALE);
				
				Pos.app.db.exec ("update tickets set ticket_type = " + Ticket.COMP_SALE + " where id = " + Pos.app.ticket.getLong ("id"));
				Pos.app.totalsService.q (PosConst.TICKET, Pos.app.ticket, Pos.app.handler);
				confirmed (false);

		  }
		  else {

				setJar (e);
				confirmText (Pos.app.getString ("comp_sale"));
		  }
	 }
}
