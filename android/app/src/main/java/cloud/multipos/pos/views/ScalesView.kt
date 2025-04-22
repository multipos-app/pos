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

import java.util.Random 

class ScalesView (val control: InputListener, title: String, val type: Int, val decimalPlaces: Int): DialogView (title) {

	 var scaleEcho: TextView

	 companion object {
		  
		  lateinit var callback: ScalesCallback
		  var weight = 0.0
	 }
	 
	 init {
		  
		  Pos.app.inflater.inflate (R.layout.scale_layout, dialogLayout)

		  scaleEcho = findViewById (R.id.scale_echo) as PosText

		  callback = object: ScalesCallback {
				
				override fun scaleData (w: Double) {
					 
					 weight = w
					 scaleEcho.setText (String.format ("%.${decimalPlaces}f " + Pos.app.getString ("pounds"), weight))
				}
		  }
		  		  		  
		  if (DeviceManager.scales?.deviceStatus () == DeviceStatus.OnLine) {
				
				DeviceManager.scales?.startCapture (callback)
		  }
		  
		  DialogControl.addView (this)

		  Thread (Runnable {

						  weight = 0.0
						  var count = 0;
						  var complete = (100..200).random ();

						  while (count < complete) {

								count ++
								weight = count.toDouble ()
								
								ScalesView.callback.scaleData (count.toDouble () / 10)
								Thread.sleep (10L)
						  }  
		  }).start ()
		  
	 }
	 
	 override fun actions (dialogView: DialogView) {

		  Logger.d ("scales action... ")
		  
		  val layout = Pos.app.inflater.inflate (R.layout.scales_action_layout, dialogActions)
		  var acceptWeight = layout.findViewById (R.id.scales_accept_weight) as Button
		  acceptWeight?.setOnClickListener {

				Logger.d ("accept weight...")
				
				control.accept (Jar ()
										  .put ("weight", weight.toDouble ()))
				
				DialogControl.close ()
		  }
	 }
}
