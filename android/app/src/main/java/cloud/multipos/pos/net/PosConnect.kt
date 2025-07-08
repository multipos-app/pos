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
 
package cloud.multipos.pos.net

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.aware.WifiAwareManager
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver

class PosConnect () : Device, DeviceCallback {
	 
	 var deviceStatus = DeviceStatus.Unknown

	 init {
		  
		  val wifiAwareManager = Pos.app.getSystemService (Context.WIFI_AWARE_SERVICE) as WifiAwareManager?
		  val filter = IntentFilter (WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)
		  val receiver = object : BroadcastReceiver () {

				override fun onReceive (context: Context, intent: Intent) {
					 
					 if (wifiAwareManager!!.isAvailable) {

						  Logger.d ("wifi aware is available...")
					 }
					 else {
						  
						  Logger.d ("wifi aware is not available...")
					 }
				}
		  }
		  
		  Pos.app.registerReceiver (receiver, filter)
		  
		  Logger.d ("pos connect... " + Pos.app.packageManager.hasSystemFeature (PackageManager.FEATURE_WIFI_AWARE) + " " + wifiAwareManager)
		  Pos.app.devices.add (this)
	 }
	 
	 override fun deviceStatus (): DeviceStatus { return deviceStatus }
	 override fun deviceClass (): DeviceClass  { return DeviceClass.PosConnect }
	 override fun deviceName (): String { return "POS Connect"  }
	 override fun deviceIcon (): Int { return R.string.fa_pos_connect }
	 
	 override fun success (device: Device) { }
	 override fun fail () { }
}
