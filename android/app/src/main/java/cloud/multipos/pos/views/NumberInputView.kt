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
import android.view.View

class NumberInputView (val control: InputListener, title: String, prompt: String, val type: Int, val decimalPlaces: Int): DialogView (title) {

	 var inputEcho: TextView
	 var decimalVal = 0.0

	 init {
		  		  
		  Pos.app.inflater.inflate (R.layout.number_input_layout, dialogLayout)
	  
		  val inputPrompt = findViewById (R.id.number_input_prompt) as TextView

		  inputPrompt.setTextColor (fg)
		  inputPrompt.text = prompt
		  inputEcho = findViewById (R.id.number_input_echo) as TextView
		  inputEcho.setTextColor (fg)
		  
		  PosDisplays.add (this)
		  Pos.app.controlLayout.push (this)
		  update ()
	 }
	 
	 override fun accept () {
		  
		  val result = Jar ().put ("value", Pos.app.input.getString ())
		  
		  if (decimalVal != 0.0) {
				
				result.put ("value", decimalVal)
				control.accept (result)
		  }
		  
		  Pos.app.input.clear ()
		  Pos.app.controlLayout.swipeRight ()
	 }
	 
	 override fun update () {
		  
		  when (type) {

				InputListener.INTEGER -> {
					 
					 inputEcho.text = Pos.app.input.getString ()
				}
				
				InputListener.DECIMAL -> {

					 decimalVal = Pos.app.input.getDouble ()
					 for (i in 1..decimalPlaces) decimalVal = decimalVal / 10.0
					 
					 inputEcho.text = String.format ("%.${decimalPlaces}f", decimalVal)
				}
				
				InputListener.CURRENCY -> {
					 
					 decimalVal = Pos.app.input.getDouble ()
					 inputEcho.text = (Pos.app.input.getDouble () / 100.0).currency ()
				}
		  }
	 }
	 
	 override fun enter () {

		  accept ()
	 }
	 
	 override fun clear () {

		  if (Pos.app.input.length () == 0) {
		  
				inputEcho.setHint (Pos.app.getString ("register_open"));
		  }		  
	 }
}
