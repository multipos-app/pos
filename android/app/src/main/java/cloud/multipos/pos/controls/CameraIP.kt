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
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.views.NumberInputView

class CameraIP (): Control (), InputListener {

	 var ipBase: String = ""
	 
	 init { }

	 override fun controlAction (jar: Jar) {
		  
	 	  Logger.d ("camera ip control action... " + DeviceManager.payment)
		  
		  NumberInputView (this,
								 Pos.app.getString (R.string.camera_ip),
								 "Enter the last digit of the CAMERA IP address, ${Pos.app.config.getString ("base_ip_address")}.nnn",
								 InputListener.INTEGER,
								 0)
	 }
	 
	 override fun accept (result: Jar) {
		  
	 	  var cameraIP = Pos.app.config.getString ("base_ip_address") + "." + result.getString ("value")
		  Pos.app.local.put ("camera_ip", cameraIP)
		  DeviceManager.payment?.start (Jar ())
	 }
}
