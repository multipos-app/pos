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
import cloud.multipos.pos.net.Post

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import android.view.ContextThemeWrapper 
import android.widget.GridLayout
import android.widget.GridLayout.*
import android.widget.LinearLayout
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.provider.Settings.Secure;

class PosInit (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {

	 var bu: Int = -1
	 var config: Int = -1
	 val locations: MutableList <InitButton> = mutableListOf ()
	 val configs: MutableList <InitButton> = mutableListOf ()
	 
	 lateinit var register: TextView
	 
	 init {

				Pos.app.inflater.inflate (R.layout.pos_init_layout, this)

				register = findViewById (R.id.pos_init_register) as Button
				register.setVisibility (View.INVISIBLE);
				
				// val version = findViewById (R.id.app_version) as TextView
				// version.setText (BuildConfig.VERSION_NAME)

				register.setOnClickListener {
					 
					 if ((bu >= 0) && (config >= 0)) {
						  
						  Pos.app.overlay.visibility = View.VISIBLE
						  
						  val display = Pos.app.getWindowManager ().getDefaultDisplay ();

						  val buID = Pos.app.posInit.getList ("business_units").get (bu).getInt ("business_unit_id")
						  val configID = Pos.app.posInit.getList ("pos_configs").get (config).getInt ("pos_config_id")
						  
						  Pos.app.local.put ("business_unit_id", buID)
						  Pos.app.local.put ("pos_config_id", configID)
 						  
						  val init = Jar ()
								.put ("merchant_id", Pos.app.posInit.getInt ("merchant_id"))
								.put ("business_unit_id", buID)
								.put ("pos_config_id", configID)
				  				.put ("density", Pos.app.getResources ().getDisplayMetrics ().densityDpi)
						  		.put ("model", android.os.Build.MODEL)
								.put ("sdk", android.os.Build.VERSION.SDK_INT)
								.put ("android_release", android.os.Build.VERSION.RELEASE)
								.put ("android_id", Secure.getString (Pos.app.activity.getContentResolver (), Secure.ANDROID_ID))
								.put ("fingerprint", android.os.Build.FINGERPRINT.contains ("generic"))
								// .put ("version_name", Pos.app.getString ("version_name"))
								// .put ("version_code", Pos.app.getString ("version_code"))
								.put ("version_name", "version")
								.put ("version_code", "86")
								.put ("display_width", display.getWidth ())
								.put ("display_height", display.getHeight ())

						  Post ("pos/init")
								.add (init)
								.exec (fun (result: Jar): Unit {
											  											  
											  if (result.getInt ("status") == 0) {

													Pos.app.local.put ("id", result.get ("device").getInt ("id"))
													Pos.app.local.put ("access_token", result.get ("device").getString ("access_token"))
													Pos.app.local.put ("refresh_token", result.get ("device").getString ("refresh_token"))

													Pos.app.download ()
											  }
								})
					 }
				}
				
				register.setTextColor (Color.WHITE)
								
				var locatinGrid = findViewById (R.id.pos_init_locations) as GridLayout
				locatinGrid.setPadding (15, 15, 0, 0)
				var pos = 0
				for (bu in Pos.app.posInit.getList ("business_units")) {

					 locatinGrid.addView (LocationButton (context, bu, pos ++))
				}
				
				var configGrid = findViewById (R.id.pos_init_configs) as GridLayout
				configGrid.setPadding (15, 15, 0, 0)
				pos = 0
				for (c in Pos.app.posInit.getList ("pos_configs")) {

					 configGrid.addView (ConfigButton (context, c, pos ++))
				}
	 }

	 inner class LocationButton (context: Context, p: Jar, pos: Int): InitButton (context) {

		  init {
				
				button.text = p.getString ("business_name")
				locations.add (this)

				button.setOnClickListener {

					 for (l in locations) {

		 				  l.button.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.light_tint))
					 }

					 button.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.select_tint))

					 bu = pos
					 if ((bu >= 0) && (config >= 0)) {
						  
						  register.setVisibility (View.VISIBLE);
					 }
				}
		  }
	 }
	 
	 inner class ConfigButton (context: Context, p: Jar, pos: Int): InitButton (context) {

		  init {
				
				button.text = p.getString ("config_desc")
				configs.add (this)
				
				button.setOnClickListener {

					 for (c in configs) {

		 				  c.button.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.light_tint))
					 }
					 
					 button.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.select_tint))
					 
					 config = pos
					 if ((bu >= 0) && (config >= 0)) {
						  
						  register.setVisibility (View.VISIBLE);
					 }
				}
		  }
	 }

	 inner open class InitButton (context: Context): LinearLayout (context) {

		  var button: Button

		  init {

				Pos.app.inflater.inflate (R.layout.pos_init_button, this)

				button = findViewById (R.id.pos_init) as Button
				
				button.setTextColor (Color.WHITE)
				
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.setMargins (10, 10, 10, 10)
				setLayoutParams (layoutParams)
		  }
	 }
	 
}
