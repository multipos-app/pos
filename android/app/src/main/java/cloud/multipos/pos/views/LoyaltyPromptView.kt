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

class LoyaltyPromptView (val confirmText: String, val confirmControl: ConfirmControl): DialogView (Pos.app.getString ("instant_loyalty")) {
	 
	 init {
		  
	  	  Pos.app.inflater.inflate (R.layout.confirm_layout, dialogLayout)

		  val text = findViewById (R.id.confirm_text) as PosText
		  text.setText ("Search for existing customer\nor\ncreate instant loyalty token.")
		  
		  val layout = Pos.app.inflater.inflate (R.layout.loyalty_prompt_actions, dialogActions)
		  val search = layout.findViewById (R.id.instant_loyalty_search) as Button
		  val instant = layout.findViewById (R.id.instant_loyalty_continue) as Button
		  
		  search?.setOnClickListener {

				confirmControl.confirmed (true)
				confirmControl.action (Jar ().put ("action", "search"))
				Pos.app.controlLayout.pop (this)
		  }
		  
		  instant?.setOnClickListener {

				confirmControl.confirmed (true)
				confirmControl.action (Jar ().put ("action", "loyalty"))
				Pos.app.controlLayout.pop (this)
		  }

		  Pos.app.controlLayout.push (this)
	 }
	 								 
	 override fun layout (): Int { return (R.layout.dialog_container_layout) }
	 override fun actions (dialogView: DialogView) {
		  
	 }
}
