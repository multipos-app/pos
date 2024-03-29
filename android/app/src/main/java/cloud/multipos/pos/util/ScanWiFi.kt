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
 
package cloud.multipos.pos.util

import cloud.multipos.pos.Pos

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver 
import android.net.wifi.WifiManager

class ScanWifi () {

	 val wifiManager = Pos.app.activity.getSystemService (Context.WIFI_SERVICE) as WifiManager

	 init {

		  Logger.d ("wifi start... ")
		  val wifiScanReceiver = object : BroadcastReceiver () {

				override fun onReceive (context: Context, intent: Intent) {
					 
					 val success = intent.getBooleanExtra (WifiManager.EXTRA_RESULTS_UPDATED, false)

					 Logger.d ("wifi success... " + success)
					 
					 if (success) {
						  
						  scanSuccess ()
					 }
					 else {
						  
						  scanFailure ()
					 }
				}
		  }

		  val intentFilter = IntentFilter ()
		  
		  intentFilter.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
		  Pos.app.activity.registerReceiver (wifiScanReceiver, intentFilter)

		  val success = wifiManager.startScan ()
		  if (!success) {
				
				scanFailure ()
		  }
	 }

	 private fun scanSuccess () {
		  
		  val results = wifiManager.scanResults
		  for (result in results) {
				
				Logger.d ("wifi ... " + result)
		  }
	 }

	 private fun scanFailure () {
		  
		  val results = wifiManager.scanResults
		  Logger.d ("wifi fail results... " + results)
	 }

}
