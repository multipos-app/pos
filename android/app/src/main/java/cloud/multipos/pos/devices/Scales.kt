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

abstract class Scales (): Device, DeviceCallback {
		  
	 var deviceStatus = DeviceStatus.Unknown

	 init { }

	 abstract fun startCapture (callback: ScalesCallback)
	 abstract fun stopCapture ()
	 
	 // pos device

	 override fun deviceClass (): DeviceClass { return DeviceClass.Scales}
	 override fun deviceStatus (): DeviceStatus  { return deviceStatus }
	 override fun deviceIcon (): Int { return R.string.fa_scale }

	 override fun success (device: Device) {

		  DeviceManager.scales = device as Scales
		  deviceStatus = DeviceStatus.OnLine
	 }
	 
	 override fun fail () {

		  deviceStatus = DeviceStatus.OffLine
	 }
}
