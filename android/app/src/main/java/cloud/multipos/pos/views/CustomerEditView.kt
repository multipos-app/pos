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

class CustomerEditView (val customer: Jar) : DialogView (Pos.app.getString ("edit_customer")), KeyboardListener {

	 var action = "add"
	 val phoneNum = StringBuffer ()
	 val formatPhone = PhoneNumberUtils ()
	 
	 lateinit var fname: EditText
	 lateinit var lname: EditText
	 lateinit var email: EditText
	 lateinit var phone: EditText
	 
	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.customer_edit_layout, dialogLayout)

		  Logger.d ("customer edit... ${customer}")

		  fname = findViewById (R.id.customer_fname) as EditText
		  lname = findViewById (R.id.customer_lname) as EditText
		  email = findViewById (R.id.customer_email) as EditText
		  phone = findViewById (R.id.customer_phone) as EditText

		  if (customer.has ("phone")) {
				
				var ph = PhoneNumberUtils.formatNumber (customer.getString ("phone"), Locale.getDefault ().getCountry ())
				
				fname.setText (customer.getString ("fname"))
				lname.setText (customer.getString ("lname"))
				email.setText (customer.getString ("email"))
				phone.setText (ph)
				action = customer.getString ("action")
		  }
		  
		  Pos.app.controlLayout.push (this)
		  Pos.app.keyboardListeners.add (this)
	 }

	 override fun accept () {

		  val cust = Customer (customer)

		  Logger.d ("customer edit done... " + customer)
		  
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
		  Pos.app.keyboardListeners.remove (this)
		  Pos.app.controlLayout.pop (this)
	 }
	 
	 override fun onNum (num: Char) {
		  
		  phone.setText ("")
		  
		  if (phoneNum.length < 10) {
				
				phoneNum.append (num)
				
				// Logger.d ("customer edit, on num... ${num} ${phoneNum.toString ()} ${PhoneNumberUtils.formatNumber (phoneNum.toString (), "US")}")
				phone.setText (PhoneNumberUtils.formatNumber (phoneNum.toString (), "US"))
				// phone.setText (Strings.phone (phoneNum.toString (), "US"))
		  }
	 }
	 
	 override fun onBackspace () {

		  if (phoneNum.length > 0) {
				
				phoneNum.setLength (phoneNum.length - 1)
				
				// Logger.d ("customer edit, on bs... ${phoneNum.toString ()} ${PhoneNumberUtils.formatNumber (phoneNum.toString (), "US")}")
				phone.setText (PhoneNumberUtils.formatNumber (phoneNum.toString (), "US"))
				// phone.setText (Strings.phone (phoneNum.toString (), "US"))
		  }
	 }
}
