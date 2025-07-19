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

class Customer (): Jar (), Model {

	 companion object {

		  fun factory (jar: Jar): Customer {

				return Customer (jar)
		  }
	 }
	 
	 constructor (jar: Jar): this () {

		  copy (jar)
	 }
	 
	 init {

		  copy (Jar ()
						.put ("id", 0)
				  		.put ("pin", pin ())
						.put ("uuid", "".uuid ())
						.put ("fname", "")
						.put ("lname", "")
						.put ("email", "")
						.put ("phone", "")
						.put ("addr_1", "")
						.put ("city", "")
						.put ("state", "")
						.put ("postal_code", "")
						.put ("total_sales", 0)
						.put ("total_visits", 0)
						.put ("loyalty_points", 0))
	 }

	 fun select (id: Int): Customer {
		  
		  val customerResult = DbResult ("select * from customers where id = ${id}", Pos.app.db)
		  
		  if (customerResult.fetchRow ()) {
	 
				copy (customerResult.row ())
		  }

		  return this
	 }
	 
	 fun select (field: String, key: String): Customer {
		  
		  val customerResult = DbResult ("select * from customers where ${field} = '${key}'", Pos.app.db)
		  
		  if (customerResult.fetchRow ()) {
	 
				copy (customerResult.row ())
		  }
		  return this
	 }

	 override fun update (): Customer {

		  val customerResult = DbResult ("select * from customers where uuid = '${this.getString ("uuid")}'", Pos.app.db)
		  if (customerResult.fetchRow ()) {
				
				Pos.app.db ().update ("customers", "uuid", this.getString ("uuid"), this)
		  }
		  else {

				Pos.app.db ().insert ("customers", this)
		  }
		  
		  return this
	 }

	 fun instant (): Customer {

		  put ("fname", Pos.app.getString ("instant_loyalty"))
		  return this
	 }
	 
	 fun pin (): Int {

		  // find a unique pin
		  
		  var pin = (1000..9999).random ()
		  val customerResult = DbResult ("select id, pin from customers where pin = ${pin}", Pos.app.db)
		  while (customerResult.fetchRow ()) {
				
				var customer = customerResult.row ()
				Logger.d ("pin exists... ${customer}")
				pin = (1000..9999).random ()
		  }

		  return pin
	 }
	 
	 override fun display (): String {

		  val sb = StringBuffer ()
		  
		  if (has ("fname") && (getString ("fname").length > 0)) {

				sb.append (getString ("fname"))
		  }
		  
		  if (has ("lname") && (getString ("lname").length > 0)) {

				if (sb.length > 0) sb.append (" ")
				sb.append (getString ("lname"))
		  }

		  if (has ("phone") && (getString ("phone").length > 0)) {
				
				if (sb.length > 0) sb.append (" \u2022 ")
				sb.append (getString ("phone").phone ())
		  }
		  
		  if (has ("email") && (getString ("email").length > 0)) {
				
				if (sb.length > 0) sb.append (" \u2022 ")
				sb.append (getString ("email"))
		  }

		  return sb.toString ()
	 }

	 fun name (): String {

		  val sb = StringBuffer ()
		  
		
		  if (has ("lname") && (getString ("lname").length > 0)) {

				return getString ("lname")
		  }

		  if (has ("phone") && (getString ("phone").length > 0)) {
				
				return getString ("phone").phone ()
		  }
		  
		  if (has ("email") && (getString ("email").length > 0)) {
				
				return getString ("email")
		  }

		  return "unknown"
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
