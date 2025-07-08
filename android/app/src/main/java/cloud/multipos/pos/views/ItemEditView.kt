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
import android.widget.EditText
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.view.inputmethod.EditorInfo
import android.text.InputType

class ItemEditView (val control: Control, val item: Jar): EditView () {
	 
	 var sku: PosEditText
	 var desc: PosEditText
	 var price: PosEditText
	 var cost: PosEditText
	 var departmentsSpinner: Spinner?
	 var depositsSpinner: Spinner?
	 var taxSpinner: Spinner?
	 var departments = mutableListOf <SpinnerItem> ()
	 var deposits = mutableListOf <SpinnerItem> ()
	 var taxes = mutableListOf <SpinnerItem> ()
	 var ticketItemIndex = -1

	 init {

		  var layout = Pos.app.inflater.inflate (R.layout.item_edit_layout, editLayout) as LinearLayout
		  		  
		  sku = posEditField (R.id.item_edit_sku, layout, item.getString ("sku"))
		  desc = posEditField (R.id.item_edit_desc, layout, item.getString ("item_desc"))
		  price = posEditField (R.id.item_edit_price, layout, item.getDouble ("price").currency ())
		  cost = posEditField (R.id.item_edit_cost, layout, item.getDouble ("cost").currency ())

		  home ()
		  
		  // build the dropdowns

		  var spinnerIndex = 0

		  // Departments

		  val departmentsResult = DbResult ("select * from departments order by department_desc" , Pos.app.db)
		  departments.add (SpinnerItem (Jar ()
														.put ("id", 0)
														.put ("department_desc",
																Pos.app.getString (R.string.item_edit_department_desc)), "department_desc"))

		  var depositIDs = mutableListOf <Int> ()  // save the department ids

		  var i = 1
		  while (departmentsResult.fetchRow ()) {

				var department = departmentsResult.row ()
				departments.add (SpinnerItem (department, "department_desc"))

				// get the deposit departments for deposits
				
				if (department.getInt ("department_type") == Department.DEPOSIT) {

					 depositIDs.add (department.getInt ("id"))
				}

				if (department.getInt ("id") == item.getInt ("department_id")) {

					 spinnerIndex = i
				}
				i ++
		  }
		  
		  departmentsSpinner = layout.findViewById (R.id.item_edit_departments) as Spinner
		  departmentsSpinner?.setFocusable (true)	  
		  ArrayAdapter <SpinnerItem> (Pos.app.activity, R.layout.pos_spinner_dropdown_list, departments)
				.also {

					 adapter -> {
						  
						  adapter.setDropDownViewResource (R.layout.pos_spinner_dropdown)
					 }
					 
					 departmentsSpinner?.adapter = adapter
				}
		  
		  departmentsSpinner?.setSelection (spinnerIndex)

		  // Deposits, get all items in a depsit department
		  
		  deposits.add (SpinnerItem (Jar ()
													.put ("id", 0)
													.put ("item_desc", Pos.app.getString (R.string.item_edit_deposits_desc)), "item_desc"))

		  spinnerIndex = 0
		  i = 0
		  for (depositID in depositIDs) {

				val itemsResult = DbResult ("select * from items where department_id = ${depositID}", Pos.app.db)

				while (itemsResult.fetchRow ()) {

					 var depositItem = itemsResult.row ()
					 
					 deposits.add (SpinnerItem (depositItem, "item_desc"))
					 if (depositItem.getInt ("id") == item.getInt ("item_link_id")) {

						  spinnerIndex = i
					 }
					 i ++
				}
		  }

		  depositsSpinner = layout.findViewById (R.id.item_edit_deposits) as Spinner
		  depositsSpinner?.setFocusable (true)	  
		  ArrayAdapter <SpinnerItem> (Pos.app.activity, R.layout.pos_spinner_dropdown_list, deposits)
				.also {

					 adapter -> {
						  
						  adapter.setDropDownViewResource (R.layout.pos_spinner_dropdown)
					 }
					 
					 depositsSpinner?.adapter = adapter
				}
		  
		  depositsSpinner?.setSelection (spinnerIndex)

		  // Taxes
 
		  taxes.add (SpinnerItem (Jar ()
												.put ("id", 0)
												.put ("short_desc", Pos.app.getString (R.string.item_edit_tax_desc)), "short_desc"))
		  
		  val taxesResult = DbResult ("select * from tax_groups", Pos.app.db)
		  
		  spinnerIndex = 0
		  i = 1
		  while (taxesResult.fetchRow ()) {
				
				var taxGroup = taxesResult.row ()

				taxes.add (SpinnerItem (taxGroup, "short_desc"))
				
				if (taxGroup.getInt ("id") == item.getInt ("tax_group_id")) {

					 spinnerIndex = i
				}
				i ++
		  }
		  
		  taxSpinner = layout.findViewById (R.id.item_edit_taxes) as Spinner
		  ArrayAdapter <SpinnerItem> (Pos.app.activity, R.layout.pos_spinner_dropdown_list, taxes)
				.also {
					 
					 adapter ->
						  
						  adapter.setDropDownViewResource (R.layout.pos_spinner_dropdown)
					 
					 taxSpinner?.adapter = adapter
				}
		  
		  taxSpinner?.setSelection (spinnerIndex)

		  if (item.has ("ticket_item_index")) {
				
				ticketItemIndex = item.getInt ("ticket_item_index")
		  }
	 
		  Pos.app.keyboardView.show (this)
	 }

	 override fun complete () {
		  		  
		  val item = Jar ()

		  item.put ("sku", sku.getText ().toString ())
		  item.put ("item_desc", desc.getText ().toString ())
		  item.put ("price", price.getText ().toString ())
		  item.put ("cost", cost.getText ().toString ())
		  
		  var index = departmentsSpinner?.getSelectedItemPosition ()!!
		  item.put ("department_id", departments [index].jar.getInt ("id"))
		  
		  index = depositsSpinner?.getSelectedItemPosition ()!!
		  item.put ("deposit_item_id", deposits [index].jar.getInt ("id"))
				
		  index = taxSpinner?.getSelectedItemPosition ()!!
		  item.put ("tax_group_id", taxes [index].jar.getInt ("id"))
		  
		  val result = Jar ()
				.put ("item", item)
				.put ("ticket_item_index", ticketItemIndex)
		  
		  control.action (result)
		  
		  Pos.app.keyboardView.swipeLeft ()
		  Pos.app.input.clear ()
		  PosDisplays.clear ()
	 }
}

