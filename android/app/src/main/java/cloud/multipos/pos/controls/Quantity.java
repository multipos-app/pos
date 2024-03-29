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
import cloud.multipos.pos.db.DbResult;
import cloud.multipos.pos.addons.*;
import cloud.multipos.pos.views.PosDisplays;

public class Quantity extends TicketModifier {

	 public Quantity () { super (); }

	 public void controlAction (Jar jar) {

		  jar (jar);
		  		  
		  if (Pos.app.input.hasInput ()) {

				// if (Pos.app.input.getString ().length () > 4) {

				// 	 PosDisplays.message (Pos.app.getString ("invalid_operation"));
				// 	 Pos.app.input.clear ();
				// 	 return;
				// }

				Pos.app.quantity = Pos.app.input.getInt ();
				
				PosDisplays.message (Pos.app.getString ("quantity") + " " + Pos.app.quantity);
				Pos.app.input.clear ();
				return;
		  }

		  if (Pos.app.ticket.hasItems ()) {
				
				for (TicketItem ti: Pos.app.ticket.selectItems ()) {

					 quantity (ti);

					 for (TicketItem linkItem: ti.links) {
							  
						  quantity (linkItem);
					 }
				}
		  }
				
		  updateDisplays ();
	 }

	 private void quantity (TicketItem ti) {
		  		  
		  int multiplier = 0;
		  int quantity = ti.getInt ("quantity");
		  
		  if ((jar () != null) && jar ().has ("multiplier")) {
				
				multiplier = jar ().getInt ("multiplier");
		  }
		  
		  if (multiplier != 0) {
				
				quantity += multiplier;
				
				if (quantity == 0) {
					 
					 if (!Pos.app.config.getBoolean ("disable_quantity_void")) {
						  
						  Control.factory ("VoidItem").action (null);
						  return;
					 }
					 
					 quantity = 1;
				}
		  }
		  else {
				return;
		  }
		  				
		  ti.put ("quantity", quantity);
		  if (ti.hasAddons ()) {
					 
		  		for (TicketItemAddon tia: ti.addons) {

					 Logger.x ("qty addon... " + tia);

					 if (ti.has ("addon_id")) {

						  DbResult addonResult  = new DbResult ("select * from addons where id = " + ti.getInt ("addon_id"), Pos.app.db);
								
						  while (addonResult.fetchRow ()) {
						  
								Jar addon = addonResult.row ();

								if (addon != null) {
									 
									 Addon a = (Addon) LoadClass.get ("cloud.multipos.pos.addons." + addon.getString ("class"));
									 a.apply (ti, addon);
								}
						  }
					 }
					 else {
						  
						  tia.put ("addon_quantity", quantity);
						  Pos.app.db.exec ("update ticket_item_addons set " +
														 "addon_quantity = " + tia.getInt ("addon_quantity") + ", " + 
														 "addon_amount = " + tia.getDouble ("addon_amount") + " " + 
														 "where id = " + tia.getInt ("id"));
					 }
				}
		  }
 	  
		  Pos.app.db.exec ("update ticket_items set quantity = " + quantity + " where id = " + Pos.app.ticket.currentItem.getInt ("id"));
		  Pos.app.ticket.update ();
		  
		  String desc =
				Pos.app.ticket.currentItem.getString ("item_desc") + ", " +
				Pos.app.ticket.currentItem.getInt ("quantity") + "@" + 
				String.format ("%.2f", Pos.app.ticket.currentItem.getDouble ("amount"));		  
	 }
	 
	 public boolean beforeAction () {
		  
		  return super.beforeAction ();
	 }
}
