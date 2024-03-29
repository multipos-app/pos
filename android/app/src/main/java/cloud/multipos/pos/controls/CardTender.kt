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
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.TicketTender

class CardTender (): Tender (null) {

	 override fun tenderType (): String { return "card" }
	 override fun tenderDesc (): String { return "card" }
	 override fun printReceipt (): Boolean { return false }

	 override fun subTenderDesc (): String {

		  when (Pos.app.ticket.getInt ("id") % 6) {

				0 -> return "visa"
				1 -> return "mastercard"
				2 -> return "amex"
				3 -> return "discover"
				4 -> return "dankort"
				else -> return "unknown" 
		  }
	 }
	 
	 override fun openDrawer (): Boolean {

		  if (jar () != null) {
				
		  		return jar ().getBoolean ("open_drawer")
		  }
		  return false
	 }
}
