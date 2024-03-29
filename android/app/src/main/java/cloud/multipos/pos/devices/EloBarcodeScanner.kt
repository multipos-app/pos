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

import java.io.IOException

import com.elo.device.DeviceManager
import com.elo.device.ProductInfo
import com.elo.device.enums.*
import com.elo.device.inventory.PrinterSupported
import com.elo.device.exceptions.*
import com.elo.device.inventory.*
import com.elo.device.peripherals.*
import com.elo.device.peripherals.BarCodeReader.BarcodeReadCallback

/**
 *
 */

class EloBarcodeScanner (): Scanner ()  {
	 
	 override fun deviceName (): String  { return "Elo refresh scanner" }
	 
	 val scannerCallback: BarcodeReadCallback = object: BarcodeReadCallback {
		  
																		 override fun onBarcodeRead (data: ByteArray) {

																			  Logger.d ("scanner callback... " + data)
																		 }
	 }

	 init {

		  Logger.d ("elo scanner... ")
		  
		  deviceStatus = DeviceStatus.OnLine
		  success (this)
		  Pos.app.devices.add (this)

		  val inventory = DeviceManager.getInventory (Pos.app)

		  if (inventory.isEloSdkSupported ()) {
		  
				val platform = inventory.getProductInfo ().eloPlatform
		  
				try {
					 
					 val deviceManager = DeviceManager.getInstance (EloPlatform.PAYPOINT_REFRESH, Pos.app.activity)
					 
					 if (inventory.hasPrinter ()) {
						  
						  val reader = deviceManager.getBarCodeReader ()
						  
						  Logger.d ("elo scanner keyboard... " + reader.getStatus ())

						  reader.setEnabled (true)
						  reader.setKbMode ()
						  reader.beep (BeepType.SUCCESS)
						  

						  reader.setBarcodeReadCallback (scannerCallback)
					 }
				}
				catch (ioe: IOException) {
				
					 Logger.w ("elo scanner error... " + ioe)
				}
		  }
	 }

	 override fun input (o: Any) {

	 }
}
