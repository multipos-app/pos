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

import java.util.*;

import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.services.*;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class CashDrawerCount extends CompleteTicket {

	 public CashDrawerCount () { super (); }
	 
	 public boolean openDrawer () { return false; }
	 public boolean printReceipt () { return false; }

	 public void controlAction (Jar e) {
		  
		  int sessionID = Pos.app.config.getInt ("pos_session_id");
		  
		  for (int i=0; i<Pos.app.drawerCounts.size (); i++) {

				Jar count = Pos.app.drawerCounts.get (i);

				Logger.d ("count... " + count);
		  }

		  // TicketTender ticketTender = new TicketTender ();
		  // ticketTender
		  // 		.put ("ticket_id", Pos.app.ticket.getLong ("id"))
		  // 		.put ("data_capture", Pos.app.drawerCounts.toString ());
		  
		  // long id = Pos.app.db.insert ("ticket_tenders", ticketTender);
		  // ticketTender.put ("id", id)
		  // 		.put ("type", Ticket.TENDER);
		  
		  // Pos.app.ticket.getList ("ticket_tenders").add (ticketTender);
		  // Pos.app.ticket.put ("ticket_type", Ticket.DRAWER_COUNT);
		  
		  // completeTicket (Ticket.COMPLETE);	 
		  // Pos.app.vars.clear ();

	 }

	 public String toText () {

		  
		  // String totalFormat = "%" + Pos.app.printerService ().quantityWidth () + "d %-" + Pos.app.printerService ().descWidth () + "s%" + Pos.app.printerService ().amountWidth () + "s";
		  // StringBuffer sb = new StringBuffer ("\n");

		  // switch (sessionType) {
		  // case Ticket.X_SESSION:
		  // 		sb.append (Strings.center (Pos.app.getString ("x_report"), Pos.app.printerService ().width (), " ") ); 
		  // 		break;
		  // case Ticket.Z_SESSION:
		  // 		sb.append (Strings.center (Pos.app.getString ("z_report"), Pos.app.printerService ().width (), " ")); 
		  // 		break;
		  // }

		  // sb.append ("\n\n\n");
		  
		  
		  // for (int i = 0; i < totalsList.size (); i ++) {

		  // 		Jar t = (Jar) totalsList.at (i);

		  // 		String desc = t.getString ("desc");
		  // 		if (desc.length () > Pos.app.printerService ().descWidth ()) {
		  // 			 desc = desc.substring (0, Pos.app.printerService ().descWidth ());
		  // 		}
				
		  // 		switch (t.getInt ("type")) {
					 
		  // 		case Ticket.CENTER:
		  // 			 sb.append (Strings.center (" " + desc + " ", Pos.app.printerService ().width (), "*") + "\n"); 

		  // 			 break;
					 
		  // 		case Ticket.SESSION_TOTAL:

		  // 			 sb.append (String.format (totalFormat,
		  // 												t.getInt ("quantity"),
		  // 												desc,
		  // 												Strings.currency (Strings.round (t.getDouble ("amount")), false) + "\n"));
		  // 			 break;
		  // 		}
		  // }
		  // return sb.toString ();

		  return null;
	 }

	 
	 public void complete (int sessionType) {

		  // String timestamp = DB.timestamp ();

		  // Pos.app.ticket.put ("state", Ticket.COMPLETE);
		  // Pos.app.ticket.put ("complete_time", Pos.app.ticket.getString ("start_time"));

		  // Pos.app.db.exec ("update tickets set state = " + Ticket.COMPLETE + ", " +
		  // 								 "start_time = '" + DB.timestamp () + "', " +
		  // 								 "ticket_type = " + sessionType + " where id = " + Pos.app.ticket.getLong ("id"));

		  // setConfirmed (true);
	 }

	 private static ArrayList <Jar> totals;
	 protected int sessionType = 0;
}
