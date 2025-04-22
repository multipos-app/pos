
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
import android.widget.GridLayout
import android.widget.GridLayout.*
import com.google.android.material.button.MaterialButton
import android.widget.GridView

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale

class TabsView (val listener: InputListener, val title: String, val openTabs: ArrayList <Jar>): DialogView (title) {
	 
	 var grid: GridView
	 private val tabs: MutableList <View> = ArrayList ()
	 var tabsView: TabsView

	 init  {

		  tabsView = this
		  Pos.app.inflater.inflate (R.layout.tabs_layout, dialogLayout)
		  grid = findViewById (R.id.tabs_layout_tabs) as GridView

		  for (tab in openTabs) {

				tabs.add (Tab (tab))
		  }
		  
		  grid.setAdapter (ListAdapter (Pos.app.activity))
		  
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
	 
	 inner open class Tab (jar: Jar): LinearLayout (Pos.app.activity) {

		  init {
				
				Pos.app.inflater.inflate (R.layout.tab_button, this)

				val ticketNo = findViewById (R.id.ticket_no) as TextView
				val ticketDetails = findViewById (R.id.ticket_details) as TextView

				ticketNo.setTextColor (if (Themed.theme == Themes.Light) Color.BLACK else Color.WHITE)
				ticketDetails.setTextColor (if (Themed.theme == Themes.Light) Color.BLACK else Color.WHITE)

				ticketNo.setText ((Pos.app.getString ("ticket_no") + " : " + jar.getInt ("id") % 1000).toString ())
				ticketDetails.text = Pos.app.getString ("tab_time") + " : " + jar.getString ("start_time").utcToLocal ("HH:mm a") + "\n" +
				Pos.app.getString ("total")  + " : " + jar.getDouble ("total").currency (true) + "\n" +
				Pos.app.getString ("items")  + " : " + jar.getInt ("item_count")

				val button = findViewById (R.id.tab_button) as LinearLayout
				button.setOnClickListener {
					 
					 val result = Jar ()
						  .put ("ticket", jar)
		  
					 listener.accept (result)
					 DialogControl.close ()
				}
		  }
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
