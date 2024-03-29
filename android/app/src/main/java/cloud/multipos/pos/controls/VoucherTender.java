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
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.models.Ticket;
import cloud.multipos.pos.models.TicketTender;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VoucherTender extends Tender {

	 public VoucherTender () {

		  super (null);
		  Logger.d ("voucher tender... ");
	 }

	 public String tenderType () { return "voucher"; } 
	 public int tenderID () { return TicketTender.VOUCHER; } 
	 public String tenderDesc () { return "voucher"; }
	 public boolean openDrawer () { return true; }
	 public boolean printReceipt () { return false; }

	 public void completePayment () {

		  String voucher = new Jar ()
				.put ("type", "voucher")
				.put ("bu_id", Pos.app.buID ())
				.put ("pos_no", Pos.app.posNo ())
				.put ("ticket_no", Pos.app.ticket.getInt ("ticket_no"))
				.put ("timestamp", Pos.app.db.timestamp (null))
				.put ("amount", Pos.app.ticket.getDouble ("total") * -1.0)
				.put ("sku", jar ().getString ("deposit_sku"))
				.toString ();
	 
		  Pos.app.ticket
				.put ("ticket_type", Ticket.VOUCHER)
				.put ("data_capture", voucher);
		  
		  Pos.app.db.update ("tickets", Pos.app.ticket);

		  super.payment ();
	 }
	 
	 public double amount () {

		  double total = Pos.app.ticket.getDouble ("total");
		  
		  switch (Pos.app.config.getString ("currency")) {

		  case "DKK":

				/**
				 *
				 * Denmark round to nearest .50
				 *
				 */
				
				double decimal = 0;
				double tmp = new BigDecimal (total - (int) total).setScale (2, RoundingMode.HALF_UP).doubleValue ();

				if ((tmp >= 0) && (tmp <= .25)) {
					 decimal = 0;
				}
				else if ((tmp > .25) && (tmp <= .50)) {
					 decimal = .50;
				}
				else if ((tmp > .50) && (tmp <= .75)) {
					 decimal = .50;
				}
				else {
					 decimal = 1.0;
				}
				
				total = total - tmp + decimal;
				break;
		  }
		  
		  return total;
	 }
}
