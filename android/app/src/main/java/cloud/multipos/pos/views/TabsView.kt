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

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.util.*

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import android.widget.GridLayout
import com.google.android.material.button.MaterialButton

class TabsView (val listener: InputListener, title: String): DialogView (title) {
	 
	 var tabs: LinearLayout
	 
	 init  {
		  
		  Pos.app.inflater.inflate (R.layout.tabs_layout, dialogLayout)
		  
		  tabs = findViewById (R.id.tabs_layout_items) as LinearLayout
		  tabs.addView (StartTab ())		  
		  Pos.app.controlLayout.push (this)
	 }

	 inner open class StartTab (): LinearLayout (Pos.app.activity) {

		  init {
				
				Pos.app.inflater.inflate (R.layout.start_tab_button, this)
				val button = findViewById (R.id.start_tab_button) as MaterialButton
				button.setTypeface (Views.icons ())
				
				button.setOnClickListener {

					 Logger.d ("add tab...")
				}
		  }
	 }
}
