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
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.devices.*

import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Random 

class ScalesView (val control: InputListener, title: String, val type: Int, val params: Jar): DialogView (title) {

	 var scaleEcho: PosText
	 var decimalVal = 0.0
	 val decimalPlaces = params.getInt ("decimal_places");
	 
	 companion object {
		  
		  lateinit var callback: ScalesCallback
		  var weight = 0.0
	 }
	 
	 init {
		  
		  Pos.app.inflater.inflate (R.layout.scale_layout, dialogLayout)

		  scaleEcho = findViewById (R.id.scale_echo) as PosText
		  
		  when (Themed.theme) {
				
				Themes.Light -> {
					 
					 scaleEcho?.setTextColor (Color.BLACK)
				}
				
				Themes.Dark -> {
					 
					 scaleEcho?.setTextColor (Color.WHITE)
					 
				}
		  }
		  
		  callback = object: ScalesCallback {
				
				override fun scaleData (w: Double) {

					 if (w < params.getDouble ("min_metric")) {

						  weight = params.getDouble ("min_metric")
					 }
					 else if (params.has ("round")) {

						  when (params.getString ("round")) {

								"half_up" -> {
									 
									 weight = BigDecimal (w.toString ())
										  .setScale (0, RoundingMode.HALF_UP)
										  .toDouble ()
								}
						  }
					 }
					 else {
						  
						  weight = w
					 }
					 
					 scaleEcho?.setText (String.format ("%.${decimalPlaces}f " + Pos.app.getString ("pounds"), weight))
				}
		  }

		  DeviceManager.scales?.startCapture (callback, false)

		  PosDisplays.add (this)
		  DialogControl.addView (this)
		  update ()
		  
		  callback.scaleData (11.99)
	 }
	 
	 override fun actions (dialogView: DialogView) {
		  
		  val layout = Pos.app.inflater.inflate (R.layout.scales_action_layout, dialogActions)
		  var acceptWeight = layout.findViewById (R.id.scales_accept_weight) as Button
		  acceptWeight?.setOnClickListener {

				accept ()
		  }
	 }
	 
	 override fun accept () {
		  
		  if (Pos.app.input.hasInput ()) {
				
				weight = Pos.app.input.getDouble ()
				for (i in 1..decimalPlaces) weight = weight / 10.0
		  }
		  
		  DeviceManager.scales?.stopCapture ()
		  
		  control.accept (Jar ().put ("value", weight))
		  DialogControl.close ()
	 }
	 
	 override fun update () {

		  decimalVal = Pos.app.input.getDouble ()
		  for (i in 1..decimalPlaces) decimalVal = decimalVal / 10.0			 
		  scaleEcho?.text = String.format ("%.${decimalPlaces}f", decimalVal)
	 }
	 
	 override fun enter () {

		  accept ()
	 }
	 
	 override fun clear () {

		  if (Pos.app.input.length () == 0) {
		  
				scaleEcho?.setHint (Pos.app.getString ("register_open"));
		  }		  
	 }
}
