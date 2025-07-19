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

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.views.CreditTenderView
import cloud.multipos.pos.views.PosDisplays

import android.os.Looper
import android.os.Handler
import android.os.Message

class PaxTender (): Tender (null) {

	 val handler = AuthHandler (this) 
	 var openDrawer = false
	 var result = 1
	 var serviceFeeApplied = false
	 var transactionType = "SALE"
	 lateinit var tenderView: CreditTenderView
	 
	 init {
		  
		  tenderType = "credit"
	 }

	 override fun controlAction (jar: Jar) {

		  jar (jar)
		  		  
		  if ((DeviceManager.payment == null) || (DeviceManager.payment?.deviceStatus () != DeviceStatus.OnLine)){
				
				Logger.w ("pax not available... ")
				PosDisplays.message (Pos.app.getString ("payment_offline"));
				return
		  }

		  tendered = total
		  
		  if (confirmed ()) {

				if (jar ().has ("debit_credit")) {
					 
					 tenderType = jar.getString ("debit_credit")
				}

				openDrawer = jar.getBoolean ("open_drawer")

				confirmed (false)
				
				// dialog.auth ()

				authAmount = total
				
				if (authAmount < 0) {
					 
					 authAmount = authAmount * -1.0
					 transactionType = "RETURN"
				}
				
				var authString = (total () * 100.0).toInt ().toString ()
				
				var p = Jar ()
					 .put ("tender_type", tenderType)
					 .put ("transaction_type", transactionType)
					 .put ("amount", authString)
					 .put ("ticket_no", Pos.app.ticket.getInt ("ticket_no"))

				Pos.app.authInProgress = true
				DeviceManager.payment?.authorize (p, handler)
		  }
		  else {

				tenderView = CreditTenderView (this)
		  }
	 }

	 override fun fees (): Double {

		  if (jar ().getString ("debit_credit") == "debit") {

				return 0.0
		  }
		  else {

				return super.fees ()
		  }
	 }
	 
	 override fun applyFees () {

		  if (jar ().getString ("debit_credit") == "debit") {

				return
		  }

		  Pos.app.input.clear ()
		  var control = OpenItem ()
		  var fees = fees ()
		  		  
		  control.action (Jar ()
		  							 .put ("confirmed", true)
		  							 .put ("sku", Pos.app.config.get ("credit_service_fee").getString ("fee_sku"))
		  							 .put ("amount", fees))
	 }

	 inner class AuthHandler (val tender: PaxTender): Handler (Looper.getMainLooper ()) {

		  override fun handleMessage (m: Message) {

				Logger.i ("auth handler... ${m}")
				
				var result = m.obj as Jar
				tender.result = result.getInt ("result")
				
				Logger.i ("auth handler... ${result}")
				
				when (m.what) {

					 PosConst.PAYMENT_COMPLETE -> {

						  when (result.getInt ("result")) {

								0 -> {
						  			 									 									 
									 if (fees () > 0) {
										  
										  applyFees ()
									 }

									 authAmount = result.getDouble ("approved_amount")
									 
									 var tt = TicketTender (Jar ()
									 									 .put ("ticket_id", Pos.app.ticket.getInt ("id"))
									 									 .put ("tender_id", 2)
									 									 .put ("status", TicketTender.COMPLETE)
									 									 .put ("tender_type", tenderDesc ())
									 									 .put ("sub_tender_type", result.getString ("card_brand").toLowerCase ())
									 									 .put ("amount",  authAmount)
									 									 .put ("returned_amount", 0.0)
									 									 .put ("tendered_amount", authAmount)
									 									 .put ("locale_language", Pos.app.config.getString ("language"))
									 									 .put ("locale_country", Pos.app.config.getString ("country"))
									 									 .put ("locale_variant", "")
									 									 .put ("data_capture", result.toString ()))
									 
									 // openDrawer = returned () > 0
									 
									 var id = Pos.app.db.insert ("ticket_tenders", tt)
									 tt
									 	  .put ("id", id)
									 	  .put ("type", Ticket.TENDER)
									 
									 Pos.app.ticket.tenders.add (tt)
									 
									 Pos.app.ticket
										  .put ("state", Ticket.COMPLETE)
										  .update ()
										  .complete ()
								}
								else -> {

									 Logger.i ("pax auth error... " + result)
									 tenderView.cancel (result.getString ("response_message"))
								}
						  }
						  
						  // reset
		  
						  tendered = 0.0
						  total = 0.0
						  returned = 0.0
						  paid = 0.0
					 }
					 
					 PosConst.PAYMENT_CANCEL -> {
						  
						  tenderView?.stop ()
					 }
				}
		  }
	 }

	 override fun openDrawer (): Boolean {

		  return openDrawer
	 }
	 
	 override fun cancel () {
		  
		  DeviceManager.payment?.cancel (handler)

		  // if (serviceFeeApplied && (result != 0)) {
				
		  // 		var control = VoidItem ()
		  // 		control.action (Jar ())
		  // }
	 }

	 override fun tenderDesc (): String { return tenderType }
	 override fun tenderType (): String { return tenderType }
	 override fun printReceipt (): Boolean { return jar ().getBoolean ("print_receipt") }
}
