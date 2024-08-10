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
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.models.Ticket;
import cloud.multipos.pos.services.TotalsService;
import cloud.multipos.pos.views.PosDisplays;
import cloud.multipos.pos.net.Upload;

import java.util.Date;

public class OpenAmount extends DefaultItem implements InputListener {

	 public OpenAmount () { }

	 public void controlAction (Jar Jar) {

		  if (Pos.app.ticket.hasItems ()) {
				
				PosDisplays.message (Pos.app.getString ("invalid_operation"));
				return;
		  }

		  jar (jar);

		  if (Pos.app.input.hasInput ()) {
				
				accept (new Jar ().put ("value", Pos.app.input.getDouble ()));
				return;
		  }
		  
		  // new NumberDialog (this, R.string.enter_amount, NumberDialog.CURRENCY, true)
		  // 		.value (0)
		  // 		.show ();
		  
	 }
	 
	 public void accept (Jar result) {

		  double amount = result.getDouble ("value");

		  int sessionID = Pos.app.config.getInt ("pos_session_id");

		  if (amount > 0) {
				
				DbResult sessionResult = new DbResult ("select * from pos_session_totals where pos_session_id = " + sessionID + " and total_type = " + TotalsService.BANK, Pos.app.db);
				if (sessionResult.fetchRow ()) {

					 Jar total =  sessionResult.row ();

					 total.put ("amount", amount);
					 Pos.app.db.update ("pos_session_totals", total);
				}

				Pos.app.ticket
					 .put ("ticket_type", Ticket.OPEN_AMOUNT)
					 .put ("state", Ticket.COMPLETE)
					 .put ("start_time", Pos.app.db.timestamp (new Date ()))
					 .put ("total", amount)
					 .put ("complete_time", Pos.app.db.timestamp (new Date ()));
				
				Pos.app.db.exec ("update tickets set start_time = '" + Pos.app.db.timestamp (new Date ()) + "', " +
											  "complete_time = '" + Pos.app.db.timestamp (new Date ()) + "', " +
											  "state = " + Ticket.COMPLETE + ", " +
											  "ticket_type = " + Ticket.BANK + " where id = " + Pos.app.ticket.getInt ("id"));
				
				new Upload ()
					 .add (Pos.app.ticket)
					 .exec ();
				
				Pos.app.ticket ();
				updateDisplays ();
		  }
	 }
	 
	 public String type () { return ""; }
}
