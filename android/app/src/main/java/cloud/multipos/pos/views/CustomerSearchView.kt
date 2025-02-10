/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.Customer

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Filter.FilterResults
import android.widget.AdapterView.OnItemClickListener
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.ArrayList
import android.graphics.Color
import android.widget.ListView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Button

class CustomerSearchView (): EditView () {
	 
    val list = mutableListOf <Jar> ()	 
	 var search: PosEditText?
	 var listView: ListView
    var listAdapter: ListAdapter
	 lateinit var customer: Customer

    init {

		  var layout = Pos.app.inflater.inflate (R.layout.customer_search_layout, editLayout) as LinearLayout

		  search = posEditField (R.id.customer_search, layout, "")
		  search?.setOnChange (this)
		  search?.requestFocus ()
		  
		  listView = ListView (context)
		  listView.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  listView.setDivider (null)
		  listView.setTranscriptMode (ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL)
		  
		  val customerList = editLayout.findViewById (R.id.customer_list_container) as LinearLayout?
		  
		  customerList?.addView (listView)
        listAdapter = ListAdapter (Pos.app)
		  listView.setAdapter (listAdapter)
		  
 		  Pos.app.keyboardView.push (this)
    }
	 
	 override fun onChange () {

		  search ()
	 }
	 
	 override fun actions () {

		  var layout = Pos.app.inflater.inflate (R.layout.customer_search_actions_layout, actionsLayout) as LinearLayout

		  var complete = layout.findViewById (R.id.customer_search_complete) as Button
		  complete.setOnClickListener {

				// check if customer was selected
				
				if (this::customer.isInitialized) {
					 
					 Pos.app.posAppBar.customer (customer.display ())
					 Pos.app.ticket.put ("customer_id", customer.getInt ("id"))
					 Pos.app.ticket.put ("customer", customer)
				}
				
				Pos.app.keyboardView.swipeLeft ()
		  }
		  
		  var update = layout.findViewById (R.id.customer_search_add_edit) as Button
		  update.setOnClickListener {

				Pos.app.keyboardView.swipeLeft ()
				if (this::customer.isInitialized) {
					 
					 Pos.app.posAppBar.customer (customer.display ())
					 Pos.app.ticket.put ("customer_id", customer.getInt ("id"))
					 Pos.app.ticket.put ("customer", customer)
				}
				else {
					 
					 // new customer

					 customer = Customer ()
				}
				
				CustomerEditView (customer.getInt ("id"))
		  }
	 }
	 
	 inner class ListAdapter (context: Context): BaseAdapter () {
		  
		  override fun getCount (): Int {
				
				return list.size
		  }

		  override fun getItem (position: Int): String? {
				
				return list [position].toString ()
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return CustomerView (list [position], position)
		  }
	 }
	 
    private inner class CustomerView (cust: Jar, position: Int) : LinearLayout (Pos.app.activity) {

		  init {
				
				val view = Pos.app.inflater.inflate (R.layout.customer_line_layout, this)
				view.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
				
				val name  = view.findViewById (R.id.customer_name) as PosText?
				val email  = view.findViewById (R.id.customer_email) as PosText?
				val phone  = view.findViewById (R.id.customer_phone) as PosText?
				
				name?.setText (cust.getString ("fname") + " " + cust.getString ("lname"))
				email?.setText (cust.getString ("email"))
				phone?.setText (cust.getString ("phone").phone ())

				if ((position % 2) == 0) {

					 setBackgroundResource (R.color.light_even_bg)
				}

				val select  = view.findViewById (R.id.customer_select) as LinearLayout?
				select?.setOnClickListener {

					 customer = Customer (list [position])
					 search?.setText (customer.display ())
				}
		  }
    }
	 
	 private fun search () {

		  val limit = 10
		  
		  list.clear ()
		  var search = search?.getText ().toString ()
		  
		  val select = "select * from customers where " +

		  "pin = '${search}'" +
		  " or " +
		  "fname like '${search}%'" +
		  " or " +
		  "lname like '${search}%'" +
		  " or " +
		  "email like '${search}%'" +
		  " or " +
		  "phone like '%${search}%'" +
		  " limit " + limit
		  
		  val custResult = DbResult (select, Pos.app.db)
		  while (custResult.fetchRow ()) {

				list.add (custResult.row ())
		  }
		  
		  listAdapter.notifyDataSetChanged ()
	 }
}
