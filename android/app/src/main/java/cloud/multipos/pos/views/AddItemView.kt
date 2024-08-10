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
import cloud.multipos.pos.db.*

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class AddItemView (control: Control,
						 sku: String,
						 departments: MutableList <Jar>,
						 deposits: MutableList <Jar>,
						 taxes: MutableList <Jar>): PosLayout (Pos.app, null) {

	 var itemDesc: EditText
	 var depts: Spinner
	 var deps: Spinner
	 var price: EditText
	 var txs: Spinner
	 var current: String = ""
	 
	 init {

		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.add_item_layout, this)

		  var title = findViewById (R.id.add_item_title) as TextView
		  title.setText (Pos.app.activity.getString (R.string.add_item_title) + " " + sku)
				
		  itemDesc = findViewById (R.id.add_item_desc) as EditText
		  itemDesc.requestFocus ()
		  
		  val deptsResult = DbResult ("select id, department_desc, department_type from departments order by department_desc" , Pos.app.db)
		  
		  departments.add (Jar ()
									  .put ("id", 0)
									  .put ("department_desc", Pos.app.getString (R.string.department)))
								 
		  while (deptsResult.fetchRow ()) {

				var department = deptsResult.row ()
				departments.add (department)
				
				if (department.getInt ("department_type") == 4) {
					 
					 val depositResult = DbResult ("select id, item_desc from items where department_id = " + department.getInt ("id") + " order by item_desc" , Pos.app.db)
					 while (depositResult.fetchRow ()) {

						  deposits.add (depositResult.row ())
					 }
				}
		  }

		  var departmentList = mutableListOf <String> ()
		  for (d in departments) {

				departmentList.add (d.getString ("department_desc"))
		  }
				
		  depts = findViewById (R.id.add_item_departments) as Spinner
		  depts.setFocusable (true)
		  
		  ArrayAdapter <String> (Pos.app.activity, R.layout.add_item_list, departmentList)
				.also {
					 
					 adapter ->
						  adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item)
					 depts.adapter = adapter
				}

		  var depositList = mutableListOf <String> ()
		  for (d in deposits) {

				depositList.add (d.getString ("item_desc"))
		  }
		  
		  deps = findViewById (R.id.add_item_deposits) as Spinner
		  ArrayAdapter <String> (Pos.app.activity, R.layout.add_item_list, depositList)
				.also {
					 
					 adapter ->
						  adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item)
					 deps.adapter = adapter
				}

		  
		  price = findViewById (R.id.add_item_price) as EditText
		  price.addTextChangedListener (object: TextWatcher {

														override fun afterTextChanged (s: Editable) {
														}
														
														override fun beforeTextChanged (s: CharSequence, start: Int, count: Int, after: Int) {
														}
														
														override fun onTextChanged (s: CharSequence, start: Int, before: Int, count: Int) {
															 
															 if (s.toString() != current) {
																  
																  price.removeTextChangedListener (this)
																  
																  val cleanString: String = s.replace ("""[$,.]""".toRegex (), "")
																  
																  val parsed = cleanString.toDouble ()
																  val formatted = Strings.currency ((parsed / 100), false)
																  
																  current = formatted
																  price.setText (formatted)
																  price.setSelection (formatted.length)
																  
																  price.addTextChangedListener(this)
															 }
														}
        })
												 
		  var taxList = mutableListOf <String> ()
		  for (t in taxes) {

				taxList.add (t.getString ("short_desc"))
		  }
		  
		  txs = findViewById (R.id.add_item_taxes) as Spinner
		  ArrayAdapter <String> (Pos.app.activity, R.layout.add_item_list, taxList)
				.also {
					 
					 adapter ->
						  adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item)
					 txs.adapter = adapter
				}
		  
		  var complete = findViewById (R.id.add_item_complete) as Button
		  complete.setOnClickListener {
				
				if (itemDesc.getText ().toString ().length == 0) {
					 
		 			 itemDesc.setBackgroundResource (R.drawable.spinner_outline_warn)
				}
				else if (departments.get (depts.getSelectedItemPosition ()).getInt ("id") == 0) {
					 
		 			 depts.setBackgroundResource (R.drawable.spinner_outline_warn)
				}
				else {

					 var p: Double = 0.0
					 if (price.getText ().toString ().length > 0) {
						  
						  p = Currency.toDouble (price.getText ().toString ())
					 }
					 
					 var jar = Jar ()
					 jar
						  .put ("complete", true)
						  .put ("sku", sku)
						  .put ("item_desc", itemDesc.getText ().toString ())
						  .put ("price", p)
						  .put ("department_id", departments.get (depts.getSelectedItemPosition ()).getInt ("id"))
						  .put ("deposit_id", deposits.get (deps.getSelectedItemPosition ()).getInt ("id"))
						  .put ("tax_group_id", taxes.get (txs.getSelectedItemPosition ()).getInt ("tax_group_id"))
					 
					 control.action (jar)
				}
		  }
	 }
}
