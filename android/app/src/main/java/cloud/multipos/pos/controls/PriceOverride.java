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

import cloud.multipos.pos.R;
import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.views.PosDisplays;

import java.util.ArrayList;

public class PriceOverride extends TicketModifier implements InputListener {

	 public PriceOverride () { super (); }

	 public void controlAction (Jar Jar) {

		  // new NumberDialog (this, R.string.enter_amount, NumberDialog.CURRENCY, true)
		  // 		.value (0)
		  // 		.show ();
	 }

	 public void accept (Jar result) {

		  double value = result.getDouble ("value");

		  double price = value;
				
		  for (TicketItem ti: Pos.app.ticket.selectItems ()) {
								
				if (ti.hasAddons ()) {

					 
					 for (TicketItemAddon tia: ti.addons) {
						  
						  Pos.app.db.exec ("delete from ticket_item_addons where id = " + tia.getLong ("id"));
					 }
					 
					 ti.addons.clear ();
				}

				if (ti.getInt ("state") == TicketItem.REFUND_ITEM) {
					 price *= -1;
				}
				
				Logger.d ("accept... " + price);
				
				ti.put ("price", price);
				ti.put ("amount", price);
				Pos.app.db.update ("ticket_items", ti);
		  }
		  
		  Pos.app.ticket.update ();
		  PosDisplays.update ();
	 }
	 
	 public String type () { return ""; }
}
