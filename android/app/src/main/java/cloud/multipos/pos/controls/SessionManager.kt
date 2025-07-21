/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 
package cloud.multipos.pos.controls

import java.util.*

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.services.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.net.Upload
import java.util.Date

open class SessionManager (): ConfirmControl () {
	 	 
	 val totalFormat = "%" + DeviceManager.printer.quantityWidth () +
	 "d %-" + DeviceManager.printer.descWidth () +
	 "s%" + DeviceManager.printer.amountWidth () + "s"
	 val summaryFormat = " %-" + (DeviceManager.printer.quantityWidth () + DeviceManager.printer.descWidth ()) +
	 "s%" + DeviceManager.printer.amountWidth () + "s"
	 val sep = "\n" + "".center (DeviceManager.printer.width (), "*") + "\n"
	 
	 val totals = mutableListOf <Jar> ()
	 val cards = mutableListOf <Jar> ()
	 val departments = mutableListOf <Jar> ()
	 val exceptions = mutableListOf <Jar> ()
	 
	 var cashSales = 0.0
	 var total = 0.0
	 var drawerCount = 0.0
	 var overShort = 0.0
	 var openAmount = 0.0
	 var sessionFloat = 0.0
	 var bank = 0.0
	 var control = 0.0
	 var difference = 0.0
	 var departmentTotal = 0.0
	 var count = false
	 var totalQuantity = 0

	 override fun openDrawer ():Boolean { return false }
	 override fun printReceipt ():Boolean { return false }

