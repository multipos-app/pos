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
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Customer
import cloud.multipos.pos.views.LoyaltyPromptView
import cloud.multipos.pos.views.CustomerEditView
import cloud.multipos.pos.views.CustomerSearchView
import cloud.multipos.pos.receipts.LoytaltyReceiptBuilder

class InstantLoyalty (): ConfirmControl () {

	 override fun controlAction (jar: Jar) {

		  if (Pos.app.ticket.has ("customer")) {

				// if customer is set, just print the token

				confirmed (false)
				var receiptBuilder = LoytaltyReceiptBuilder ().ticket (Pos.app.ticket,  PosConst.PRINTER_RECEIPT)				
				receiptBuilder.print ()

				// launch customer edit in case they want to add some details
				
				CustomerEditView (Pos.app.ticket.get ("customer").getInt ("customer_id"))
				return
		  }
		  
		  if (confirmed ()) {

				confirmed (false)				
				when (jar.getString ("action")) {

					 "loyalty" -> {

						  val customer = Customer ().instant ()

						  Logger.x ("new customer... ${customer}")
						  
						  customer.put ("type", "customers")
						  Pos.app.ticket.put ("customer", customer)
						  Pos.app.ticket.updates.add (customer)
						  Pos.app.posAppBar.customer (customer.display ())
						  controlAction (Jar ())
					 }

					 "search" -> {

						  CustomerSearchView ()
					 }
				}
		  }
		  else {
				
				LoyaltyPromptView (Pos.app.getString ("instant_loyalty"), this)
		  }
	 }
}
