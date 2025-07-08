
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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.*

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import com.google.android.material.button.MaterialButton
import android.widget.ListView
import androidx.core.content.ContextCompat

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale

class ItemModifiersView (val ti: TicketItem, val modifiers: MutableList <Jar>): DialogView (ti.getString ("item_desc")) {
	 
	 private val modifierViews: MutableList <View> = ArrayList ()
	 lateinit var listView: ListView

	 init  {


		  for (modifier in modifiers) {

				modifierViews.add (ItemModifierifier (modifier))
		  }
		  
		  Pos.app.inflater.inflate (R.layout.item_modifiers_layout, dialogLayout)
		  listView = findViewById (R.id.modifiers_list) as ListView
		  listView.setAdapter (ListAdapter (Pos.app.activity))
		  DialogControl.addView (this)
	 }
								 
	 override fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.item_modifiers_actions_layout, dialogActions)
  		  
		  val complete = findViewById (R.id.modifiers_complete) as Button
		  complete.setOnClickListener () {
						  
				Control.factory ("UpdateModifiers").action (Jar ()
																				.put ("ticket_item", ti)
																				.put ("modifiers", modifiers))
				DialogControl.close ()
		  }
	 }
	 
	 inner class ItemModifierifier (modifier: Jar): LinearLayout (Pos.app.activity) {

		  init {
				
				Pos.app.inflater.inflate (R.layout.item_modifier_layout, this)
				
				val desc = findViewById (R.id.modifier_desc) as TextView
				desc.setText (modifier.getString ("item_desc"))
				
				val price = findViewById (R.id.modifier_price) as TextView
				price.setText (modifier.getDouble ("item_price").currency ())

				var add = findViewById (R.id.modifier_add) as Button
				var del = findViewById (R.id.modifier_del) as Button

				when (modifier.getInt ("value")) {

					 1 -> { add.setTextColor (ContextCompat.getColor (Pos.app, R.color.pos_success)) }
					 -1 -> { del.setTextColor (ContextCompat.getColor (Pos.app, R.color.pos_danger)) }
				}
				
				add.setOnClickListener {

					 modifier.put ("value", 1)
					 add.setTextColor (ContextCompat.getColor (Pos.app, R.color.pos_success))
					 del.setTextColor (ContextCompat.getColor (Pos.app, R.color.lt_gray))
				}
				
				del.setOnClickListener {
					 
					 modifier.put ("value", -1)
					 del.setTextColor (ContextCompat.getColor (Pos.app, R.color.pos_danger))
					 add.setTextColor (ContextCompat.getColor (Pos.app, R.color.lt_gray))
				}
				
				var clear = findViewById (R.id.modifier_clear) as Button
				clear.setTextColor (if (Themed.theme == Themes.Light) Color.BLACK else Color.WHITE)
				clear.setOnClickListener {
					 
					 modifier.put ("value", 0)
					 add.setTextColor (ContextCompat.getColor (Pos.app, R.color.lt_gray))
					 del.setTextColor (ContextCompat.getColor (Pos.app, R.color.lt_gray))
				}
		  }
	 }
	 
	 inner class ListAdapter (context: Context): BaseAdapter () {
		  
		  override fun getCount (): Int {
				
				return modifiers.size
		  }

		  override fun getItem (position: Int): String? {
				
				return modifiers [position].toString ()
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return modifierViews [position]
		  }
	 }		  
}
