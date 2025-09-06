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
 
package cloud.multipos.pos.services;

import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;

import java.util.Date;

public class TotalsService extends Service  {

	 public TotalsService () { 

		  super (); 
	 }

	 public boolean ready () { return true; }
	 
	 public boolean process (ServiceMessage message) {

		  ticket = (Ticket) message.entity;

		  switch (ticket.getInt ("ticket_type")) {

		  case Ticket.LOGIN:
		  case Ticket.LOGOUT:
		  case Ticket.X_SESSION:
		  case Ticket.Z_SESSION:
				return true;
				
		  case Ticket.VOID:

				updateTotal (VOID_SALE, 
								 0,
								 ticket.getDouble ("total"),
								 1,
								 "void_sale",
								 "");
				return true;
				
		  case Ticket.NO_SALE:

				updateTotal (NO_SALE, 
								 0,
								 0,
								 1,
								 "no_sale",
								 "");
				return true;

		  }
		  
		  updateTotal (CUSTOMER_COUNT, 
							0,
							0,
							1,
							"customer_count",
							"");


		  for (int i=0; i<ticket.items.size (); i ++) {

				TicketItem ti = ticket.items.get (i);
				
				// ticket.items.stream ().forEach (ti -> {

				if (ti.getInt ("state") == TicketItem.VOID_ITEM) {

					 updateTotal (VOID_ITEM, 
									  0,
									  0,
									  1,
									  "void_item",
									  "");
				}
				else {

					 totalTypeID = ti.getInt ("department_id");
					 amount = ti.extAmount ();
					 quantity = ti.getInt ("quantity");
						  
					 if (ti.getInt ("state") == TicketItem.RETURN_ITEM) {
						  quantity *= -1;
								
						  updateTotal (RETURN_ITEM, 
											0,
											amount,
											quantity * -1,
											"return_item",
											"");
								
								
					 }
						  
					 updateTotal (DEPARTMENT, 
									  totalTypeID,
									  amount,
									  quantity,
									  "department_toal",
									  "");

					 for (int j=0; j<ti.addons.size (); j ++) {

						  TicketItemAddon tia = ti.addons.get (j);
						  
						  // ti.addons.stream ().forEach (tia -> {
								
						  updateTotal (DISCOUNT, 
											0,
											tia.getDouble ("addon_amount"),
											1,
											"discount",
											"");
					 }
				}
		  }

		  for (int i=0; i < ticket.taxes.size (); i ++) {

				TicketTax tx = ticket.taxes.get (i);
				
				// ticket.taxes.stream ().forEach (ta -> {

				totalTypeID = tx.getInt ("tax_group_id");
				amount = tx.getDouble ("tax_amount");

				updateTotal (TAX, 
								 totalTypeID,
								 amount,
								 1,
								 "tax",
								 "");
		  }
		  
		  for (TicketAddon ta: ticket.addons) {
				
				// ticket.taxes.stream ().forEach (ta -> {

				Logger.x ("ticket addon... " + ta);
					 
				updateTotal (SERVICE_FEE, 
								 ta.getInt ("addon_type"),
								 ta.getDouble ("addon_amount"),
								 ta.getInt ("addon_quantity"),
								 ta.getString ("addon_description"),
								 "");
		  }

		  for (int i=0; i<ticket.tenders.size (); i ++) {

				TicketTender tt = ticket.tenders.get (i);

				totalTypeID = tt.getInt ("tender_id");
				amount = tt.getDouble ("tendered_amount") - tt.getDouble ("returned_amount");

				int tenderType = ticket.getInt ("ticket_type") != Ticket.BANK ? TENDER : BANK;
					 
				updateTotal (tenderType, 
								 totalTypeID,
								 amount,
								 1,
								 tt.getString ("tender_type"),
								 tt.getString ("sub_tender_type"));

				if (tt.getDouble ("round_diff") != 0) {
					 
					 updateTotal (CASH_ROUND_DIFF, 
									  totalTypeID,
									  tt.getDouble ("round_diff"),
									  1,
									  "round_diff",
									  "");
				}
					 
				if ((totalTypeID == TicketTender.ACCOUNT) && ticket.has ("customer")) {

						  
					 // updateTotal (ACCOUNT, 
					 // 					ticket.getInt ("customer_id"),
					 // 					amount,
					 // 					1,
					 // 					tt.getString ("tender_type"),
					 // 					tt.getString ("sub_tender_type"));
						  
					 Jar total = new Jar ()
						  .put ("pos_session_id", Pos.app.config.getInt ("pos_session_id"))
						  .put ("total_type", ACCOUNT)
						  .put ("total_type_id", ticket.getInt ("customer_id"))
						  .put ("amount", amount)
						  .put ("quantity", 1)
						  .put ("total_type_desc", tt.getString ("tender_type"))
						  .put ("total_sub_type_desc", String.valueOf (ticket.getInt ("ticket_no")) + " - " + ticket.get ("customer").getString ("contact"));
						  
					 Pos.app.db ().insert ("pos_session_totals", total);

				}
		  }
		  
		  updateSummary ();
		  return true;
	 }

		  
	 private void updateTotal (int totalType,
										int totalTypeID,
										double amount,
										int quantity,
										String totalTypeDesc,
										String totalSubTypeDesc) {

		  amount = Currency.round (amount);
		  
		  String select =
				"select id, amount, quantity from pos_session_totals where " +
				"pos_session_id = " + Pos.app.config.getInt ("pos_session_id") + " and " +
				"total_type = " + totalType + " and " +
				"total_type_id = " + totalTypeID;

		  if (totalTypeDesc.length () > 0) {
				select += " and total_type_desc = '" + totalTypeDesc + "'";
		  }
		  
		  if (totalSubTypeDesc.length () > 0) {
				select += " and total_sub_type_desc = '" + totalSubTypeDesc + "'";
		  }
		  
		  DbResult sessionResult  = new DbResult (select, Pos.app.db ());		  
		  if (sessionResult.fetchRow ()) {

				Jar session = sessionResult.row ();
				
				double totalAmount = session.getDouble ("amount") + amount;
				int totalQuantity = session.getInt ("quantity") + quantity;
				
				String update = 
					 "update pos_session_totals set " +
					 "amount = " + totalAmount + ", " +
					 "quantity = " + totalQuantity +
					 ", update_time = '" + Pos.app.db.timestamp (new Date ()) +
					 "' where " +
					 "id = " + session.getInt ("id");

				Pos.app.db ().exec (update);

				if (totalType == TENDER) {
					 // pos.posSession ().put ("session_quantity", totalQuantity);
					 // pos.posSession ().put ("session_total", totalAmount);
				}
				
										  
		  }
		  else {
				
				Jar total = new Jar ()
					 .put ("pos_session_id", Pos.app.config.getInt ("pos_session_id"))
					 .put ("total_type", totalType)
					 .put ("total_type_id", totalTypeID)
					 .put ("amount", amount)
					 .put ("quantity", quantity)
					 .put ("total_type_desc", totalTypeDesc)
					 .put ("total_sub_type_desc", totalSubTypeDesc);
				
				Pos.app.db ().insert ("pos_session_totals", total);

				if (totalType == TENDER) {
					 // pos.posSession ().put ("session_quantity", quantity);
					 // pos.posSession ().put ("session_total", amount);
				}
		  }
	 }

