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

public class Settle extends Control {

	 public Settle () { super (); }

	 public void controlAction (Jar e) {

		  ArrayList <TicketTender> tenders = new ArrayList <TicketTender> ();

		  ResultSet ttResult = Pos.app.db.query (
				"select count(*) from tickets t, ticket_tenders tt " +
				" where t.id = tt.ticket_id and (tt.status = " + TicketTender.NOT_SETTLED + " or tt.status = " + TicketTender.SETTLED + ")" +
				" and t.pos_session_id = " + Pos.app.config.getInt ("pos_session_id"));

		  if (ttResult.next ()) {

				if (ttResult.getInt (0) > 0) {
				}
		  }
	 }

	 public boolean beforeAction () {

		  if (Pos.app.ticket.items.size () > 0) {
				return false;
		  }
		  return true;
	 }
}

