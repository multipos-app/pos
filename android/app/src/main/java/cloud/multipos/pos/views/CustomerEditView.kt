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
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.*

import android.widget.Button
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import android.text.InputFilter
import android.telephony.PhoneNumberUtils
import java.util.Locale
import android.text.TextWatcher
import android.text.Editable
import java.util.UUID

class CustomerEditView (customer: Jar) : DialogView ("") {

	 var action = "add"

	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.customer_edit_layout, this)

		  Logger.d ("customer edit... ${customer}")

		  val fname = findViewById (R.id.customer_fname) as EditText
		  val lname = findViewById (R.id.customer_lname) as EditText
		  val email = findViewById (R.id.customer_email) as EditText
		  val phone = findViewById (R.id.customer_phone) as EditText

		  // phone.addTextChangedListener (object: TextWatcher {
					 
		  // 			 override fun onTextChanged (s: CharSequence, start: Int, before: Int, count: Int) {
						  
		  // 				  Logger.d ("ph edit... " + s)
		  // 				  var ph = PhoneNumberUtils.formatNumber (s.toString (), Locale.getDefault ().getCountry ())
		  // 				  Logger.d ("phone... " + ph)
		  // 			 }
					 
		  // 			 override fun afterTextChanged (s: Editable) { }
		  // 			 override fun beforeTextChanged (s: CharSequence, start: Int, before: Int, count: Int) { }
		  // 		})

		  val title = findViewById (R.id.customer_edit_title) as PosText
		  if (customer == null) {

				title.setText (Pos.app.getString ("add_customer"))
		  }

		  if (customer.has ("phone")) {
				
				var ph = PhoneNumberUtils.formatNumber (customer.getString ("phone"), Locale.getDefault ().getCountry ())
				
				fname.setText (customer.getString ("fname"))
				lname.setText (customer.getString ("lname"))
				email.setText (customer.getString ("email"))
				phone.setText (ph)
				action = customer.getString ("action")
		  }
		  
		  val cont = findViewById (R.id.customer_edit_continue) as Button
		  cont.setOnClickListener {
				
				val cust = Customer (customer)

				cust
					 .put ("contact", fname.getText ().toString () + " " + lname.getText ().toString ())
					 .put ("fname", fname.getText ().toString ())
					 .put ("lname", lname.getText ().toString ())
					 .put ("email", email.getText ().toString ())
					 .put ("phone", phone.getText ().toString ())

				if (customer.getString ("action")  == "add") {
					 
					 cust.put ("uuid", UUID.randomUUID ().toString ())  // add a uuid
				}

				Pos.app.ticket.put ("customer", cust)
				
				Pos.app.customer = cust
				Pos.app.ticket.put ("customer", cust)
				val customer = Customer (Pos.app.customer)
				Pos.app.posAppBar.customer (customer.display ())
				Pos.app.controlLayout.swipeRight ()
		  }

		  Pos.app.controlLayout.push (this)
	 }
}
