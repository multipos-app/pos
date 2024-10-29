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

class SizeView (val listener: InputListener, list: ArrayList <Jar>, title: String): SelectView (listener, list, title) {
	 
	 override fun line (context: Context, jar: Jar, position: Int): LinearLayout {

		  return SizeLine (context, jar, position)
	 }
	 
	 inner class SizeLine (context: Context, line: Jar, position: Int): LinearLayout (context) {
		  
		  init {
				
				Pos.app.inflater.inflate (R.layout.select_line, this)
				
				val desc = findViewById (R.id.select_line_desc) as TextView
				val amount = findViewById (R.id.select_line_amount) as TextView
				
				desc.setText (line.getString ("desc"))
				amount.setText (Strings.currency (line.getDouble ("price"), false))

				when (Themed.theme) {

					 Themes.Light -> {

						  desc.setTextColor (Color.BLACK)
						  amount.setTextColor (Color.BLACK)
					 }
					 Themes.Dark -> {

						  desc.setTextColor (Color.WHITE)
						  amount.setTextColor (Color.WHITE)
					 }
				}
				
				setOnClickListener () {

					 listener.accept (Jar ()
												 .put ("desc", line.getString ("desc"))
												 .put ("amount", line.getDouble ("amount"))
												 .put ("position", position))

					 Pos.app.controlLayout.swipeRight ()
				}
		  }
	 }
}
