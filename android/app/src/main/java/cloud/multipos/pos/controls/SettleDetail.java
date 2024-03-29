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

public class SettleDetail extends SettleReport {

	 public SettleDetail () { super (); }

	 public void report () {

		  ArrayList <Jar> reportLines = new ArrayList <Jar> ();

		  for (Jar tender: tenders) {

				Jar tt = new Jar ();
				
				String settleStatus = " ";
				switch (tender.getInt ("status")) {

				case TicketTender.SETTLED:
					 settleStatus = "*";
					 break;
				}

				tt.
					 put ("ticket_id", tender.getInt ("ticket_id")).
					 put ("card_number", tender.getString ("card_number")).
					 put ("auth_code", tender.getString ("auth_code")).
					 put ("amount", tender.getDouble ("amount")).
					 put ("tip", tender.getDouble ("tip")).
					 put ("type", Ticket.SETTLE_DETAIL_LINE).
					 put ("status", settleStatus);
							
				reportLines.add (tt);
				
				Logger.d ("tt... " + tt);
		  }

		  Jar report = new Jar ();
		  report.
				put ("Pos.app.session_id", Pos.app.config.getInt ("Pos.app.session_id")).
				put ("report_type", Ticket.SETTLE_DETAIL).
				put ("ticket_no", Pos.app.ticket.getInt ("ticket_no")).
				put ("Pos.app.session_id", Pos.app.ticket.getInt ("Pos.app.session_id")).
				put ("report_lines", reportLines);

		  // Pos.app.printerService ().q (PosConst.PRINTER_REPORT, report, null);

	 }
}
