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

public class SettleSummary extends SettleReport {

	 public SettleSummary () { super (); }

	 public void report () {

		  ArrayList <Jar> reportLines = new ArrayList <Jar> ();

		  int totalNotSettledCount = 0;
		  double totalNotSettledAmount = 0.0;
		  int totalSettledCount = 0;
		  double totalSettledAmount = 0.0;
		  int totalCount = 0;
		  double totalAmount = 0.0;
		  int totalTip = 0;
		  double totalTipAmount = 0.0;

		  for (Jar tender: tenders) {

				switch (tender.getInt ("status")) {
			 
				case TicketTender.NOT_SETTLED:

					 totalNotSettledCount ++;
					 totalNotSettledAmount += tender.getDouble ("amount");
					 break;

				case TicketTender.SETTLED:

					 totalSettledCount ++;
					 totalSettledAmount += tender.getDouble ("amount");
					 break;
				}

				totalCount ++;
				totalAmount += tender.getDouble ("amount");

				if (tender.getDouble ("tip") > 0) {
					 totalTip ++;
					 totalTipAmount += tender.getDouble ("tip");
				}
		  }
		  
		  reportLines.add (new Jar ().
								 put ("type", Ticket.SESSION_TOTAL).
								 put ("quantity", totalNotSettledCount).
								 put ("desc", Pos.app.getString ("not_settled")).
								 put ("amount", totalNotSettledAmount));
		  
		  reportLines.add (new Jar ().
								 put ("type", Ticket.SESSION_TOTAL).
								 put ("quantity", totalSettledCount).
								 put ("desc", Pos.app.getString ("settled")).
								 put ("amount", totalSettledAmount));

		  reportLines.add (new Jar ().
								 put ("type", Ticket.SESSION_TOTAL).
								 put ("quantity", totalTip).
								 put ("desc", Pos.app.getString ("tip")).
								 put ("amount", totalTipAmount));

		  reportLines.add (new Jar ().
								 put ("type", Ticket.SESSION_TOTAL).
								 put ("quantity", totalCount).
								 put ("desc", Pos.app.getString ("total")).
								 put ("amount", totalAmount));

		  Jar report = new Jar ();
		  report.
				put ("Pos.app.session_id", Pos.app.config.getInt ("Pos.app.session_id")).
				put ("report_type", Ticket.SETTLE_SUMMARY).
				put ("ticket_no", Pos.app.ticket.getInt ("ticket_no")).
				put ("Pos.app.session_id", Pos.app.ticket.getInt ("Pos.app.session_id")).
				put ("report_lines", reportLines);

		  // Pos.app.printerService ().q (PosConst.PRINTER_REPORT, report, null);
	 }

}
