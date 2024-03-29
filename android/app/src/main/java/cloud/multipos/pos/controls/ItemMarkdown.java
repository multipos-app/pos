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

import java.util.ArrayList;

import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.views.PosMenus;

public abstract class ItemMarkdown extends TicketModifier {

	 public ItemMarkdown () { super (); }
	 public boolean afterAction () { return true; }

	 public abstract double getMarkdown (TicketItem ti, Jar Jar);

	 public void controlAction (Jar Jar) {
		  
		  if (Pos.app.ticket.currentItem == null) return;
		  if (Pos.app.ticket.currentItem.addons.size () > 0) return;  // only one addon???

		  for (TicketItem ti: Pos.app.ticket.selectItems ()) {

				double markdown = getMarkdown (ti, jar);
				
				if (markdown == 0) {
					 continue;
				}

				String desc = null;
				if (jar.has ("receipt_desc")) {
					 
					 desc = jar.getString ("receipt_desc");
				}
				else {
					 desc = Pos.app.getString ("discount");
				}
				
				TicketItemAddon tia = new TicketItemAddon ();
				tia
					 .put ("ticket_item_id", ti.getInt ("id"))
					 .put ("addon_amount", markdown)
					 .put ("addon_quantity", ti.getInt ("quantity"))
					 .put ("addon_description", desc);
				
				if (jar.has ("addon_id")) {
					 tia.put ("addon_id", jar.getInt ("addon_id"));
				}
		  
				long id = Pos.app.db.insert ("ticket_item_addons", tia);
				tia
					 .put ("id", id);
		  
				ti.addons.add (tia);
		  }

		  Pos.app.ticket.update ();
		  updateDisplays ();
		  // PosMenus.home ();
	 }
}
