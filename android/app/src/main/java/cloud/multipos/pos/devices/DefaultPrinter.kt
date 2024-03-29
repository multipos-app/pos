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

import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.Logger
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.receipts.PrintCommands

class DefaultPrinter (): Printer () {

	 init {
		  
		  width = 42
		  boldWidth = 21
		  quantityWidth = 4
		  descWidth = 30
		  amountWidth = 8
		  deviceStatus = DeviceStatus.Invisible
	 }
	 
	 override fun deviceName (): String  { return "Default receipt printer" }

	 override fun print (title: String, report: String, ticket: Ticket) { }
	 override fun print (ticket: Ticket, type: Int) { }
	 override fun queue (printCommands: PrintCommands) {

		  for (pc in printCommands.list) {

				if (pc.text.length > 0) {

					 Logger.d (pc.text)
				}
		  }
	 }
	 override fun drawer () { }
	 	 
	 override fun qrcode (): Boolean { return false }
	 override fun barcode (): Boolean { return false }
}