	 private void updateSummary () {
		  
		  DbResult totalResult  = new DbResult ("select sum(amount) from pos_session_totals where " +
															 "pos_session_id = " + Pos.app.config.getInt ("pos_session_id") + " and " +
															 "total_type = " + TENDER, Pos.app.db ());
		  
		  
		  if (totalResult.fetchRow ()) {

				total = 0;
				
				Jar t = totalResult.row ();
				if (t.has ("sum(amount)")) {
					 total = t.getDouble ("sum(amount)");
				}
		  }
		  
		  totalResult  = new DbResult ("select sum(amount) from pos_session_totals where " +
												 "pos_session_id = " + Pos.app.config.getInt ("pos_session_id") + " and " +
												 "total_type = " + TENDER + " and " +
												 "total_type_id = 1", Pos.app.db ());
		  
		  
		  if (totalResult.fetchRow ()) {
				
				cashInDrawer = 0;
				
				Jar t = totalResult.row ();
				if (t.has ("sum(amount)")) {
					 cashInDrawer = t.getDouble ("sum(amount)");
				}
		  }
		  
		  totalResult  = new DbResult ("select sum(quantity) from pos_session_totals where " +
												 "pos_session_id = " + Pos.app.config.getInt ("pos_session_id") + " and " +
												 "total_type = " + CUSTOMER_COUNT, Pos.app.db ());
		  
		  
		  if (totalResult.fetchRow ()) {

				customerCount = 0;
				
				Jar t = totalResult.row ();
				if (t.has ("sum(quantity)")) {
					 customerCount =t.getInt ("sum(quantity)");
				}
		  }		  
	 }
	 
	 public static TotalsService factory () {
		  
		  TotalsService t = new TotalsService ();
		  t.start ();
		  return t;
	 }

	 /***************************************************************************
	  *
	  **************************************************************************/

	 public static final int DEPARTMENT               = 1;
	 public static final int TENDER                   = 2;
	 public static final int TAX                      = 3;
	 public static final int VOID_ITEM                = 4;
	 public static final int VOID_SALE                = 5;
	 public static final int NO_SALE                  = 6;
	 public static final int X_REPORT                 = 7;
	 public static final int DISCOUNT                 = 8;
	 public static final int LOGIN                    = 9;
	 public static final int LOGOUT                   = 10;
	 public static final int CUSTOMER_COUNT           = 11;
	 public static final int BANK                     = 12;
	 public static final int DRAWER_COUNT             = 13;
	 public static final int DRAWER_COUNT_EXCEPTION   = 14;
	 public static final int CASH_ROUND_DIFF          = 15;
	 public static final int RETURN_ITEM              = 16;
	 public static final int ACCOUNT                  = 17;
	 public static final int SERVICE_FEE              = 18;
	 
	 private double total = 0;
	 private double cashInDrawer = 0;
	 private int customerCount = 0;
	 private int totalTypeID;
	 private double amount;
	 private int quantity;
	 private Ticket ticket;

	 public double total () { return total; }	 
	 public double cashInDrawer () { return cashInDrawer; }
	 public int customerCount () { return customerCount; }

	 
}
