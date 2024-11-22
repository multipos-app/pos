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
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.views.PosDisplays;

public class ItemText extends Control {

	 public ItemText () { super (); }

	 public void controlAction (Jar Jar) {
		  
		  if (Pos.app.ticket.currentItem == null) return;

		  Jar tmp  = new Jar ().put ("item_detail", jar.getString ("text"));
		  String text = tmp.toString ();
		  
		  if (Pos.app.ticket.currentItem.hasAddons ()) {

				for (int i=0; i<Pos.app.ticket.currentItem.getList ("ticket_item_addons").size (); i++) {
					 
					 TicketItemAddon tia = (TicketItemAddon) Pos.app.ticket.currentItem.getList ("ticket_item_addons").get (i);
					 
					 if (tia.getString ("addon_data_capture").length () > 0) {
						  
						  Jar dc = new Jar (tia.getString ("addon_data_capture"));
						  
						  Logger.d ("tia in text item...");
						  Logger.d (tia.json ());

						  if (dc.getString ("item_detail").length () > 0) {
								
								tia.put ("addon_data_capture", text);
								Pos.app.db.exec ("update ticket_item_addons set addon_data_capture = '" + text + "' where id = " + tia.getInt ("id"));
						  }
					 }
				}
		  }
		  else {
				
				TicketItemAddon ticketItemAddon = TicketItemAddon.factory ();
				ticketItemAddon
					 .put ("ticket_item_id", Pos.app.ticket.currentItem.getInt ("id"))
					 .put ("addon_type", 1)
					 .put ("addon_amount", 0)
					 .put ("addon_quantity", 0)
					 .put ("addon_description", Pos.app.getString ("item_details"))
					 .put ("addon_data_capture", text);
				
				ticketItemAddon
					 .put ("id", Pos.app.db.insert ("ticket_item_addons", ticketItemAddon));
				
				Pos.app.ticket.currentItem.getList ("ticket_item_addons").add (ticketItemAddon);
		  }
		  
		  updateDisplays ();
	 }
}