	 override fun controlAction (jar: Jar) {
				
		  var sessionID = Pos.app.config.getInt ("pos_session_id")
		  
		  cashSales = 0.0
		  total = 0.0
		  openAmount = 0.0
		  bank = 0.0
		  control = 0.0
		  difference = 0.0
		  departmentTotal = 0.0
		  count = false
		  totalQuantity = 0
		  totals.clear ()
		  cards.clear ()
		  departments.clear ()
		  exceptions.clear ()

		  var sessionTotal: Jar		  
		  var sessionResults = DbResult (Pos.app.db
														 .find ("pos_session_totals")
														 .conditions (arrayOf ("pos_session_id = " + sessionID,
																					  "total_type = " + TotalsService.BANK,
																					  "total_sub_type_desc = 'open_amount'"))
														 .query (),
													Pos.app.db)
					 		  
		  if (sessionResults.fetchRow ()) {

				sessionTotal = sessionResults.row ()
				openAmount = sessionTotal.getDouble ("amount")
		  }
		  
		  totalQuantity = 0
		  total = 0.0
		  var cardQuantity = 0
		  var card = 0.0
		  var totalReceived = 0.0
		  
		  sessionResults = DbResult (Pos.app.db
													.find ("pos_session_totals")
													.conditions (arrayOf ("pos_session_id = " +
																				 sessionID + " and " +
																				 "total_type in (" + TotalsService.TENDER +
																									  ", " +
																									  TotalsService.ACCOUNT + ")"))
													.query (),
											  Pos.app.db)
		  
		  while (sessionResults.fetchRow ()) {

				sessionTotal = sessionResults.row ()
								
				when (sessionTotal.getString ("total_type_desc")) {

				"cash" -> {

					 totals.add (Jar ()
										  .put ("type", "total")
										  .put ("desc", sessionTotal.getString ("total_type_desc"))
										  .put ("quantity", sessionTotal.getInt ("quantity"))
										  .put ("amount", sessionTotal.getDouble ("amount")))
					 
					 cashSales = sessionTotal.getDouble ("amount")
					 total += sessionTotal.getDouble ("amount")
					 totalQuantity ++ 
				}

				"card", "credit" -> {
					 
					 cards.add (Jar ()
										 .put ("type", "card")
										 .put ("desc", sessionTotal.getString ("total_sub_type_desc"))
										 .put ("quantity", sessionTotal.getInt ("quantity"))
										 .put ("amount", sessionTotal.getDouble ("amount")))
					 
					 card += sessionTotal.getDouble ("amount")
					 cardQuantity += sessionTotal.getInt ("quantity")
					 total += sessionTotal.getDouble ("amount")
					 totalQuantity ++
				}
				else -> {

					 var p = Jar ()
						  .put ("type", sessionTotal.getString ("total_type_desc"))
						  .put ("quantity", sessionTotal.getInt ("quantity"))
						  .put ("amount", sessionTotal.getDouble ("amount"))
					 
					 if (sessionTotal.getInt ("total_type") == TotalsService.ACCOUNT) {
						  
						  p.put ("account_tag", sessionTotal.getString ("total_sub_type_desc"))
					 }
					 else {
						  
						  totals.add (Jar ()
												.put ("type", "total")
												.put ("desc", sessionTotal.getString ("total_type_desc"))
												.put ("quantity", sessionTotal.getInt ("quantity"))
												.put ("amount", sessionTotal.getDouble ("amount")))
						  
						  total += sessionTotal.getDouble ("amount")
						  totalQuantity ++
					 }
				}
				}
				}
						  
		  if (card > 0) {
				
				totals.add (Jar ()
									 .put ("type", "total")
									 .put ("desc", "card")
									 .put ("quantity", cardQuantity)
									 .put ("amount", card))
		  }
		  
		  totals.add (Jar ()
								.put ("type", "total")
								.put ("desc", "total")
								.put ("quantity", totalQuantity)
								.put ("amount", total))
		  
		  /**
			*
			* Get bank totals, float, drops...
			*
			*/
		  
		  sessionResults = DbResult (Pos.app.db
													.find ("pos_session_totals")
													.conditions (arrayOf ("pos_session_id = " + sessionID,
																				 "total_type = " + TotalsService.BANK))
													.query (),
													Pos.app.db)
		  
		  while (sessionResults.fetchRow ()) {
				
		  		sessionTotal = sessionResults.row ()

				when (sessionTotal.getString ("total_type_desc")) {
					 
					 "cash_drop" -> { }  // don't count drops
					 
					 else -> {
						  
						  bank += sessionTotal.getDouble ("amount")
					 }
				}
				
		  		totals.add (Jar ()
		  							 .put ("type", "total")
		  							 .put ("desc", sessionTotal.getString ("total_type_desc"))
		  							 .put ("quantity", 0)
		  							 .put ("amount", sessionTotal.getDouble ("amount")))
		  }
		  
		  /**
			*
			* Get fees
			*
			*/
		  
		  sessionResults = DbResult (Pos.app.db
													.find ("pos_session_totals")
													.conditions (arrayOf ("pos_session_id = " + sessionID,
																				 "total_type = " + TotalsService.SERVICE_FEE))
													.query (),
													Pos.app.db)
		  
		  while (sessionResults.fetchRow ()) {
				
		  		sessionTotal = sessionResults.row ()
				
		  		totals.add (Jar ()
		  						.put ("type", "service_fee")
		  						.put ("desc", sessionTotal.getString ("total_sub_type_desc"))
		  						.put ("quantity", 0)
		  						.put ("amount", sessionTotal.getDouble ("amount")))
		  }
		  
		  /**
		  	*
		  	* Drawer count
		  	*
		  	*/

		  if (count) {
				
				totals.add (Jar ()
								.put ("type", "total")
								.put ("desc", "drawer_count")
								.put ("quantity", 0)
								.put ("amount", drawerCount ()))
		  		  
				totals.add (Jar ()
								.put ("type", "total")
								.put ("desc", "cash_diff")
								.put ("quantity", 0)
								.put ("amount", cashInDrawer () - drawerCount ()))
		  }

		  
		  /**
		  	*
		  	* float to next session
		  	*
		  	*/

		  if (sessionFloat > 0) {
					 
				totals.add (Jar ()
								.put ("type", "total")
								.put ("desc", "session_float")
								.put ("quantity", 0)
								.put ("amount", sessionFloat))
		  }
		  
		  /**
		  	*
		  	* Departments
		  	*
		  	*/
		  
		  sessionResults = DbResult (Pos.app.db
													.find ("pos_session_totals")
													.conditions (arrayOf ("pos_session_id = " + sessionID,
																				 "total_type = " + TotalsService.DEPARTMENT))
													.query (),
													Pos.app.db)
					 
		  totalQuantity = 0
		  while (sessionResults.fetchRow ()) {

		  		sessionTotal = sessionResults.row ()
				var totalDesc = ""
				
		  		val departmentResults = DbResult (Pos.app.db
																  .find ("departments")
																  .conditions (arrayOf ("id = " + sessionTotal.getInt ("total_type_id")))
																  .query (),
		  													 Pos.app.db)
				
		  		if (departmentResults.fetchRow ()) {

		  			 val department = departmentResults.row ()					 
		  			 totalDesc = department.getString ("department_desc")			 
		  		}

		  		departments
					 .add (Jar ()
							 .put ("type", "total")
							 .put ("desc", totalDesc)
							 .put ("quantity", sessionTotal.getInt ("quantity"))
							 .put ("amount", sessionTotal.getDouble ("amount")))
				
				totalQuantity += sessionTotal.getInt ("quantity")
		  		departmentTotal += sessionTotal.getDouble ("amount")

		  }

		  /**
		  	*
		  	* Exceptions
		  	*
		  	*/
		  
		  departments
				.add (Jar ()
							 .put ("type", "total")
							 .put ("desc", "total")
							 .put ("quantity", totalQuantity)
							 .put ("amount", departmentTotal))
		  
		  sessionResults = DbResult ("select * from pos_session_totals where " +
											  "pos_session_id = " + sessionID + " and " +
											  "total_type in (4, 5, 6, 7, 8, 15, 16)",
											  Pos.app.db)
		  
		  while (sessionResults.fetchRow ()) {
				
		  		sessionTotal = sessionResults.row ()
		  		exceptions
					 .add (Jar ()
								  .put ("type", "total")
								  .put ("desc", sessionTotal.getString ("total_type_desc"))
								  .put ("quantity", sessionTotal.getInt ("quantity"))
								  .put ("amount", sessionTotal.getDouble ("amount")))
		  }
		  
		  Pos.app.ticket
				.put ("total", total)
				.put ("totals", totals)
				.put ("cards", cards)
				.put ("departments", departments)
				.put ("exceptions", exceptions)
	 }
	 
