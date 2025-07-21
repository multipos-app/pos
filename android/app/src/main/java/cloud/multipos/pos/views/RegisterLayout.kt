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
import cloud.multipos.pos.net.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;

class RegisterLayout (context: Context, attrs: AttributeSet): PosLayout (context, attrs), NetworkListener {

	 var username: EditText
	 var password: EditText
	 var register: TextView
	 var network: TextView
	 
	 init {
		  
		  var layout = Pos.app.inflater.inflate (R.layout.pos_register_layout, this)

		  username = layout.findViewById (R.id.pos_username) as EditText
        password = layout.findViewById (R.id.pos_password) as EditText
        register = layout.findViewById (R.id.pos_register) as TextView
        network = layout.findViewById (R.id.pos_register_network) as TextView
		  
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

													 Logger.w ("bad username in register... ")
													 register.setText (Pos.app.getString ("invalid_login"))
													 username.getText ().clear ()
													 password.getText ().clear ()
												}
										  }
									 }
					  })
		  }

		  Network (this)
	 }

	 override fun onSuccess () {

		  network?.setText (Pos.app.getString ("fa_wifi_on"))
		  network?.setTextColor (Color.parseColor (Pos.app.getString (R.color.white)))
		  Logger.d ("wifi success...")
	 }
	 
	 override fun onFail () {

		  network?.setText (Pos.app.getString ("fa_wifi_off"))
		  network?.setTextColor (Color.parseColor (Pos.app.getString (R.color.red)))
		  Logger.d ("wifi fail...")
	 }
}
