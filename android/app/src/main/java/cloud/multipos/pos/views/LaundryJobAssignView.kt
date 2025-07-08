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
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.db.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import java.util.Stack
import android.text.method.PasswordTransformationMethod

class LaundryJobAssignView (val laundryJobsView: LaundryJobsView): PosLayout (Pos.app, null), PosKeyListener, ThemeListener {

	 lateinit var assignPrompt: PosButton
	 lateinit var assignEcho: PosText
	 val sb = StringBuffer ()

	 var pin = ""
	 var passcode = ""
	 
	 init {

		  val layout = Pos.app.inflater.inflate (R.layout.laundry_job_assign_layout, laundryJobsView.controls ())
		  
		  assignEcho = layout.findViewById (R.id.job_assign_echo) as PosText
		  assignEcho.text = Pos.app.getString ("ellipsis")
		  assignPrompt = layout.findViewById (R.id.job_assign_prompt) as PosButton

		  assignPrompt.setOnClickListener {
				
				sb.setLength (0)
				pin = ""
				passcode = ""
				assignEcho.text = Pos.app.getString ("ellipsis")
				assignPrompt.text = Pos.app.getString ("job_enter_pin")
		  }
		  
		  val cancel = layout.findViewById (R.id.job_cancel) as PosButton?
		  cancel?.setOnClickListener {

				sb.setLength (0)
				pin = ""
				passcode = ""
				Pos.app.auxView.hide ()
		  }
	 }

	 // pos key lisenter impl
	 
	 override fun text (text: String) {

		  sb.append (text)
		  assignEcho.text = sb.toString ()
	 }

	 override fun enter () {

		  if (sb.length > 0) {
				
				if (pin.length == 0) {

					 pin = sb.toString ()
					 sb.setLength (0)
					 assignEcho.text = ""
					 assignEcho.setTransformationMethod (PasswordTransformationMethod.getInstance ())
				}
				else {
					 
					 passcode = sb.toString ()
					 
					 var employeeResult  = DbResult ("select username, password, fname, lname, profile_id, id from employees " +
																"where username = '" +  pin + "'" +
																" and password = '" + passcode.md5Hash () + "'",
																Pos.app.db)
					 					 
		  			 if (employeeResult.fetchRow ()) {

						  var emp = employeeResult.row ()
						  laundryJobsView.complete (Jar ()
																  .put ("employee_id", emp.getInt ("id")))
						  Pos.app.auxView.hide ()
					 }
					 else {
						  
						  sb.setLength (0)
						  pin = ""
						  passcode = ""
						  assignEcho.text = Pos.app.getString ("ellipsis")
						  assignPrompt.text = Pos.app.getString ("job_invalid")
					 }
				}
		  }
	 }

	 override fun del () {

		  if (sb.length > 0) {

				sb.setLength (sb.length - 1)
				assignEcho.text = sb.toString ()

				if (sb.length == 0) {

					 assignEcho.text = Pos.app.getString ("ellipsis")
				}
		  }
	 }
}
