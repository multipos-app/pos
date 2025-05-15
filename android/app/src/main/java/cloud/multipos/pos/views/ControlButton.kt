/*
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

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

/**
 *
 * See PosButton style for setup and padding
 *
 */

abstract class ControlButton (val params: Jar, val controls: MutableMap <String, Control>): LinearLayout (Pos.app.activity) {

	 abstract fun draw (params: Jar)
	 
	 lateinit var view: View

	 init {

		  draw (params)
		  var control: Control?  = controls.get (params.getString ("class"))
				
		  if (control == null) {
				
				var controlClass = params.getString ("class")

				if (controlClass == "Item") {
					 
					 controlClass = "DefaultItem"
				}
				
				control = Control.factory (controlClass)
				controls.put (params.getString ("class"), Control.factory (controlClass))
		  }

		  if (this::view.isInitialized) {
				
				view.setOnClickListener {
				
					 val klass = controls.get (params.getString ("class"))
				
					 if (klass != null) {
					 
						  klass.action (params.get ("params").put ("entry_mode", "control"))
					 }
				}
		  
				view.setOnLongClickListener {
				
					 return@setOnLongClickListener true
				}
		  }
	 }
}
