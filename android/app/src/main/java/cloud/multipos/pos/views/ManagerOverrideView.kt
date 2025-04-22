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
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.models.*

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import java.util.ArrayList
import android.widget.TextView
import android.view.Gravity
import android.graphics.Typeface
import android.view.View
import android.widget.Button

class ManagerOverrideView (control: Control): DialogView (Pos.app.getString ("manager_override")) {

	 var echo: TextView
	 var manager = ""
	 var pin = ""
	 var mask = false
	 
	 init {

		  Pos.app.inflater.inflate (R.layout.manager_override_layout, dialogLayout)
		  		  
		  echo = findViewById (Pos.app.resourceID ("echo", "id")) as TextView
		  // accept.setText (Pos.app.getString ("enter_manager_number"));
		  
		  PosDisplays.add (this)
		  DialogControl.addView (this)
	 }

	 override fun accept () {
		  
		  if ((manager.length > 0) && (pin.length > 0)) {
					 
				val select = "select username, password, fname, lname, profile_id, id from employees " +
				"where username = '" +  manager + "'" + " and password = '" + pin.md5Hash () + "'"
					 
				val employeeResult  = DbResult (select, Pos.app.db)

				
				if (employeeResult.fetchRow ()) {
					 
					 Pos.app.ticket.put ("manager", Employee (employeeResult.row ()))
					 // Pos.app.controlLayout.swipeLeft ()
					 PosDisplays.remove (this)
				}
				else {
						  
					 echo.setText ("");
					 echo.setHint (Pos.app.getString ("invalid_login"));
					 manager = ""
					 pin = ""
					 mask = false
				}
		  }
		  else if (manager.length > 0) {

				manager = echo.text.toString ()
				mask = true
				echo.setText ("");
				echo.setHint (Pos.app.getString ("pincode"));
		  }
		  
		  Pos.app.input.clear ()
	 }

	 
	 override fun update () {

		  if (mask) {

				var tmp = ""
				for (i in 1..Pos.app.input.length ()) {

					 pin = Pos.app.input.getString ()
		  			 tmp += Pos.app.getString (R.string.echo_dot)
				}
				
				echo.text = tmp
		  }
		  else {
				
				echo.text = Pos.app.input.getString ()
				manager = Pos.app.input.getString ()
		  }
	 }
	 
	 override fun clear () {

		  if (Pos.app.input.length () == 0) {
		  
		  		echo.setHint (Pos.app.getString ("manager_number"));
				manager = ""
				pin = ""
				mask = false
		  }		  
	 }
}
