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

open class LoytaltyReceiptBuilder (): DefaultReceiptBuilder () {

	 override fun ticket (t: Ticket, type: Int): ReceiptBuilder {
		  
		  printCommands.clear ()
		  ticket = t
		  header ()
		  
		  feed (1)
		  printCommands
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.LINE))
		  feed (1)
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.BOLD_TEXT))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text ("Loyalty Token"))
		  printCommands
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text ("Save this pin if you misplace this token"))
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.BIG_TEXT))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.BOLD_TEXT))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text ("${Pos.app.getString ("pin")} : ${Pos.app.ticket.get ("customer").getInt ("pin")}"))
		  
		  feed (1)
		  footer ()

		  return this
	 }

	 
}
