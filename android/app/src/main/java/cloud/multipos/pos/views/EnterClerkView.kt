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
import android.widget.TextView
import android.widget.Button
import android.view.View

class EnterClerkView (val control: InputListener, title: String, prompt: String): DialogView (title) {

	 var enterClerkEcho: TextView
	 var enterClerkStatus: TextView
	 lateinit var clerk: Employee
	 
	 init {
		  
		  if (Pos.app.controls.size > 0) {
				
				val control = Pos.app.controls.removeFirst ()
				Logger.x ("enter clerk view... ${control}")
		  }
	  
		  Pos.app.inflater.inflate (R.layout.enter_clerk_layout, dialogLayout)
	  
		  val inputPrompt = findViewById (R.id.enter_clerk_prompt) as TextView
		  inputPrompt.setTextColor (fg)
		  inputPrompt.text = prompt
		  
		  enterClerkEcho = findViewById (R.id.enter_clerk_echo) as TextView
		  enterClerkEcho.setTextColor (fg)
		  enterClerkStatus = findViewById (R.id.enter_clerk_status) as TextView
		  enterClerkStatus.setTextColor (fg)
		  
		  PosDisplays.add (this)
		  DialogControl.addView (this)
		  Pos.app.input.clear ()
	 }
	 
	 override fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.enter_clerk_actions_layout, dialogActions)
  		  
		  val accept = findViewById (R.id.enter_clerk_accept) as Button
		  accept.setOnClickListener () {

				accept ()
		  }
	 }

	 override fun accept () {
		  
		  Logger.d ("accept... " + enterClerkEcho.text + " " + this::clerk.isInitialized)
		  
		  if (this::clerk.isInitialized) {

				Pos.app.input.clear ()
				PosDisplays.clear ()
				DialogControl.close ()
				control.accept (clerk)
		  }
		  else {
				
				val employeeResult  = DbResult ("select * from employees where username = '" + enterClerkEcho.text  + "'", Pos.app.db)
				if (employeeResult.fetchRow ()) {
					 
					 clerk = Employee (employeeResult.row ())
					 enterClerkStatus.text = clerk.display () + '?'
				}
				else {

					 Pos.app.input.clear ()
					 enterClerkStatus.text = Pos.app.getString (R.string.clerk_not_found)
				}
		  }
	 }
	 
	 override fun update () {
		  
		  enterClerkEcho.text = Pos.app.input.getString ()
	 }
	 
	 override fun enter () {

		  accept ()
	 }
	 
	 override fun clear () {
		  
		  enterClerkEcho.text = ""
		  enterClerkStatus.text = ""
	 }
}
