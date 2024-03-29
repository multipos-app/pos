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
import cloud.multipos.pos.db.DB;
import cloud.multipos.pos.views.PosDisplays;
import cloud.multipos.pos.views.PosMenus;
import cloud.multipos.pos.devices.*;

import java.util.Date;

public class NoSale extends Control {

	 public NoSale () { super (); }

	 public void controlAction (Jar e) {

		  String timeStamp = Pos.app.db.timestamp (new Date ());

		  Pos.app
				.ticket
				.put ("ticket_type", Ticket.NO_SALE)
				.put ("state", Ticket.COMPLETE)
				.put ("star_time", timeStamp)
				.put ("complete_time", timeStamp);
		  
		  Pos.app.db.exec ("update tickets set start_time = '" + timeStamp + "', " +
										 "complete_time = '" + timeStamp + "', " +
										 "ticket_type = " + Ticket.NO_SALE + ", " +
										 "state = " + Ticket.COMPLETE + " " +
										 "where id = " + Pos.app.ticket.getInt ("id"));

		  Pos.app.cloudService.upload (Pos.app.ticket, 0);
		  Pos.app.totalsService.q (PosConst.TICKET, Pos.app.ticket, Pos.app.handler);
		  DeviceManager.printer.drawer ();

		  Pos.app.ticket ();
		  PosDisplays.clear ();
		  PosDisplays.message ("");
		  PosDisplays.home ();
		  updateDisplays ();
		  // PosMenus.home ();
	 }

	 public boolean beforeAction () {
		  
		  PosDisplays.message (Pos.app.getString ("invalid_operation"));
		  if (Pos.app.ticket.items.size () > 0) {
				return false;
		  }
		  return super.beforeAction ();
	 }
}
