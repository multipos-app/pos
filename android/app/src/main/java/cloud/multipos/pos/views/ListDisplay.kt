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
import cloud.multipos.pos.controls.DefaultItem
import cloud.multipos.pos.models.TicketItem

import android.content.Context
import android.util.AttributeSet
import android.widget.BaseAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener

import java.util.ArrayList
import java.util.List

abstract class ListDisplay (context: Context, attrs: AttributeSet): LinearLayout (context, attrs) {
	 	 
	 var singleSelect = false
	 
	 lateinit var listView: ListView
	 lateinit var list: MutableList <Jar>
	 lateinit var listAdapter: ListAdapter	 
	 lateinit var inflater: LayoutInflater

	 var selectValues = mutableListOf <Int> ()
	 var select = -1
	 
	 abstract fun listEntry (po: Int, view: View): View
	 abstract fun list (): MutableList <Jar>

	 open fun layout (): String { return "ticket" }
	 	 
	 init {

		  inflater = LayoutInflater.from (context)
		  inflater.inflate (Pos.app.resourceID (layout (), "layout"), this)
		  			  
		  val listLayout = findViewById (Pos.app.resourceID ("list_display", "id")) as LinearLayout
		  if (listLayout == null) {
				
				Logger.w ("create list layout failed... " + layout () + " " + Pos.app.config.getString ("metrics"))
		  }
		  else {
		  
				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  
				listView = ListView (context)
				listView.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  
				listView.setDivider (null)
				listView.setTranscriptMode (ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL)
		  
				listLayout.addView (listView)
		  
				list = list ()
				
				listAdapter = ListAdapter (context)
				listView.setAdapter (listAdapter)
		  }
	 }

	 inner class ListAdapter (context: Context): BaseAdapter () {
 
		  override fun getCount (): Int {
				
				return list.size
		  }

		  override fun getItem (pos: Int): Any {
				
				return list.get (pos)
		  }
		  
		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }
		  
		  override fun getView (pos: Int, convertView: View?, parent: ViewGroup): View {
				
				val view = if (convertView == null) View (context) else convertView
				
				if (list.size > 0) {
					 
					 return listEntry (pos, view)
				}
				
				return view
		  }
	 }
	 
	 fun redraw () {
		  
		  listAdapter.notifyDataSetChanged ()
	 }

	 /**
	  *
	  * update the list
	  *
	  */
	 
	 fun updateList () {

		  list = list ()
		  listAdapter.notifyDataSetChanged ()
		  listView.setSelection (listAdapter.getCount () - 1)
	 }

	 open fun select (): Int { return select }

	 open fun select (position: Int) {

		  if (singleSelect) {

				clearSelect ()
	 			Pos.app.selectValues.add (position)
		  }
		  
	 	  Pos.app.selectValues.add (position)
	 	  listAdapter.notifyDataSetChanged ()
	 }

	 fun clearSelect () {
		  
		  select = -1 
		  Pos.app.selectValues.clear ()

		  if (listAdapter != null) {
				
				listAdapter.notifyDataSetChanged ()
		  }
		  
		  invalidate ()
	 }
	 
	 fun selectValues (): MutableList <Int> {
		  
		  return selectValues
	 }
}
