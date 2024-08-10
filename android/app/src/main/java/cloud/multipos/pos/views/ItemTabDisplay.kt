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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.devices.ScanListener
import cloud.multipos.pos.net.Post

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher

import cloud.multipos.pos.databinding.ItemTabDisplayBinding

data class DropDown (val list: MutableList <Jar>, val spinner: Spinner)

class ItemTabDisplay (context: Context, attrs: AttributeSet): PosLayout (context, attrs),  PosTabListener {
	 
	 var departments: MutableList <Jar>
	 var departmentsSpinner: Spinner

	 var deposits: MutableList <Jar> = mutableListOf ()
	 var depositsSpinner: Spinner

	 var taxes: MutableList <Jar> = mutableListOf ()
	 var taxesSpinner: Spinner
	 
	 var sku: EditText
	 var itemDesc: EditText
	 var price: EditText
	 var cost: EditText

	 var item = Jar ()
	 
	 init {
		  
		  Pos.app.inflater.inflate (R.layout.item_tab_display, this)
		  
		  sku = findViewById (R.id.add_item_sku) as EditText
		  itemDesc = findViewById (R.id.add_item_desc) as EditText
		  sku.requestFocus ();

		  departments = mutableListOf <Jar> ()
		  departmentsSpinner = findViewById (R.id.add_item_departments) as Spinner
		  
		  initSpinner ("id", 
							"department_desc",
							Pos.app.getString (R.string.add_item_department_desc),
							"select id, department_desc from departments order by department_desc",
							departments,
							departmentsSpinner)
		  
		  deposits = mutableListOf <Jar> ()
		  depositsSpinner = findViewById (R.id.add_item_deposits) as Spinner
		  
		  initSpinner ("id", 
							"item_desc",
							Pos.app.getString (R.string.add_item_deposits_desc),
							"select i.id, i.item_desc from items i, departments d where i.department_id = d.id and d.department_type = 4",
							deposits,
							depositsSpinner)
		  
		  taxes = mutableListOf <Jar> ()
		  taxesSpinner = findViewById (R.id.add_item_taxes) as Spinner
		  
		  initSpinner ("tax_group_id", 
							"short_desc",
							Pos.app.getString (R.string.tax),
							"select tax_group_id, short_desc from taxes",
							taxes,
							taxesSpinner)
		  		  
		  // suppliers = mutableListOf <Jar> ()
		  // suppliersSpinner = findViewById (R.id.add_item_suppliers) as Spinner
		  
		  // initSpinner ("id", 
		  // 					"supplier_name",
		  // 					Pos.app.getString (R.string.suppliers),
		  // 					"select id, supplier_name from suppliers",
		  // 					suppliers,
		  // 					suppliersSpinner)
		  		  
		  price = findViewById (R.id.add_item_price) as EditText
		  cost = findViewById (R.id.add_item_cost) as EditText

		  // packageQuantity = findViewById (R.id.add_item_package_quantity) as EditText
		  
		  val reset = findViewById (R.id.add_item_reset) as Button
		  reset.setOnClickListener {

				reset ()				
		  }
		  
		  val complete = findViewById (R.id.add_item_complete) as Button
		  complete.setOnClickListener {

				val p = Jar ()
					 .put ("sku", sku.getText ().toString ())
					 .put ("item_desc", itemDesc.getText ().toString ().toUpperCase ().replace ("'", "`"))
					 .put ("price", price.getText ().toString ())
					 .put ("cost", cost.getText ().toString ())
					 // .put ("package_quantity", packageQuantity.getText ().toString ())
					 .put ("department_id", departments.get (departmentsSpinner.getSelectedItemPosition ()).getInt ("id"))
					 .put ("deposit_item_id", deposits.get (depositsSpinner.getSelectedItemPosition ()).getInt ("id"))
					 .put ("tax_group_id", taxes.get (taxesSpinner.getSelectedItemPosition ()).getInt ("tax_group_id"))
					 // .put ("supplier_id", suppliers.get (suppliersSpinner.getSelectedItemPosition ()).getInt ("id"))

				if (item.has ("id")) {

					 p
						  .put ("item_id", item.getInt ("id"))
						  .put ("item_prices_id", item.get ("item_prices").getInt ("id"))
						  .put ("inv_item_id", item.get ("inv_items").getInt ("id"))
				}
				
				Post ("pos/pos-item-update")
					 .add (p)
					 .exec (fun (result: Jar): Unit {
																		
									if (result.getInt ("status") == 0) {

										 reset ()
									}
									else {

										 reset ()
									}
					 })
				}
	 }

