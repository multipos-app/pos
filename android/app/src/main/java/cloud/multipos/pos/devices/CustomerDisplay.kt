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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket

import android.os.Build

abstract class CustomerDisplay (): DeviceManager (), DeviceCallback {
	 
	 var deviceStatus = DeviceStatus.Unknown
	 
	 init {
		  
		  // if (Build.MODEL.startsWith ("Elo")) {
				
		  // 		devices.add (EloCustomerDisplay ())
		  // }
				
		  // devices.add (DefaultCustomerDisplay ())

		  // if (devices.size > 0) {
				
		  // 		val device = devices.removeFirst ()
		  // 		start ()
		  // }
	 }
	 
	 // device
	 
	 override fun deviceStatus (): DeviceStatus { return deviceStatus }
	 override fun deviceClass (): DeviceClass { return DeviceClass.CustomerDisplay }
	 override fun deviceIcon (): Int { return R.string.fa_customer_display }

	 // customer display
	 
	 abstract fun clear ()
	 abstract fun update (ticket: Ticket)
	 abstract fun text (text: String, lineNo: Int)
	 
	 override fun success (device: Device) {

		  Logger.d ("customer display init... ")
		  DeviceManager.customerDisplay = device as CustomerDisplay
	 }
	 
	 override fun fail () { }
	 	 
	 // companion object {

	 // 	  val devices = mutableListOf <CustomerDisplay> ()
		  
	 // 	  fun start () {
				
	 // 			if (Build.MODEL.startsWith ("Elo")) {
					 
	 // 				 devices.add (EloCustomerDisplay ())
	 // 			}
				
	 // 			devices.add (DefaultCustomerDisplay ())

	 // 			if (devices.size > 0) {

	 // 				 val device = devices.removeFirst ()
	 // 				 device.start ()
	 // 			}
	 // 	  }
	 // }
}
