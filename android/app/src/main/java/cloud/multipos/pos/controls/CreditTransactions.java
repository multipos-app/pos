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

public class CreditTransactions extends Control {

	 public CreditTransactions () { super (); }

	 public void controlAction (Jar e) {

		  if (Pos.app.ticket.items.size () > 0) {
				return;
		  }

		  ticketList = new ArrayList <Jar> ();

		  ResultSet ticketResult = Pos.app.db.query ("select t.id, tt.amount, tt.data_capture from tickets t, ticket_tenders tt where t.id = tt.ticket_id and tt.tender_type = 'Credit' order by t.id desc limit 20");
		 
		  while (ticketResult.next ()) {

				Jar dataCapture = new Jar (ticketResult.getString (2));

				Logger.d ("credit sale... " + dataCapture);
				String dc = dataCapture.getString ("card_no");
				
		  		Jar t = new Jar ();
				t.
					 put ("type", Ticket.CREDIT_RECALL).
					 put ("id", ticketResult.getInt (0)).
					 put ("desc", dc).
					 put ("amount", ticketResult.getDouble (1));

		  		ticketList.add (t);
		  }

		  if (ticketList.size () > 0) {
		  }
	 }

	 public Jar at (int position) {
		  if (position < ticketList.size ()) {
				return ticketList.get (position);
		  }
		  return null;
	 }

	 public void select (int selected) {

		  int ticketID = ticketList.get (selected).getInt ("id");

		  // Pos.app.ticket = new Ticket (ticketID);
		  
	 }

	 public int size () {
		  return ticketList.size ();
	 }

	 private ArrayList <Jar> ticketList;
}
