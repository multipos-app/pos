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
import cloud.multipos.pos.devices.DeviceManager

open class ReceiptItem (ti: TicketItem) {

	 val printCommands = PrintCommands ()

	 companion object {

		  fun factory (ti: TicketItem): ReceiptItem {

				when (Pos.app.ticket.getInt ("ticket_type")) {

					 Ticket.WEIGHT_ITEMS -> {

						  return WeightReceiptItem (ti)
					 }

					 else -> {
						  return DefaultReceiptItem (ti)
					 }
				}
		  }
	 }
}
