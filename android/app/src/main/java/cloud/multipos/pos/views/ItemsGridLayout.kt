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
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.db.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.GridLayout
import android.widget.ScrollView
import android.widget.GridLayout.*
import android.widget.Button
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ContextThemeWrapper 
import android.graphics.Color

class ItemsGridLayout (posMenus: PosMenus, jar: Jar, attrs: AttributeSet): PosLayout (Pos.app.activity, attrs) {
	 
	 var theme = "solid"
	 var themeWrapper = ContextThemeWrapper (Pos.app.activity, R.style.PosControlButton)
	 val item = Control.factory ("DefaultItem")
	 val items: MutableList <Jar> = mutableListOf ()
	 var pos = 0
	 var pageSize = 0
	 
	 var grid: GridLayout
	 
	 val gradient = arrayOf ("102128",
									 "11232A",
									 "12242C",
									 "13262E",
									 "142830",
									 "152932",
									 "162B34",
									 "172D36",
									 "182F38",
									 "19303A",
									 "1A323C",
									 "1B343E",
									 "1C3540",
									 "1D3742",
									 "1E3944",
									 "1E3A47",
									 "1F3C49",
									 "203E4B",
									 "213F4D",
									 "22414F",
									 "234351",
									 "244453",
									 "254655",
									 "264857",
									 "274A59",
									 "284B5B",
									 "294D5D",
									 "2A4F5F",
									 "2B5061")

	 init {

		  Pos.app.inflater.inflate (R.layout.items_grid_layout, this)
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  theme = posMenus.buttonStyle ()
		  when (posMenus.buttonStyle ()) {

				"solid" -> {

					 themeWrapper = ContextThemeWrapper (Pos.app.activity, R.style.PosControlButton)
				}
		  }
		  
		  grid = findViewById (R.id.items_grid) as GridLayout
	 	  grid.setColumnCount (jar.getInt ("cols"))
	 	  grid.setColumnCount (5)
		  
		  var itemsResult = DbResult ("select id, sku, item_desc from items where department_id = " + jar.getInt ("department_id") + " order by item_desc", Pos.app.db)
	 	  while (itemsResult.fetchRow ()) {

		  		items.add (itemsResult.row ())
		  }

		  pageSize = if (items.size > gradient.size) gradient.size - 1 else gradient.size
		  
		  render ()
	 }

	 fun render () {

		  grid.removeAllViews ()
		  
		  var i = pos
		  var gradientIndex = 0
		  
	 	  while (i < (pos + pageSize)) {

				if (i == items.size) {

					 break;
				}
				else {
					 
		  			 grid.addView (ItemButton (items.get (i), gradient.get (gradientIndex ++)))
					 i ++
				}
		  }
		  
		  pos = i

		  var pad = 0;
		  if (items.size < pageSize) {
				
				pad = pageSize - items.size

		  }
		  else if (pos == items.size) {

				pad = items.size % pageSize
		  }

		  if (pad > 0) {
				
				for (n in 1..pad) {

		  			 grid.addView (EmptyButton ())
					 pos ++
				}
		  }

		  if (items.size > pageSize) {
				
				val n = {

					 if (pos > items.size) {
						  
						  pos = 0;
					 }
					 
					 render ()
				}
				
				val p = {
					 
					 if (pos == pageSize) {
						  
						  pos = 0
					 }
					 else {
						  pos -= pageSize * 2
					 }
					 
					 render ()
				}
				
				val next = NavButton (n)
				next.setText (Pos.app.getString ("next"))
				
				val prev = NavButton (p)
				prev.setText (Pos.app.getString ("prev"))
				
				grid.addView (prev)
				grid.addView (next)
		  }
	 }

	 inner open class ItemButton (jar: Jar, color: String): LinearLayout (Pos.app.activity) {

		  var button: Button
		  
		  init {
				
				Pos.app.inflater.inflate (R.layout.item_button, this)
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.height = 0
				layoutParams.width = 0

				button = findViewById (R.id.item_button) as Button
				button.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
				
				setClickable (true)
				button = findViewById (R.id.item_button) as Button
				button.setText (jar.getString ("item_desc"))
				
				var c = Color.parseColor ("#" + color)
				button.setBackgroundColor (c)
				
				button.setOnClickListener {

					 item.action (jar)
				}
				
				setLayoutParams (layoutParams)
				setClickable (true)
		  }

		  fun setText (text: String) {

				button.setText (text)
		  }
	 }

	 inner open class NavButton (fn: () -> Unit): LinearLayout (Pos.app.activity) {

		  var button: Button
		  
		  init {
				
				Pos.app.inflater.inflate (R.layout.nav_next_button, this)
				
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.height = 0
				layoutParams.width = 0

				button = findViewById (R.id.nav_button) as Button
				button.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

				button.setOnClickListener { fn.invoke () }
				
				setLayoutParams (layoutParams)
				setClickable (true)
		  }

		  fun setText (text: String) {

				button.setText (text)
		  }
	 }

	 inner class EmptyButton (): LinearLayout (Pos.app.activity) {
		  
		  init {
				
	 			setBackgroundResource (R.color.transparent)
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.height = 0
				layoutParams.width = 0
				setLayoutParams (layoutParams)
		  }
	 }
}
