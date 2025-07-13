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
 
package cloud.multipos.pos.receipts

import cloud.multipos.pos.Pos
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.devices.DeviceManager

class DefaultReceiptItem (ti: TicketItem): ReceiptItem (ti) {

	 val itemFormat = "%-" + DeviceManager.printer.quantityWidth () + "d%-" + DeviceManager.printer.descWidth () + "s%" + DeviceManager.printer.amountWidth () + "s"
	 val modFormat = "%-" + (DeviceManager.printer.quantityWidth () + DeviceManager.printer.descWidth ()) + "s%" + DeviceManager.printer.amountWidth () + "s"

	 init {
		  
		  var desc = ti.getString ("item_desc")
		  if (desc.length > DeviceManager.printer.descWidth ()) {

				desc = desc.substring (0, DeviceManager.printer.descWidth () - 1)
		  }

		  when (ti.getInt ("state")) {

				TicketItem.REDEEM -> {

					 desc += " " + Pos.app.getString ("pull_tab_redeem")
				}

				else -> { }
		  }
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (itemFormat, ti.getInt ("quantity"), desc, ti.extAmount ().currency ())))

		  ti.addons.forEach {

				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (modFormat, it.getString ("addon_description"), it.getDouble ("addon_amount").currency ())))
		  }

		  ti.mods.forEach {

				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (modFormat, "add " + it.getString ("mod_desc"), it.getDouble ("price").currency ())))
		  }
		  
		  ti.links.forEach {
					 
				var desc = it.getString ("item_desc")
				if (desc.length > DeviceManager.printer.descWidth ()) {

					 desc = desc.substring (0, DeviceManager.printer.descWidth () - 1)
				}
		  			 
				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (itemFormat, it.getInt ("quantity"), desc, it.extAmount ().currency ())))
		  }
	 }
}
