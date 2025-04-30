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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.*

import android.widget.Button
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.content.Context
import android.widget.TextView
import android.text.InputFilter
import android.telephony.PhoneNumberUtils
import java.util.Locale
import android.text.TextWatcher
import android.text.Editable
import java.util.UUID
import android.widget.Spinner
import android.telephony.PhoneNumberFormattingTextWatcher

class CustomerEditView (val customerID: Int) : EditView () {

	 lateinit var customer: Customer
	 
	 // var pin: PosText
	 var fname: PosEditText
	 var lname: PosEditText
	 var email: PosEditText
	 var phone: PosEditText
	 var addr: PosEditText
	 var city: PosEditText
	 var state: Spinner?
	 var postalCode: PosEditText
	 
	 init {

		  customer = Customer ().select (customerID)
		  
		  var layout = Pos.app.inflater.inflate (R.layout.customer_edit_layout, editLayout) as LinearLayout
		  
		  // pin = layout.findViewById (R.id.customer_pin) as PosText
		  // pin.setText (customer.getString ("pin"))
		  
		  fname = posEditField (R.id.customer_fname, layout, customer.getString ("fname"))
		  lname = posEditField (R.id.customer_lname, layout, customer.getString ("lname"))
		  email = posEditField (R.id.customer_email, layout, customer.getString ("email"))
		  phone = posEditField (R.id.customer_phone, layout, customer.getString ("phone"))
		  addr = posEditField (R.id.customer_addr, layout, customer.getString ("addr_1"))
		  city = posEditField (R.id.customer_city, layout, customer.getString ("city"))
		  postalCode = posEditField (R.id.customer_postal_code, layout, customer.getString ("postal_code"))

		  state = editLayout.findViewById (R.id.customer_state) as Spinner?
		  state?.setFocusable (true)
		  ArrayAdapter <String> (Pos.app.activity, R.layout.pos_spinner_dropdown_list, States.list ())
				.also {
					 
					 adapter ->
						  adapter.setDropDownViewResource (R.layout.pos_spinner_dropdown)
					 
					 state?.adapter = adapter
				}

		  state?.setSelection (States.indexOf (customer.getString ("state")))
		  
		  Pos.app.keyboardView.push (this)
		  home ()
	 }
	 
	 override fun complete () {

		  if (!this::customer.isInitialized) {

				customer = Customer ()
		  }
		  
		  customer
				.put ("is_dirty", true)
				.put ("type", "customers")		  
				.put ("fname", fname.getText ().toString ())
				.put ("lname", lname.getText ().toString ())
				.put ("email", email.getText ().toString ())
				.put ("phone", phone.getText ().toString ())
				.put ("addr_1", addr.getText ().toString ())
				.put ("city", city.getText ().toString ())
				.put ("postal_code", postalCode.getText ().toString ())
		  
		  var index: Int? = 0
		  index = state?.getSelectedItemPosition ()
		  val pos: Int = index!!
		  customer.put ("state", States.abbr (pos))
		  		  
		  if (customerID > 0) {
				
				Pos.app.db.update ("customers", customerID, customer)
		  }
		  else {
				
				Pos.app.db.insert ("customers", customer)
		  }

		  Pos.app.ticket.updates.add (customer)  // attach it to the current sale
		  Pos.app.keyboardView.swipeLeft ()
	 }
}
