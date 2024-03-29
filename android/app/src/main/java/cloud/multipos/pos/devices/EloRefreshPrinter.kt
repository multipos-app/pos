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
import cloud.multipos.pos.models.*
import cloud.multipos.pos.receipts.*

import java.io.IOException

import com.elo.device.DeviceManager
import com.elo.device.ProductInfo
import com.elo.device.enums.EloPlatform
import com.elo.device.inventory.PrinterSupported
import com.elo.device.exceptions.*
import com.elo.device.inventory.Inventory
import com.elo.device.peripherals.CashDrawer
import com.elo.device.enums.Alignment

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.Paint
import android.graphics.Canvas
import android.text.TextPaint
import android.text.StaticLayout
import android.graphics.Rect
import android.graphics.Color
import android.text.Layout
import android.graphics.BitmapFactory

/**
 * Elo-PP3-13 - If exception check that USB is set to host mode
 */

class EloRefreshPrinter (): Printer () {
	 
    lateinit var printer: com.elo.device.peripherals.Printer
	 lateinit var cashDrawer: CashDrawer

	 init {

		  width = 32
		  boldWidth = 11
		  quantityWidth = 4
		  descWidth = 20
		  amountWidth = 8
	 }
	 
	 override fun qrcode (): Boolean { return true }
	 override fun barcode (): Boolean { return true }
	 override fun deviceName (): String  { return "Elo refresh printer" }
	 
	 override fun start (jar: Jar) {

		  search ()
	 }
	 	 
	 fun search (): Boolean {
		  
		  val inventory = DeviceManager.getInventory (Pos.app)

		  if (!inventory.isEloSdkSupported ()) {
				
				return false
		  }
		  
		  val platform = inventory.getProductInfo ().eloPlatform

		  try {

				val deviceManager = DeviceManager.getInstance (EloPlatform.PAYPOINT_REFRESH, Pos.app.activity)
				
				if (inventory.hasPrinter ()) {
					 
		 			 printer = deviceManager.getPrinter ()
				}
				else {
					 
					 Logger.w ("elo sdk no printer...")
					 return false
				}

				if (inventory.hasCashDrawer ()) {
					 
					 cashDrawer = deviceManager.getCashDrawer ()
				}
				else {
					 
					 Logger.d ("elo sdk no cash drawer...")
					 return false
				}

				if (printer == null) {
					 return false
				}
				
				Logger.d ("start EloRefreshPrinter... ")

				if (printer.hasPaper ()) {
					 
					 Pos.app.sendMessage (PosConst.PRINTER_ON_LINE)
				}
				else {
					 
					 Pos.app.sendMessage (PosConst.PRINTER_OFF_LINE)
				}
		  }
		  catch (ioe: IOException) {
				
				Logger.w ("printer error... " + ioe)
				Pos.app.sendMessage (PosConst.PRINTER_OFF_LINE)
				fail ()
				return false
		  }
		  
		  success (this)
		  
		  Pos.app.local.put ("receipt_printer",
									Jar ()
										 .put ("printer_name", "refresh_printer")
										 .toString ())
		  
		  // printer.print ("1234567890123456789012345678901234567890")

		  return true
	 }

	 override fun print (title: String, report: String, ticket: Ticket) { }
	 override fun print (ticket: Ticket, type: Int) { }
	 override fun drawer () {
		  
		  if (cashDrawer != null) {
				
				cashDrawer.open ()
		  }
	 }
	 
