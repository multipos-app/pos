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
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.receipts.*

import android.os.Build
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat

abstract class Printer (): Device, DeviceCallback {
	 
	 var deviceStatus = DeviceStatus.Unknown
	 var width = 42
	 var boldWidth = 21
	 var quantityWidth = 4
	 var descWidth = 30
	 var amountWidth = 8

	 init {

	 }

	 // device
	 
	 override fun deviceStatus (): DeviceStatus { return deviceStatus }
	 override fun deviceClass (): DeviceClass { return DeviceClass.Printer }
	 override fun deviceIcon (): Int { return R.string.fa_receipt_printer }

	 // pos receipt printer
	 
	 open fun width (): Int { return width }
	 open fun boldWidth (): Int { return boldWidth }
	 open fun quantityWidth (): Int { return quantityWidth }
	 open fun descWidth (): Int { return descWidth }
	 open fun amountWidth (): Int { return amountWidth }
	 
	 abstract fun print (title: String, report: String, ticket: Ticket)
	 abstract fun print (ticket: Ticket, type: Int)
	 abstract fun queue (printCommands: PrintCommands)
	 abstract fun drawer ()
	 abstract fun qrcode (): Boolean
	 abstract fun barcode (): Boolean

	 init {

		  for (device in Pos.app.devices) {

				Logger.x ("device... " + device)
				if (device.deviceClass () == DeviceClass.Printer) {

					 Pos.app.devices.remove (device)
				}
		  }
		  				
		  Pos.app.devices.add (this)
	 }
	 	 
	 // pos device callback
	 
	 override fun success (device: Device) {

		  val printer = device as Printer

		  Logger.d ("printer success... " + printer.width () + " " + printer)
		  
		   width = printer.width ()
			boldWidth = printer.boldWidth ()
			quantityWidth = printer.quantityWidth ()
			descWidth = printer.descWidth ()
			amountWidth = printer.amountWidth

			DeviceManager.printer = printer
			deviceStatus = DeviceStatus.OnLine
	 }
	 
	 override fun fail () {

		  Logger.w ("printer fail...")
		  deviceStatus = DeviceStatus.OffLine
	 }
	 
    fun qrcode (qrcodeText: String, w: Int, h: Int, padding: Int): Bitmap {
                  
        val writer = QRCodeWriter ()       
        val bitMatrix = writer.encode (qrcodeText, BarcodeFormat.QR_CODE, w, h)
        val width = bitMatrix.getWidth ()
        val height = bitMatrix.getHeight ()
        
        var bmp = Bitmap.createBitmap (width + padding + 1, height, Bitmap.Config.RGB_565)
                
        for (x in 0..padding) {
            
            for (y in 0..(height - 1)) {
						  
					 bmp.setPixel (x, y, Color.WHITE)
            }
        }
		  
        for (x in 0..(width - 1)) {
            
            for (y in 0..(height - 1)) {

					 if (bitMatrix.get (x, y)) {
						  
						  bmp.setPixel (x + padding + 1, y, Color.BLACK)
					 }
					 else {
						  
						  bmp.setPixel (x + padding + 1, y, Color.WHITE)
					 }
            }
        }
        
        return bmp
    }
}
