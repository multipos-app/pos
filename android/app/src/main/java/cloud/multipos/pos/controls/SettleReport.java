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
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.db.*;

import java.util.ArrayList;

public abstract class SettleReport extends Control {

	 public SettleReport () { super (); }

	 abstract void report ();

	 public void controlAction (Jar e) {

		  tenders = new ArrayList <Jar> ();

		  ResultSet ttResult = Pos.app.db.query ("select tt.*, t.state from tickets t, ticket_tenders tt " +
															 "where t.id = tt.ticket_id and " + 
															 "Pos.app.session_id = " + Pos.app.config.getInt ("Pos.app.session_id") + " and " +
															 "(tt.status = " + TicketTender.SETTLED + " or tt.status = " + TicketTender.NOT_SETTLED + ")");
		  while (ttResult.next ()) {
				
				Jar tt = new Jar ();
				Jar dataCapture = new Jar (ttResult.getString (12));

				tt.
					 put ("ticket_id", ttResult.getInt (1)).
					 put ("card_number", Strings.getMaskedCCNum (dataCapture.getString ("card_number"))).
					 put ("auth_code", dataCapture.getString ("auth_code")).
					 put ("amount", ttResult.getDouble (5)).
					 put ("tip", ttResult.getDouble (8)).
					 put ("status", ttResult.getInt (3));

				tenders.add (tt);
		  }

		  report ();
	 }

	 protected ArrayList <Jar> tenders;
}
