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

abstract class SelectView (listener: InputListener, list: ArrayList <Jar>, title: String ): DialogView (title) {

	 abstract fun line (context: Context, jar: Jar, position: Int, ): LinearLayout
	 
	 var i: Int = 0
	 var listView: LinearLayout

	 init {

		  Pos.app.inflater.inflate (R.layout.select_layout, dialogLayout)
		  listView = findViewById (R.id.select_layout_items) as LinearLayout
		  
		  for (l in list) {

				Logger.d ("select line... " + l)
				listView.addView (line (context, l, i ++))
		  }
		  
		  Pos.app.controlLayout.push (this)
	 }
}
