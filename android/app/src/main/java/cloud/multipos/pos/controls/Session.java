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
import cloud.multipos.pos.views.ConfirmView;
import cloud.multipos.pos.devices.*;
import cloud.multipos.pos.views.PosDisplays;

import android.app.Dialog;
import java.util.Date;

public abstract class Session extends CompleteTicket {

	 public Session () { super (); }
	 
	 public boolean openDrawer () { return false; }

	 public void controlAction (Jar Jar) {
		  
		  int sessionID = Pos.app.config.getInt ("pos_session_id");
		  totalsList.clear ();
		  
		  Jar total = null;
		  String totalDesc = "";
		  int totalQuantity = 0;
		  Jar session = null;
		  DbResult totalsResults = null;
		  
		  double totalAmount = 0;
		  double cashInDrawer = 0;
		  double drawerOpenAmount = 0;
		  double drawerCountTotal = 0;

		  int [] totalTypes = { TotalsService.DEPARTMENT,
										TotalsService.TAX,
										TotalsService.TENDER,
										TotalsService.BANK,
										TotalsService.DRAWER_COUNT};

		  int [] exceptionTypes = { TotalsService.VOID_ITEM,
											 TotalsService.VOID_SALE,
											 TotalsService.DISCOUNT,
											 TotalsService.NO_SALE,
											 TotalsService.VOID_ITEM };
		  
		  for (int i=0; i<Pos.app.drawerCounts.size (); i++) {

				drawerCountTotal += Pos.app.drawerCounts.get (i).getDouble ("amount");
		  }

		  for (int i=0; i<totalTypes.length; i++) {

				switch (totalTypes [i]) {

				case TotalsService.DEPARTMENT:

					 totalsList.add (new Jar ()
										  .put ("type", Ticket.CENTER)
										  .put ("desc", Pos.app.getString ("department_totals")));

					 totalsResults = new DbResult ("select total_type_id, quantity, amount from pos_session_totals where " +
															 "pos_session_id = " + sessionID + " and " +
															 "total_type = " + TotalsService.DEPARTMENT,
															 Pos.app.db);
					 

					 while (totalsResults.fetchRow ()) {

						  total = totalsResults.row ();
						  
						  DbResult departmentResults = new DbResult ("select department_desc from departments where id = " +
																					total.getInt ("total_type_id"),
																					Pos.app.db);
						  if (departmentResults.fetchRow ()) {

								Jar department = departmentResults.row ();
								totalDesc = department.getString ("department_desc");			 
						  }

						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", totalDesc)
												.put ("quantity", total.getInt ("quantity"))
												.put ("amount", total.getDouble ("amount")));

						  totalQuantity += total.getInt ("quantity");
						  totalAmount += total.getDouble ("amount");

					 }

					 totalsList.add (new Jar ()
										  .put ("type", Ticket.SESSION_TOTAL)
										  .put ("desc", Pos.app.getString ("total"))
										  .put ("quantity", totalQuantity)
										  .put ("amount", totalAmount));

					 break;

				case TotalsService.TAX:

					 switch (Pos.app.config.getString ("currency")) {

					 case "USD":
						  
						  totalQuantity = 0;
						  totalAmount = 0;

						  totalsList.add (new Jar ()
												.put ("type", Ticket.CENTER)
												.put ("desc", Pos.app.getString ("tax_totals")));

						  totalsResults = new DbResult ("select total_type_id, quantity, amount from pos_session_totals where " +
																  "pos_session_id = " + sessionID + " and " +
																  "total_type = " + TotalsService.TAX,
																  Pos.app.db);
					 
						  while (totalsResults.fetchRow ()) {

								total = totalsResults.row ();

								DbResult taxResults = new DbResult ("select short_desc from tax_groups where id = " +
																				total.getInt ("total_type_id"),
																				Pos.app.db);
								if (taxResults.fetchRow ()) {
								
									 Jar tax = taxResults.row ();
									 totalDesc = tax.getString ("short_desc");			 
								}

								totalsList.add (new Jar ()
													 .put ("type", Ticket.SESSION_TOTAL)
													 .put ("desc", totalDesc)
													 .put ("quantity", total.getInt ("quantity"))
													 .put ("amount", total.getDouble ("amount")));

								totalQuantity += total.getInt ("quantity");
								totalAmount += total.getDouble ("amount");

						  }
					 
						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", Pos.app.getString ("total"))
												.put ("quantity", totalQuantity)
												.put ("amount", totalAmount));
						  break;
					 }
					 
					 break;
					 

				case TotalsService.TENDER:

					 totalQuantity = 0;
					 totalAmount = 0;

					 totalsList.add (new Jar ()
										  .put ("type", Ticket.CENTER)
										  .put ("desc", Pos.app.getString ("tender_totals")));

					 totalsResults = new DbResult ("select * from pos_session_totals where " +
															 "pos_session_id = " + sessionID + " and " +
															 "total_type = " + TotalsService.TENDER,
															 Pos.app.db);
					 
					 while (totalsResults.fetchRow ()) {

						  total = totalsResults.row ();
						  Logger.d (total);
						  
						  // DbResult tenderResults = new DbResult ("select tender_desc from tenders where id = " +
						  // 													  total.getInt ("total_type_id"),
						  // 													  Pos.app.db);
						  // if (tenderResults.fetchRow ()) {
						  // 		Jar tender = tenderResults.row ();
						  // 		totalDesc = tender.getString ("tender_desc");			 
						  // }

						  switch (total.getString ("total_type_desc")) {

						  case "credit":
								
								totalsList.add (new Jar ()
													 .put ("type", Ticket.SESSION_TOTAL)
													 .put ("desc", total.getString ("total_sub_type_desc"))
													 .put ("quantity", total.getInt ("quantity"))
													 .put ("amount", total.getDouble ("amount")));
								break;

						  case "cash":
								totalsList.add (new Jar ()
													 .put ("type", Ticket.SESSION_TOTAL)
													 .put ("desc", total.getString ("total_type_desc"))
													 .put ("quantity", total.getInt ("quantity"))
													 .put ("amount", total.getDouble ("amount")));
						  default:
								
								totalsList.add (new Jar ()
													 .put ("type", Ticket.SESSION_TOTAL)
													 .put ("desc", total.getString ("total_type_desc"))
													 .put ("quantity", total.getInt ("quantity"))
													 .put ("amount", total.getDouble ("amount")));
								break;

						  }

						  totalQuantity += total.getInt ("quantity");
						  totalAmount += total.getDouble ("amount");

						  if (total.getInt ("total_type_id") == 1) {
								cashInDrawer += total.getDouble ("amount");
						  }
					 }

					 totalsList.add (new Jar ()
										  .put ("type", Ticket.SESSION_TOTAL)
										  .put ("desc", Pos.app.getString ("total"))
										  .put ("quantity", totalQuantity)
										  .put ("amount", totalAmount));

					 break;

				case TotalsService.BANK:
					 
					 totalsResults = new DbResult ("select total_type_id, quantity, amount from pos_session_totals where " +
															 "pos_session_id = " + sessionID + " and " +
															 "total_type = " + TotalsService.BANK,
															 Pos.app.db);
					 
					 while (totalsResults.fetchRow ()) {

						  total = totalsResults.row ();
						  
						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", Pos.app.getString ("open_amount"))
												.put ("quantity", 0)
												.put ("amount", total.getDouble ("amount")));
						  
						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", Pos.app.getString ("cash_in_drawer"))
												.put ("quantity", 0)
												.put ("amount", total.getDouble ("amount") + cashInDrawer));

