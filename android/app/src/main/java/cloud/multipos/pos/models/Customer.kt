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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.receipts.*

class Customer (val customerID: Int): Jar (), Model {

	 lateinit var customer: Jar
	 
	 init {

		  if (customerID > 0) {
				
				val customerResult = DbResult ("select * from customers where id = ${customerID}", Pos.app.db)
				if (customerResult.fetchRow ()) {
					 
					 customer = customerResult.row ()
					 copy (customer)
				}
		  }
		  else {

				put ("id", 0)
					 .put ("fname", "DAN")
					 .put ("lname", "THEMAN")
					 .put ("email", "dan${(0..1000).random ()}@email.com")
					 .put ("phone", "9071112222")
					 .put ("addr_1", "One Main St")
					 .put ("city", "Topeka")
					 .put ("state", "KS")
					 .put ("postal_code", "99999")
		  }
	 }
	 
	 override fun update () {

		  if (customer != null) {

				copy (customer)
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

				sb.append (getString ("phone").phone ())
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
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (getString ("phone").phone ()))
		  }

		  if (has ("email") && (getString ("email").length > 0)) {

				pc
					 .add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (getString ("email")))
		  }
		  
		  return pc
	 }
}
