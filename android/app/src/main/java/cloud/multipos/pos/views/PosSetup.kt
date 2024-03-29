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

class PosSetup (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {

	 var bu: Int = -1
	 var config: Int = -1
	 val locations: MutableList <SetupButton> = mutableListOf ()
	 val configs: MutableList <SetupButton> = mutableListOf ()
	 
	 lateinit var register: TextView
	 
	 init {

		  // if ((Pos.app.businessUnits.size == 1) && (Pos.app.configs.size == 1)) {

		  // 		Logger.d ("skip setup...")
				
		  // 		Pos.app.config
		  // 			 .put ("business_unit", Pos.app.businessUnits.get (0))
		  // 			 .put ("pos_config_id", Pos.app.configs.get (0).getInt ("pos_config_id"))
				
		  // 		Pos.app.config.update ()  // save the config
		  // 		Pos.app.registered ()
		  // }
		  // else {
				
				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
				Pos.app.inflater.inflate (R.layout.pos_setup_layout, this)

				register = findViewById (R.id.pos_setup_register) as Button
				register.setVisibility (View.INVISIBLE);

				register.setOnClickListener {
					 
					 if ((bu >= 0) && (config >= 0)) {
						  
						  Pos.app.config
								.put ("business_unit", Pos.app.businessUnits.get (bu))
								.put ("pos_config_id", Pos.app.configs.get (config).getInt ("pos_config_id"))
					 	  
						  Pos.app.config.update ()  // save the config
						  Pos.app.registered ()
					 }
				}
				
				register.setTextColor (Color.WHITE)
				
				var locatinGrid = findViewById (R.id.pos_setup_locations) as GridLayout
				locatinGrid.setPadding (15, 15, 0, 0)
				var pos = 0
				for (bu in Pos.app.businessUnits) {

					 locatinGrid.addView (LocationButton (context, bu, pos ++))
				}
				
				var configGrid = findViewById (R.id.pos_setup_configs) as GridLayout
				configGrid.setPadding (15, 15, 0, 0)
				pos = 0
				for (c in Pos.app.configs) {

					 configGrid.addView (ConfigButton (context, c, pos ++))
				}
		  // }
	 }

	 inner class LocationButton (context: Context, p: Jar, pos: Int): SetupButton (context) {

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
	 
	 inner class ConfigButton (context: Context, p: Jar, pos: Int): SetupButton (context) {

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

	 inner open class SetupButton (context: Context): LinearLayout (context) {

		  var button: Button

		  init {

				Pos.app.inflater.inflate (R.layout.pos_setup_item, this)

				button = findViewById (R.id.pos_setup) as Button
				
				button.setTextColor (Color.WHITE)
				
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.setMargins (10, 10, 10, 10)
				setLayoutParams (layoutParams)
		  }
	 }
	 
}
