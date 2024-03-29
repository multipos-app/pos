/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 
package cloud.multipos.pos.pricing

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.DefaultItem

class Standard (): Pricing () {

 	 override fun apply (item: DefaultItem): Boolean {

		  var amount = 0.0

		  if (item.item.has ("price")) {

				amount = item.item.getDouble ("price")
		  }
		  
		  if ((Pos.app.ticket.getInt ("ticket_type") == Ticket.RETURN_SALE) || item.jar ().getBoolean ("is_negative")) {

				amount *= -1.0
		  }
		  		  
		  item
		  		.ticketItem ()
		  		.put ("amount", amount)

		  item
		  		.complete ()

		  return false
	 }
}
