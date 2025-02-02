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
 
package cloud.multipos.pos.devices

import java.util.ArrayList
import android.os.Handler

import cloud.multipos.pos.util.*
import cloud.multipos.pos.*

abstract class Payment (): Device, DeviceCallback {
	 
	 var deviceStatus = DeviceStatus.Unknown

	 init { }
	 
	 abstract fun authorize (jar: Jar, h: Handler)
	 
	 open fun cancel (h: Handler) { }

	 // pos device

	 override fun deviceClass (): DeviceClass { return DeviceClass.Payment}
	 override fun deviceStatus (): DeviceStatus  { return deviceStatus }
	 override fun deviceIcon (): Int { return R.string.fa_payment }
	 
	 // pos device callback

	 override fun success (device: Device) {

		  Logger.i ("pos payment success... " + device)
		  DeviceManager.payment = device as Payment
		  // deviceStatus = DeviceStatus.OnLine
	 }

	 override fun fail () {

		  Logger.i ("pos payment fail...")
	 }
}