	 fun close (ticketType: Int) {

		  val timestamp = Pos.app.db.timestamp (Date ())
		  
		  if (ticketType == Ticket.DRAWER_COUNT) {
								
				val session = Jar ()
				val counts = mutableListOf <Jar> ()
				
				for (count in Pos.app.drawerCounts) {
					 
					 if (count.getInt ("denom_count") > 0) {
						  
						  counts.add (Jar ()
												.put ("denom_id", count.getInt ("denom_id"))
												.put ("denom_name", count.get ("denom").getString ("denom_name"))
												.put ("denom_count", count.getInt ("denom_count")))
					 }
				}

				if (sessionFloat > 0) {
					 
					 /**
					  *
					  * Save float for next session
					  *
					  */

					 val float = Jar ()
						  .put ("pos_session_id", Pos.app.config.getInt ("pos_session_id"))
						  .put ("total_type", TotalsService.BANK)
						  .put ("amount", sessionFloat)
						  .put ("quantity", 1)
						  .put ("total_type_desc", "session_float")
	 
					 session.put ("float", float)
					 
					 Pos.app.db.insert ("pos_session_totals", float)
					 Pos.app.drawerCounts.clear ()
		  		}
				
				session.put ("cash_sales", cashSales)
				session.put ("counts", counts)
				session.put ("drawer_count", drawerCount)
				session.put ("over_short", overShort)
				
				Pos.app.ticket.put ("cash_management", session)
		  }

		  // update current session with complete time
		  
		  Pos.app.db.update ("pos_sessions",
									Pos.app.config.getInt ("pos_session_id"),
									Jar ().put ("complete_time",
													Pos.app.db.timestamp (java.util.Date ())))

		  // start a new session
		  
		  var sessionID = Pos.app.db.insert ("pos_sessions", Jar ()
															  .put ("business_unit_id", Pos.app.config.getInt ("business_unit_id"))
															  .put ("pos_no", Pos.app.posNo ()))

		  // save the new session in the config
		  
		  Pos.app.config.put ("pos_session_id", sessionID)
		  
		  // update and post the ticket to the server
		  
		  Pos.app.ticket
				.put ("state", Ticket.COMPLETE)
				.put ("ticket_type", ticketType)
				.put ("complete_time", Pos.app.ticket.getString ("start_time"))
				.put ("total", total)
				.put ("ticket_text", Pos.app.receiptBuilder ().text ())
				.update ()
				.complete ()
	 }

	 fun cashInDrawer (): Double {
		  
		  return bank + cashSales
	 }
	 
	 fun drawerCount (): Double {

		  var drawerCount = 0.0
		  
		  for (c in Pos.app.drawerCounts) {
					 
				drawerCount += (c.getInt ("denom_count") * c.get ("denom").getDouble ("denom")) / 100.0
		  }
		  
		  return drawerCount
	 }
	 
	 override fun beforeAction (): Boolean {

		  if (Pos.app.ticket.hasItems ()) {
				
				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("invalid_operation"))
												 .put ("echo_text", ""))
				return false
		  }
		  
		  return true
	 }
}
