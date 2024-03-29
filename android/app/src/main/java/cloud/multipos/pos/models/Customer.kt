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
 
package cloud.multipos.pos.models

import cloud.multipos.pos.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.receipts.*

class Customer (val customer: Jar?): Jar (), Model {

	 init {

		  if (customer != null) {
				
				copy (customer)
		  }
	 }
	 
	 override fun update () {

		  if (customer != null) {

				copy (customer)
				Pos.app.customer = this
				Pos.app.ticket.put ("customer", this)
				Pos.app.ticket.put ("customer_id", this.getInt ("id"))
				Pos.app.db ().exec ("update tickets set customer_id = " + customer?.getInt ("id") + " where id = " + Pos.app.ticket.getInt ("id"))
		  }
	 }
	 
	 override fun display (): String {

		  val sb = StringBuffer ()
		  
		  if (has ("fname") && (getString ("fname").length > 0)) {

				sb.append (getString ("fname") + " ")
		  }
		  
		  if (has ("lname") && (getString ("lname").length > 0)) {

				sb.append (getString ("lname") + " ")
		  }

		  if (has ("phone") && (getString ("phone").length > 0)) {

				sb.append (Strings.phone (getString ("phone"), Pos.app.config.getString ("locale")) + " ")
		  }

		  if (has ("email") && (getString ("email").length > 0)) {

				sb.append (getString ("email"))
		  }

		  return sb.toString ()
	 }

	override fun receipt (): PrintCommands {

		  val pc = PrintCommands ()

		  pc
				.add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (Pos.app.getString ("customer")))

		  var name = ""
		  
		  if (has ("fname") && (getString ("fname").length > 0)) {

				name += getString ("fname") + " "
		  }
		  
		  if (has ("lname") && (getString ("lname").length > 0)) {

				name +=  getString ("lname")
		  }
		  
		  if (name.length > 0) {

				pc
					 .add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (name))
		  }
		  
		  if (has ("phone") && (getString ("phone").length > 0)) {

				pc
					 .add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (Strings.phone (getString ("phone"), Pos.app.config.getString ("locale"))))
		  }

		  if (has ("email") && (getString ("email").length > 0)) {

				pc
					 .add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (getString ("email")))
		  }
		  
		  return pc
	 }
}
