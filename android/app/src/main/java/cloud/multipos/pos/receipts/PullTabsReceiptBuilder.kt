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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.models.Customer
import cloud.multipos.pos.devices.DeviceManager

import java.util.*
import java.text.SimpleDateFormat

open class PullTabsReceiptBuilder (): DefaultReceiptBuilder () {

	 override fun footer (): ReceiptBuilder {

		  var dateFormat = SimpleDateFormat (dateTimeFormat, locale ())
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (receiptUtils.tag (ticket)))
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (dateFormat.format (Date ())))

		  return this
	 }

	 override fun summary (): ReceiptBuilder {
		  
		  val summaryFormat = "%-${(DeviceManager.printer.quantityWidth () + DeviceManager.printer.descWidth ())}s%${DeviceManager.printer.amountWidth ()}s"
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (summaryFormat,
																																		Pos.app.getString ("pull_tabs_total_items"),
																																		ticket.getInt ("item_count"))))
		  feed (2)
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.CUT))
		  
		  return this
	 }
}