						  drawerOpenAmount = total.getDouble ("amount");
					 }
					 
					 break;
					 
				case TotalsService.DRAWER_COUNT:

					 if (sessionType == Ticket.Z_SESSION) {
						  						  						  
						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", Pos.app.getString ("drawer_count"))
												.put ("quantity", 0)
												.put ("amount", drawerCountTotal));
						  
					 	  Pos.app.drawerCounts.clear ();
					 }
				}
		  }
		  
		  totalsList.add (new Jar ()
								.put ("type", Ticket.CENTER)
								.put ("desc", Pos.app.getString ("totals_exceptions")));
		  
		  // if ((cashInDrawer + drawerOpenAmount) != drawerCountTotal) {

		  // 		if (!jar ().has ("drawer_count_confirm") && (sessionType == Ticket.Z_SESSION)) {
					 
		  // 			 jar ().put ("drawer_count_confirm", true);
				
		  // 			 setConfirmed (false);
		  // 			 setConfirmText (Pos.app.getString ("drawer_count_and_cash_totals_do_not_match"));
		  // 			 getDialog ().show ();
		  // 			 return;

		  // 		}

		  // 		totalsList.add (new Jar ()
		  // 							 .put ("type", Ticket.SESSION_TOTAL)
		  // 							 .put ("desc", Pos.app.getString ("drawer_count_error"))
		  // 							 .put ("quantity", 0)
		  // 							 .put ("amount", (cashInDrawer + drawerOpenAmount) - drawerCountTotal));
		  // }							 
							  
		  
		  for (int i=0; i<exceptionTypes.length; i++) {

				switch (exceptionTypes [i]) {
		  
					 
				case TotalsService.NO_SALE:
					 
					 totalsResults = new DbResult ("select total_type_id, quantity, amount from pos_session_totals where " +
															 "pos_session_id = " + sessionID + " and " +
															 "total_type = " + TotalsService.NO_SALE,
															 Pos.app.db);
					 
					 if (totalsResults.fetchRow ()) {
						  
						  total = totalsResults.row ();

						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", Pos.app.getString ("no_sale"))
												.put ("quantity", total.getInt ("quantity"))
												.put ("amount", 0));
					 }
					 
					 break;
					 
				case TotalsService.VOID_SALE:
					 					  
					 totalsResults = new DbResult ("select total_type_id, quantity, amount from pos_session_totals where " +
															 "pos_session_id = " + sessionID + " and " +
															 "total_type = " + TotalsService.VOID_SALE,
															 Pos.app.db);
					 
					 if (totalsResults.fetchRow ()) {
						  

						  total = totalsResults.row ();

						  totalsList.add (new Jar ()
												.put ("type", Ticket.SESSION_TOTAL)
												.put ("desc", Pos.app.getString ("void_sale"))
												.put ("quantity", total.getInt ("quantity"))
												.put ("amount", 0));
					 }
					 
					 break;
					 
				case TotalsService.DISCOUNT:
						  
					 totalsList.add (new Jar ()
										  .put ("type", Ticket.CENTER)
										  .put ("desc", Pos.app.getString ("totals_exceptions")));
						  
				}
					 
				totalsResults = new DbResult ("select total_type_id, quantity, amount from pos_session_totals where " +
														"pos_session_id = " + sessionID + " and " +
														"total_type = " + TotalsService.DISCOUNT,
														Pos.app.db);
					 
				if (totalsResults.fetchRow ()) {
						  
					 total = totalsResults.row ();

					 totalsList.add (new Jar ()
										  .put ("type", Ticket.SESSION_TOTAL)
										  .put ("desc", Pos.app.getString ("discounts"))
										  .put ("quantity", total.getInt ("quantity"))
										  .put ("amount", total.getDouble ("amount")));
				}
					 
				break;
		  }


		  Logger.d ("printing in session... " + jar.getBoolean ("print_report"));

		  jar.put ("print_report", false);
		  
		  if (jar.getBoolean ("print_report")) {
				
				confirmed (false);
				setJar (new Jar ());

				String ticketText = getText ();
		  
				Pos.app.ticket.put ("ticket_text", ticketText);
				String completeTime = Pos.app.db.timestamp (new Date ());		  
				Pos.app.ticket
					 .put ("complete_time", completeTime)
					 .put ("state", Ticket.COMPLETE)
					 .put ("ticket_type", sessionType)
					 .put ("ticket_text", getText ());
				
				Pos.app.db.exec ("update tickets set state = " + Ticket.COMPLETE + ", " +
											  "ticket_type = " + sessionType + ", " +
											  "complete_time = '" + completeTime + "', " +
											  "ticket_text = '" + Pos.app.ticket.getString ("ticket_text") + "' " +
											  " where id = " + Pos.app.ticket.getLong ("id"));
				
				// Pos.app.printerService ().q (PosConst.PRINTER_REPORT, Pos.app.ticket, null);
				Pos.app.cloudService.upload (Pos.app.ticket, 0);
		  }
	 }

	 public String getText () {

		  Printer printer = DeviceManager.printer;

		  String totalFormat =
				"%" + printer.quantityWidth () +
				"d %-" + printer.descWidth () +
				"s%" + printer.amountWidth () + "s";
		  
		  StringBuffer sb = new StringBuffer ("\n");

		  switch (sessionType) {
		  case Ticket.X_SESSION:
				sb.append (Strings.center (Pos.app.getString ("x_report"), printer.width (), " ") ); 
				break;
		  case Ticket.Z_SESSION:
				sb.append (Strings.center (Pos.app.getString ("z_report"), printer.width (), " ")); 
				break;
		  }

		  sb.append ("\n\n\n");
		  
		  for (int i = 0; i < totalsList.size (); i ++) {

				Jar t = (Jar) totalsList.get (i);

				String desc = t.getString ("desc");
				if (desc.length () > printer.descWidth ()) {
					 desc = desc.substring (0, printer.descWidth ());
				}
				
				switch (t.getInt ("type")) {
					 
				case Ticket.CENTER:
					 sb.append (Strings.center (" " + desc + " ", printer.width (), "*") + "\n"); 

					 break;
					 
				case Ticket.SESSION_TOTAL:

					 sb.append (String.format (totalFormat,
														t.getInt ("quantity"),
														desc,
														Strings.currency (Strings.round (t.getDouble ("amount")), false) + "\n"));
					 break;
				}
		  }
		  return sb.toString ();
	 }

	 
	 public void complete (int sessionType) {

		  String timestamp = Pos.app.db.timestamp (new Date ());

		  Pos.app.ticket.put ("state", Ticket.COMPLETE);
		  Pos.app.ticket.put ("complete_time", Pos.app.ticket.getString ("start_time"));

		  Pos.app.db.exec ("update tickets set state = " + Ticket.COMPLETE + ", " +
										 "start_time = '" + Pos.app.db.timestamp (new Date ()) + "', " +
										 "ticket_type = " + sessionType + " where id = " + Pos.app.ticket.getLong ("id"));

		  confirmed (true);
		  updateDisplays ();
	 }

	 private ArrayList <Jar> totalsList = new ArrayList <Jar> ();
	 public ArrayList <Jar> totalsList () { return totalsList; }
	 
	 public boolean beforeAction () {

		  if (Pos.app.ticket.hasItems ()) {
				return false;
		  }
		  return true;
	 }
	 
	 public boolean override () { return override; }
	 public void setOverride (boolean override) {
		  this.override = override;
	 }

	 private boolean override = false;
	 private static ArrayList <Jar> totals;
	 protected int sessionType = 0;
	 
}
