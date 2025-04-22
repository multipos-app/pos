
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

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale

class ItemModifiersView (val item: Jar): DialogView (item.getString ("item_desc")) {
	 
	 private val modViews: MutableList <View> = ArrayList ()
	 private val mods: MutableList <Jar> = ArrayList ()
	 lateinit var listView: ListView

	 init  {

		  // load item modifiers here

		  val sel = "select i.id, i.sku, i.item_desc, ip.price, ip.cost " +
		  "from departments d, items i, item_prices ip " +
		  "where d.department_id = ${item.getInt ("department_id")} and department_type = 5 and i.department_id = d.id and i.id = ip.item_id"
		  
		  Logger.x ("item mod... ${item}");
		  
		  val modsResult = DbResult (sel, Pos.app.db)
		  var pos = 0;
		  
		  while (modsResult.fetchRow ()) {
				
				var mod = modsResult.row ()
				mod.put ("pos", pos ++)
				mod.put ("value", 0)
				mods.add (mod)
				modViews.add (Mod (mod))
		  }

		  if (mods.size > 0) {

				Pos.app.inflater.inflate (R.layout.item_mods_layout, dialogLayout)
				listView = findViewById (R.id.mods_list) as ListView
				listView.setAdapter (ListAdapter (Pos.app.activity))
				DialogControl.addView (this)
		  }
	 }

								 
	 override fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.item_mods_actions_layout, dialogActions)
  		  
		  val complete = findViewById (R.id.mods_complete) as Button
		  complete.setOnClickListener () {

				for (mod in mods) {

					 if (mod.getInt ("value") != 0) {
						  
						  Control.factory ("ItemModifier").action (Jar ()
																					  .put ("ticket_item_index", item.getInt ("ticket_item_index"))
																					  .put ("mod", mod))
					 }
				}
				
				DialogControl.close ()
		  }
	 }
	 
	 inner class Mod (item: Jar): LinearLayout (Pos.app.activity) {

		  init {
				
				Pos.app.inflater.inflate (R.layout.item_mod_layout, this)
				
				val desc = findViewById (R.id.mod_desc) as TextView
				desc.setText (item.getString ("item_desc"))
				
				val price = findViewById (R.id.mod_price) as TextView
				price.setText (item.getDouble ("item_price").currency ())

				var add = findViewById (R.id.mod_add) as Button
				add.setOnClickListener {

					 item.put ("value", 1)
					 add.setTextColor (R.color.pos_success)
					 setBackgroundResource (R.color.lt_gray)
				}
				
				var del = findViewById (R.id.mod_del) as Button
				del.setOnClickListener {
					 
					 item.put ("value", -1)
					 del.setTextColor (R.color.pos_danger)
					 setBackgroundResource (R.color.lt_gray)
				}
		  }
	 }
	 
	 inner class ListAdapter (context: Context): BaseAdapter () {
		  
		  override fun getCount (): Int {
				
				return mods.size
		  }

		  override fun getItem (position: Int): String? {
				
				return mods [position].toString ()
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return modViews [position]
		  }
	 }		  
}
