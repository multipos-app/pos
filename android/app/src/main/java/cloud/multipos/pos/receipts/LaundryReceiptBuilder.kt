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

open class LaundryReceiptBuilder (): DefaultReceiptBuilder () {

	 override fun customer (): ReceiptBuilder {
		  
		  if (Pos.app.ticket.getInt ("customer_id") > 0) {

				val customer = Customer (Pos.app.ticket.getInt ("customer_id"))
		  
				separator ()
				printCommands.addAll (customer.receipt ())
		  }
		  
		  return this
	 }
}