	 override fun view (): View { return this }
	 
	 override fun onScan (scan: String) {

		  sku.setText (scan)
		  val item = Item (Jar ().put ("sku", scan))
		  
		  itemDesc.setText (item.getString ("item_desc"))
		  price.setText (Strings.currency (item.getDouble ("price"), false))
		  cost.setText (Strings.currency (item.getDouble ("cost"), false))
		  
		  // packageQuantity.setText (item.get ("inv_items").getInt ("package_quantity").toString ())
		  
		  position (taxes, item.getInt ("tax_group_id"), taxesSpinner, "tax_group_id")
		  position (departments, item.getInt ("department_id"), departmentsSpinner, "id")

		  // position (suppliers, item.get ("item_prices").getInt ("supplier_id"), suppliersSpinner, "id")
		  
		  // if (item.has ("item_links") && (item.get ("item_links").size > 0)) {
				
		  // 		position (deposits, item.get ("item_links").getInt ("id"), depositsSpinner, "id")
		  // }


																				 // Cloud ()
		  // 		.post (listOf ("pos", "edit-item", scan), fun (result: Jar): Unit {
							  
		  // 					  if (result.getInt ("status") == 0) {  // poplate fields

		  // 																		 item = result.get ("item")

		  // 																		 Logger.x ("item... " + item)
																				 
		  // 																		 itemDesc.setText (item.getString ("item_desc"))
		  // 																		 price.setText (Strings.currency (item.get ("item_prices").getDouble ("price"), false))
		  // 																		 cost.setText (Strings.currency (item.get ("item_prices").getDouble ("cost"), false))
		  // 																		 packageQuantity.setText (item.get ("inv_items").getInt ("package_quantity").toString ())
																				 
		  // 																		 position (departments, item.getInt ("department_id"), departmentsSpinner, "id")
		  // 																		 position (suppliers, item.get ("item_prices").getInt ("supplier_id"), suppliersSpinner, "id")
		  // 																		 position (taxes, item.get ("item_prices").getInt ("tax_group_id"), taxesSpinner, "tax_group_id")
																				 
		  // 																		 if (item.has ("item_links") && (item.get ("item_links").size > 0)) {
																					  
		  // 																			  position (deposits, item.get ("item_links").getInt ("id"), depositsSpinner, "id")
		  // 																		 }
		  // 					  }
		  // 		})
	 }

	 fun position (list: MutableList <Jar>, id: Int, spinner: Spinner?, field: String) {
		  
		  var pos = -1
		  var i = 0
		  for (p in list) {
								
				if (p.getInt (field) == id) {
					 
					 pos = i
					 break
				}
				i ++
		  }
		  
		  if (pos > 0) {
				
				spinner?.setSelection (pos)
		  }
		  else {

				spinner?.setSelection (0)
		  }
	 }
	 
	 fun initSpinner (id: String,
							desc: String,
							hint: String,
							select: String,
							list: MutableList <Jar>,
							spinner: Spinner) {
		  
		  list.add (Jar ()
							 .put (id, 0)
							 .put (desc, hint))

		  val tmp = mutableListOf <String> ()
		  tmp.add (hint)
		  val result = DbResult (select, Pos.app.db)
		  		  
		  while (result.fetchRow ()) {

				var row = result.row ()
								
				list.add (row)
				tmp.add (row.getString (desc))
		  }
		  
		  ArrayAdapter <String> (Pos.app.activity, R.layout.item_tab_list, tmp)
				.also {
					 
					 adapter -> adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item)
					 spinner.adapter = adapter
				}
	 }

	 fun reset () {
		  
	 	  sku.setText ("")
		  itemDesc.setText ("")
		  price.setText ("")
		  cost.setText ("")		  
		  departmentsSpinner.setSelection (0)
		  depositsSpinner.setSelection (0)
		  taxesSpinner.setSelection (0)

		  item = Jar ()
		  
		  sku.requestFocus ();
	 }

}