	 override fun queue (printCommands: PrintCommands) {

		  for (pc in printCommands.list) {
								
				when (pc.directive) {
					 
					 PrintCommand.CENTER_TEXT -> {
						  
						  printer.setAlignment (Alignment.CENTER)
						  printer.print (pc.text)
						  reset ()
					 }
					 
					 PrintCommand.LEFT_TEXT -> {
						  
						  printer.setAlignment (Alignment.LEFT)
						  printer.print (pc.text.trim ())
						  reset ()
					 }

					 PrintCommand.RIGHT_TEXT -> {

						  printer.setAlignment (Alignment.RIGHT)
						  printer.print ((pc.text))
						  reset ()
					 }
					 
					 PrintCommand.SMALL_TEXT , PrintCommand.NORMAL_TEXT -> {

						  printer.setFontSize (0, 0)
						  printer.setFontSize (FONT_SM, FONT_SM)
					 }
					 
					 PrintCommand.BIG_TEXT -> {
						  
						  printer.setFontSize (0, 0)
						  printer.setFontSize (FONT_MED, FONT_MED)
					 }
					 
					 PrintCommand.BOLD_TEXT -> {
						  
						  printer.setBold (true)
					 }
					 
					 PrintCommand.INVERSE_TEXT -> {
					 }
					 
					 PrintCommand.QR_CODE -> {
						  
						  Logger.d ("print qrcode... " + pc.text);
						  
						  // val bmp = qrcode (pc.text, QRCODE_WIDTH, QRCODE_HEIGHT)
						  // if (bmp != null) {
								
						  // 		printer.printImage (bmp, 100, 0, QRCODE_WIDTH, QRCODE_HEIGHT)
						  // }
								
						  printer.printBarcode (pc.text, true, BARCODE_WIDTH, BARCODE_HEIGHT)
					 }
					 
					 PrintCommand.BARCODE -> {

						  printer.setAlignment (Alignment.CENTER)
						  printer.printBarcode (pc.text, true, BARCODE_WIDTH, BARCODE_HEIGHT)
					 }
					 
					 PrintCommand.LINE -> {
						  						  		
						  printer.setAlignment (Alignment.CENTER)
						  printer.print (line ())
						  reset ()
					 }
					 
					 PrintCommand.FEED -> {
						  						  
						  printer.feed (pc.value)
						  // for (i in 0..pc.value) {
								
						  // 		printer.print ("\n")
						  // }
					 }
					 
					 PrintCommand.OPEN_DRAWER -> {

						  Logger.d ("cash drawer open... " + cashDrawer)
						  
						  if (cashDrawer != null) {
								
								cashDrawer.open ()
						  }
					 }
					 
					 PrintCommand.CUT -> {
						  
					 }
				}
		  }

		  try {
				
				if (!printer.hasPaper ()) {
					 
					 Pos.app.sendMessage (PosConst.PRINTER_PAPER_OUT)
				}
				else if (printer.hasPaper ()) {
					 
					 Pos.app.sendMessage (PosConst.PRINTER_ON_LINE)
				}
		  }
		  catch (ioe: IOException) {

				Logger.w ("printer error... " + ioe)
				Pos.app.sendMessage (PosConst.PRINTER_OFF_LINE)
		  }
	 }

	 private fun reset () {
		  
		  printer.setFontSize (0, 0)
		  printer.setFontSize (FONT_MED, FONT_MED)
		  printer.setBold (false)
	 }

	 private fun center (bmp: Bitmap): Bitmap {
		  
        
        var bmp = Bitmap.createBitmap (QRCODE_WIDTH, QRCODE_HEIGHT, Bitmap.Config.RGB_565)
                
        // for (x in 0..(QRCODE_WIDTH - 1)) {
            
        //     for (y in 0..(QRCODE_HEIGHT - 1)) {

		  // 			 bmp.setPixel (x, y, Color.WHITE)
        //     }
        // }
        
        return bmp
	 }
	 
	 private fun line (): String {

		  val sb = StringBuffer ()
        val bar = ""

		  for (i in 1..width ()) {

				sb.append (bar)
		  }

		  return sb.toString ()
	 }

	 companion object {

		  val FONT_SM = 14
		  val FONT_MED = 14
		  val FONT_LG = 14
		  val BARCODE_WIDTH = 2
		  val BARCODE_HEIGHT = 50
		  val QRCODE_WIDTH = 200
		  val QRCODE_HEIGHT = 200
		  val PIXEL_WIDTH = 576
		  val QRCODE_X = 200
		  val QRCODE_Y = 0	 
		  val PAPER_SIZE_THREE_INCH = 576
	 }
}
