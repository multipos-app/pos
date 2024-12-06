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
import cloud.multipos.pos.net.Post

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.EditText;
import android.widget.TextView;

class RegisterLayout (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {

	 var username: EditText
	 var password: EditText
	 var register: TextView
	 
	 init {
		  		  
		  Pos.app.inflater.inflate (R.layout.pos_register_layout, this)

		  username = findViewById (R.id.pos_username) as EditText
        password = findViewById (R.id.pos_password) as EditText
        register = findViewById (R.id.pos_register) as TextView

        register.setOnClickListener {
				
				Pos.app.overlay.visibility = View.VISIBLE
				
				val jar = Jar ()
					  .put ("uname", username.getText ().toString ())
					  .put ("passwd", password.getText ().toString ())
				 
				 Post ("pos/register")
					  .add (Jar ()
									.put ("uname", username.getText ().toString ())
									.put ("passwd", password.getText ().toString ()))
					  .exec (fun (result: Jar): Unit {

									 if (result.has ("register_status")) {

										  when (result.getInt ("register_status")) {

												0 -> {
													 
													 Pos.app.posInit (result)
												}
												else -> {

													 Logger.d ("bad username in register... ")
													 register.setText (Pos.app.getString ("invalid_login"))
													 username.getText ().clear ()
													 password.getText ().clear ()
												}
										  }
									 }
					  })
		  }
	 }
}
