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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.Control

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import java.util.ArrayList
import android.widget.Button
import android.widget.TextView
import android.content.res.TypedArray
import android.content.res.Resources
import android.view.Gravity
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.widget.ProgressBar

class PosLogin (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {

	 var getPin = false
	 var cashier = ""
	 var pin = ""
	 var mask = ""
	 
	 init {
		  
		  Pos.app.inflater.inflate (Pos.app.resourceID ("login_buttons", "layout"), this)

		  var loginInput = findViewById (R.id.login_input) as TextView
		  var loginOverlay = findViewById (R.id.login_overlay) as LinearLayout
		  
		  var del = findViewById (R.id.login_del) as Button
		  del.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.white))
		  del.setOnClickListener {
				
				if (getPin) {
					 
					 if (pin.length > 0) {
						  pin = pin.substring (0, pin.length - 1)
						  mask = mask.substring (0, mask.length - 1)
						  loginInput.setText (mask)
					 }
				}
				else {
					 if (cashier.length > 0) {
						  cashier = cashier.substring (0, cashier.length - 1)
						  loginInput.setText (cashier)
					 }
				}
		  }

		  val buttons = listOf (R.id.login_button_0,
								  R.id.login_button_1,
								  R.id.login_button_2,
								  R.id.login_button_3,
								  R.id.login_button_4,
								  R.id.login_button_5,
								  R.id.login_button_6,
								  R.id.login_button_7,
								  R.id.login_button_8,
								  R.id.login_button_9)

		  var i= 0
		  for (digit in buttons) {
				
		  		var bval = i.toString ()
				
		  		var digit = findViewById (buttons [i]) as Button
		  		digit.setOnClickListener {

		  			 if (getPin) {
		  				  pin += bval
		  				  mask += "\u2022"
		  				  loginInput.setText (mask)
		  			 }
		  			 else {
		  				  cashier += bval
		  				  loginInput.setText (cashier)
		  			 }
		  		}
				i ++
		  }
		  		  
		  var login = findViewById (R.id.login_button) as Button
		  login.setOnClickListener {

		  		if (getPin) {
									 
					 loginOverlay.setVisibility (View.VISIBLE)
					 
		  			 if ((cashier.length > 0) && (pin.length > 0)) {

		  				  var employeeResult  = DbResult ("select username, password, fname, lname, profile_id, id from employees " +
																	 "where username = '" +  cashier + "'" +
																	 " and password = '" + pin.md5Hash () + "'",
																	 Pos.app.db)
									 
		  				  if (employeeResult.fetchRow ()) {

								login.setText (Pos.app.getString ("please_wait"))
								Pos.app.sendMessage (PosConst.LOGIN, employeeResult.row ());
						  }
		  				  else {
										  
		  						loginInput.setText (Pos.app.getString ("invalid_login"))
		  						cashier = ""
		  						pin = ""
		  						mask = ""
		  						getPin = false
		  				  }
		  			 }
		  		}
		  		else if (cashier.length > 0) {
								
		  			 getPin = true
		  			 loginInput.setText ("")
		  			 loginInput.setHint (Pos.app.getString ("pincode"))
		  			 login.setText (Pos.app.getString ("login"))
		  		}
		  }

		  cashier = ""
		  pin = ""
		  mask = ""
	 }
}
