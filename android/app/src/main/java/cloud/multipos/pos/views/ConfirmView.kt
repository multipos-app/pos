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
import cloud.multipos.pos.controls.*

import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import android.graphics.Color

class ConfirmView (val confirmText: String, val confirmControl: ConfirmControl): DialogView (Pos.app.getString ("confirm")) {

	 init {
		  
		  Pos.app.inflater.inflate (R.layout.confirm_layout, dialogLayout)
	  
		  val text = findViewById (R.id.confirm_text) as PosText
		  text.setText (confirmText)
		  text.setTextColor (fg)
		  
		  Pos.app.controlLayout.push (this)
	 }

	 override fun accept () {

		  confirmControl.confirmed (true)
		  confirmControl.action (Jar ())
		  Pos.app.controlLayout.pop (this)
	 }
}
