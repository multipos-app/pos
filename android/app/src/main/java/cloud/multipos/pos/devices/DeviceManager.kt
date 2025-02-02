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
 
package cloud.multipos.pos.devices;

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.surveillance.*

import android.os.Build

/**
 *
 * start the devices, each device will attach itself as the start
 *
**/

abstract class DeviceManager (): Device {

	 companion object {

		  val deviceTypes = listOf ("receipt_printer", "kitchen_printer", "scanner", "payment", "scales", "video_capture", "screen_capture")
		  
		  @JvmField var printer: Printer = DefaultPrinter ()
		  @JvmField var customerDisplay: CustomerDisplay? = null
		  @JvmField var scanner: Scanner? = null
		  @JvmField var payment: Payment? = null
		  @JvmField var scales: Scales? = null

		  var started = false
		  
		  fun start (devices: String) {

				if (!started) {

					 started = true
					 				
					 if (Pos.app.config.has (devices)) {

						  val devices = Pos.app.config.get (devices)
						  for (deviceType in deviceTypes) {
								
								var device = devices.get (deviceType);

								Logger.i ("start device... ${deviceType} ${device}")
						  
								if (device.getString ("class").length > 0) {

									 var deviceClass = device.getString ("class")
									 var jar = device.get ("jar") as Jar
									 var dev = LoadClass.get (deviceClass) as Device?
									 
									 if (dev != null) {
										  
										  dev.start (jar)
									 }
								}
						  }
					 }
					 else {
						  
						  Logger.i ("no devices...")
					 }
				}
		  }
	 }
}
