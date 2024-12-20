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

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.controls.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.models.Customer;

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Filter.FilterResults
import android.widget.AdapterView.OnItemClickListener
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.ArrayList
import android.graphics.Color

class CustomerSearchView (val listener: InputListener?) : DialogView (Pos.app.getString ("search_customer")) {
	 
    private val list: MutableList <Jar> = ArrayList ()
    private var adapter: ListAdapter? = null
    private var selected = -1
    private var searched = false
    private var customer = Jar ()
	 
    init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

		  when (Themed.theme) {

				Themes.Dark -> {
					 
					 Pos.app.inflater.inflate (R.layout.customer_search_dark_layout, dialogLayout)
				}
				else -> {
					 
					 Pos.app.inflater.inflate (R.layout.customer_search_layout, dialogLayout)
				}
		  }
		  

        val text = findViewById (R.id.customer_search_input) as AutoCompleteTextView		  
        text.setTypeface (Views.displayFont ())
        adapter = ListAdapter (Pos.app.activity, android.R.layout.simple_dropdown_item_1line, list)
        text.setAdapter (adapter)
        text.threshold = 0
		  
        text.onItemClickListener = OnItemClickListener { _, _, position, _ -> selected = position
																			
																			customer = list [selected]

																			val cust = Customer (customer)
																			text.setText (cust.display ())
																			// Pos.app.posAppBar.customer (cust.display ())
																			// addEdit.text = Pos.app.getString ("edit_customer")
																			// Pos.app.lowerKeyboard (Pos.app.controlLayout)
		  }

		  text.requestFocus ()
		  
        if (Pos.app.ticket.has ("customer")) {
				
            // addEdit.setText (Pos.app.getString ("edit_customer"))
        }
		  
 		  Pos.app.controlLayout.push (this)
    }
	 
	 override fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.customer_search_actions, dialogActions)	  		  
		  
        val select = findViewById (R.id.customer_search_complete) as Button
        select.setOnClickListener {
				
				val cust = Customer (customer)
				Pos.app.posAppBar.customer (cust.display ())
				cust.update ()
				
				if (listener != null) {
					 
					 listener.accept (Jar ()
												 .put ("dialog", "customer")
												 .put ("customer_id", customer.getInt ("id")))						  
				}
				
 				Pos.app.controlLayout.swipeRight ()
		  }

        val addEdit = findViewById (R.id.customer_search_add_edit) as Button
        addEdit.setOnClickListener {
				
				if (Pos.app.ticket.has ("customer")) {

					 customer = Pos.app.ticket.get ("customer")
					 customer.put ("action", "edit")
				}
				else {

					 customer = Jar ()
						  .put ("fname", "")
						  .put ("lname", "")
						  .put ("email", "")
						  .put ("phone", "")
						  .put ("action", "add")
				}
				
            CustomerEditView (customer)
        }
	 }
	 
    inner class ListAdapter (context: Context?, resource: Int, list: List <Jar?>?): ArrayAdapter <Any?> (context!!, resource, list!!) {
		  
		  private val listFilter: ListFilter = ListFilter ()
		  override fun getCount (): Int {
				
				return list.size
		  }

		  override fun getItem (position: Int): String? {
				
				return list [position].getString ("contact")
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return CustomerLayout (list [position])
		  }

		  override fun getFilter (): Filter {
				
				return listFilter
		  }

		  inner class ListFilter : Filter () {
				
				override fun performFiltering (prefix: CharSequence): FilterResults {

					 Logger.d ("list filter... ${prefix}")
					 
					 val results = FilterResults ()
					 
					 if (prefix.length > 0) {
						  
						  list.clear ()
						  val limit = 10
						  searched = true
						  
						  val select = "select id, fname, lname, email, phone  from customers where " +
                    " (email like  '" + prefix + "%')" +
                    " or " +
                    " (phone like  '%" + prefix + "%')" +
						  " limit " + limit
						  
						  val custResult = DbResult (select, Pos.app.db)
						  while (custResult.fetchRow ()) {
								
								val cust = custResult.row () as Jar
								list.add (cust)
						  }
						  
						  results.values = list
						  results.count = list.size
					 }
					 else {
						  
						  results.values = list
						  results.count = 0
					 }
					 
					 return results
				}

				override fun publishResults (constraint: CharSequence?, results: FilterResults) {

					 notifyDataSetChanged ()
				}
		  }
	 }

    private inner class CustomerLayout (cust: Jar?) : LinearLayout (Pos.app.activity) {
		  
        init {
				
				Pos.app.inflater.inflate (Pos.app.resourceID ("customer_select_layout", "layout"), this)
            val contact = findViewById <View> (Pos.app.resourceID("customer_select_contact", "id")) as TextView
            contact.typeface = Views.displayFont ()
            val customer = Customer (cust)
            contact.text = customer.display ()
        }
    }

}
