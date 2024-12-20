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

import android.os.Build

abstract class Scanner (): Device, DeviceCallback {
	 
	public var deviceStatus = DeviceStatus.Unknown
	 
	 init { }
	 
	 // pos scanner
	 
	 abstract fun input (obj: Any)

	 // pos device

	 override fun deviceClass (): DeviceClass { return DeviceClass.HandScanner}
	 override fun deviceStatus (): DeviceStatus  { return deviceStatus }
	 override fun deviceIcon (): Int { return R.string.fa_scanner }

	 // pos device callback

	 override fun success (device: Device) {

		  deviceStatus = DeviceStatus.OnLine
		  DeviceManager.scanner = device as Scanner
		  Pos.app.devices.add (device)
	 }

	 override fun fail () {

		  Logger.w ("pos scanner fail...")
		  deviceStatus = DeviceStatus.OnLine
	 }
}

