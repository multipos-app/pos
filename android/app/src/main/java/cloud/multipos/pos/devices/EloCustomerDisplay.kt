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
 
package cloud.multipos.pos.devices;

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.models.*;

import com.elo.device.DeviceManager;
import com.elo.device.ProductInfo;
import com.elo.device.enums.EloPlatform;
import com.elo.device.inventory.PrinterSupported;
import com.elo.device.exceptions.*;
import com.elo.device.inventory.Inventory;
import com.elo.device.peripherals.CFD;
import com.elo.device.enums.Alignment;


class EloCustomerDisplay (): CustomerDisplay () {

	 var cfd: CFD? = null
	 
	 override fun deviceName (): String  { return "Elo customer display" }
	 
	 override fun start (jar: Jar) {
		  
		  val inventory = DeviceManager.getInventory (Pos.app.activity);
		  
		  if (inventory.isEloSdkSupported ()) {
				
				val dm = DeviceManager.getInstance (EloPlatform.PAYPOINT_2, Pos.app.activity);

				cfd = dm.getCfd ();
				
				cfd?.setEnabled (true)
				cfd?.clear ()
				cfd?.setBacklight (true)
				cfd?.setLine (1, Pos.app.getString ("customer_message"))
				cfd?.setLine (2, Pos.app.config.get ("business_unit").getString ("business_name"))
				success (this)
		  }
	 }

	 override fun clear () {

		  // cfd?.clear ()
	 }
	 
	 override fun update (ticket: Ticket) {

		  var line1 = Pos.app.getString ("register_open")
		  var line2 = ""

		  when (ticket.getInt ("state")) {

				Ticket.COMPLETE -> {
					 
					 var returned = 0.0
					 
					 for (tt in ticket.tenders) {
						  
						  returned += tt.getDouble ("returned_amount")
					 }
					 
					 line1 = String.format ("%-10s %5.2f", Pos.app.getString ("total"), ticket.getDouble ("total"))
					 line2 = String.format ("%-10s %5.2f", Pos.app.getString ("change"), returned)
				}

				else -> {

					 if (ticket.items.size > 0) {

						  var ti = ticket.items.last ()
						  var itemDesc = ti.getString ("item_desc")
						  if (itemDesc.length > 10) {

								itemDesc = itemDesc.substring (0, 10)
						  }
				
						  line1 = String.format ("%-10s %5.2f", itemDesc, ti.extAmount ())
					 }
		  
					 line2 = String.format ("%-10s %5.2f", Pos.app.getString ("total"), ticket.getDouble ("total"))
				}
		  }

		  cfd?.clear ()
		  cfd?.setLine (1, line1)
		  cfd?.setLine (2, line2)

		  Logger.d (line1)
		  Logger.d (line2)
	 }

	 override fun text (text: String, lineNo: Int) {

		  // cfd?.setLine (lineNo, text)
	 }
}
