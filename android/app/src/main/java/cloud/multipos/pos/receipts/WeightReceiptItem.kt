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

class WeightReceiptItem (ti: TicketItem): ReceiptItem (ti) {

	 val itemFormat = "%-${DeviceManager.printer.quantityWidth ()}s%-${DeviceManager.printer.descWidth ()}s%${DeviceManager.printer.amountWidth ()}.3f"

	 init {
		  
		  var quantity = " ".fill (DeviceManager.printer.quantityWidth ())
		  if (ti.getInt ("quantity") > 0) {
				
				quantity = String.format ("%-${DeviceManager.printer.quantityWidth ()}d", ti.getInt ("quantity"))
		  }

		  var desc = ti.getString ("item_desc")
		  if (desc.length > DeviceManager.printer.descWidth ()) {
				
				desc = desc.substring (0, DeviceManager.printer.descWidth () - 1)
		  }

		  val metric = ti.getDouble ("metric") / 1000.0
		  
		  val line = String.format (itemFormat, quantity, desc, ti.getDouble ("metric"))
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (line))
	 }
}
