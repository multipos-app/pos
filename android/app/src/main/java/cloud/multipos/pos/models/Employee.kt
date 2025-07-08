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
import cloud.multipos.pos.receipts.*

class Employee (val employee: Jar?): Jar (), Model {

	 val deny = mutableListOf <String> ()
		  
	 init {

		  if (employee != null) {
								
				copy (employee)

				var employeeProfileResult =  DbResult ("select pd.profile_class from profiles p, profile_permissions pd " +
																	"where p.id = pd.profile_id and p.id = " +
																	employee.getInt ("profile_id"),
																	Pos.app.db)
		  
				while (employeeProfileResult.fetchRow ()) {

		  			 var employeeProfile = employeeProfileResult.row ();
		  			 deny.add (employeeProfile.getString ("profile_class"));
				}
		  }
	 }
	 
	 fun deny (): MutableList <String> { return deny }
	 
	 override fun display (): String {

		  val sb = StringBuffer ()
		  
		  if (has ("fname") && (getString ("fname").length > 0)) {
				
				sb.append (getString ("fname") + " ")
		  }
		  
		  if (has ("lname") && (getString ("lname").length > 0)) {
				
				sb.append (getString ("lname") + " ")
		  }
		  
		  return sb.toString ()
	 }

	 fun update () { }
	 
	 override fun receipt (): PrintCommands {
		  
		  val pc = PrintCommands ()
		  pc
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (Pos.app.getString ("clerk_desc")))
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (display ()))
		  
		  return pc
	 }
}
