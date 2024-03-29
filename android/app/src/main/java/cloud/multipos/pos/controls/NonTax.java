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
import cloud.multipos.pos.views.PosDisplays;

public class NonTax extends Control {

	 public NonTax () { super (); }

	 public void controlAction (Jar e) {

		  if (Pos.app.ticket.hasItems ()) {
				
				PosDisplays.message (Pos.app.getString ("invalid_operation"));
				return;
		  }
		  
		  Pos.app.ticket
				.put ("ticket_type", Ticket.SALE_NONTAX);
		  
		  Pos.app.db.update ("tickets", Pos.app.ticket.getInt ("id"), new Jar ().put ("ticket_type", Ticket.SALE_NONTAX));  		  
		  PosDisplays.message (Pos.app.getString ("non_tax_sale"));

	
	 }
}
