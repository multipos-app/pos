
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
import cloud.multipos.pos.util.extensions.*

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import android.widget.GridView

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale

class OpenTabsView (val listener: InputListener, val title: String, val openTabs: ArrayList <Jar>): DialogView (title) {
	 
	 var grid: GridView
	 private val tabs: MutableList <View> = ArrayList ()

	 init  {

		  Pos.app.inflater.inflate (R.layout.open_tabs_layout, dialogLayout)
		  grid = findViewById (R.id.open_tabs_grid) as GridView

		  var i = 0
		  for (tab in openTabs) {

				tabs.add (Tab (tab, i ++))
		  }
		  
		  grid.setAdapter (ListAdapter (Pos.app))
		  
		  PosDisplays.add (this)
		  DialogControl.addView (this)
	 }
	 
	 override fun accept () {
		  
		  val result = Jar ()
				.put ("tab_no", Pos.app.input.getString ())
		  
		  listener.accept (result)
		  DialogControl.close ()
	 }
	 
	 override fun enter () {

		  accept ()
	 }
	 
	 override fun update () {
	 }
	 
	 override fun clear () {
	 }
	 
	 inner open class Tab (jar: Jar, i: Int): LinearLayout (Pos.app.activity) {

		  init {

				Logger.x ("tab... ${jar}")
				
				Pos.app.inflater.inflate (R.layout.open_tab_line, this)

				val openTabNo = theme (findViewById (R.id.open_tab_no) as TextView)
				val openTabStart = theme (findViewById (R.id.open_tab_start) as TextView)
				val openTabItems = theme (findViewById (R.id.open_tab_items) as TextView)
				val openTabTotal = theme (findViewById (R.id.open_tab_total) as TextView)

				openTabNo.setText (jar.getInt ("id").jobNo ())
				openTabStart.setText (jar.getString ("start_time").utcToLocal ("HH:mm a"))
				openTabItems.setText (jar.getInt ("item_count").toString ())
				openTabTotal.setText (jar.getDouble ("total").currency (false))
				
				val layout = this.findViewById (R.id.open_tab_layout) as LinearLayout

				if (i.even ()) {
					 
					 layout.setBackgroundResource (if (Themed.theme == Themes.Light) R.color.light_even_bg else R.color.dark_even_bg)
				}
				else {

					 layout.setBackgroundResource (if (Themed.theme == Themes.Light) R.color.light_odd_bg else R.color.dark_odd_bg)
				}

				layout.setClickable (true)
				layout.setOnClickListener {
					 
					 val result = Jar ()
						  .put ("open_tab", jar)
		  
					 listener.accept (result)
					 DialogControl.close ()
				}
		  }
	 }

	 fun theme (text: TextView): TextView {

		  text.setTextColor (if (Themed.theme == Themes.Light) Color.BLACK else Color.WHITE)
		  return text
	 }
	 
	 inner class ListAdapter (context: Context): BaseAdapter () {
		  
		  override fun getCount (): Int {
				
				return tabs.size
		  }

		  override fun getItem (position: Int): String? {
				
				return tabs [position].toString ()
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return tabs [position]
		  }
	 }		  
}
