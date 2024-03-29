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

// POSLink_Java_Android_V1.08.00_20210622_InstallPackage/doc_android/index.html

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.Views
import cloud.multipos.pos.receipts.*
import cloud.multipos.pos.models.Ticket

import android.graphics.Bitmap

import com.pax.poslink.peripheries.POSLinkPrinter
import com.pax.poslink.peripheries.POSLinkPrinter.*
import com.pax.poslink.print.PrintBarcode
import com.pax.poslink.peripheries.POSLinkPrinter.PrintListener
import com.pax.poslink.peripheries.ProcessResult

class PaxE800Printer (): Printer () {

    lateinit var posLinkPrinter: POSLinkPrinter
    lateinit var formatter: POSLinkPrinter.PrintDataFormatter
	 
    var listener = PaxPrinterListener ()
	 val xxxx = "                                       "
	 val line = "———————————————————————————————————————"

	 init {
		  
		  width = 40
		  boldWidth = 20
		  quantityWidth = 4
		  descWidth = 28
		  amountWidth = 8
	 }

	 override fun qrcode (): Boolean { return true }
	 override fun barcode (): Boolean { return true }
	 override fun deviceName (): String  { return "E800 printer" }
	 
	 override fun start (jar: Jar) {
		  
		  Logger.d ("pax e800 printer start... ")
		  
		  posLinkPrinter = POSLinkPrinter.getInstance (Pos.app.activity ())
		  
		  if (posLinkPrinter != null) {
				
				posLinkPrinter.setTypeFace (Views.receiptFont ())
				formatter = POSLinkPrinter.PrintDataFormatter ()
				
				Logger.d ("pax e800 printer success... ")
								
				success (this)
		  }
		  else {

				Logger.w ("e800 printer fail...")
				fail ()
		  }
	 }
		  
	 override fun print (title: String, report: String, ticket: Ticket) { }
	 override fun print (ticket: Ticket, type: Int) { }
	 override fun queue (commands: PrintCommands) {
		  
		  var qrcode: Bitmap? = null
		  
		  for (pc in commands.list) {
				
				when (pc.directive) {
					 
					 PrintCommand.CENTER_TEXT -> {
					 
						  formatter
								.addCenterAlign ()
								// .addBigFont ()
								// .addInvert ()
								.addContent (pc.text.trim ())
								.addLineSeparator ()
					 }
					 
					 PrintCommand.LEFT_TEXT -> {
						  
						  formatter
								.addLeftAlign ()
								.addNormalFont ()
								.addContent (pc.text.trim ())
								.addLineSeparator ()
					 }
					 
					 PrintCommand.RIGHT_TEXT -> {
						  
					 }
					 
					 PrintCommand.SMALL_TEXT, PrintCommand.NORMAL_TEXT -> {
						  
						  formatter
								.addNormalFont ()
					 }
					 
					 PrintCommand.BIG_TEXT -> {
						  
						  formatter
								// .addInvert ()
								.addBigFont ()
					 }
					 
					 PrintCommand.BOLD_TEXT -> {
					 }
					 
					 PrintCommand.INVERSE_TEXT -> {
					 }
					 
					 PrintCommand.QR_CODE -> {
						  
						  qrcode = qrcode (pc.text.trim (), 320, 320, 10)
					 }
					 
					 PrintCommand.BARCODE -> {
						  
					 }
					 
					 PrintCommand.FEED -> {
					 }
					 
					 PrintCommand.LINE -> {
						  
						  formatter
								.addCenterAlign ()
								// .addInvert ()
								.addContent (line)
								.addLineSeparator ()
					 }
					 
					 PrintCommand.OPEN_DRAWER -> {
						  
					 }
					 
					 PrintCommand.CUT -> {

						  if (qrcode == null) {
								
								posLinkPrinter.print (formatter.build (), POSLinkPrinter.CutMode.FULL_PAPER_CUT, listener, false)
								formatter.clear ()
						  }
						  else {
								
								posLinkPrinter.print (formatter.build (), POSLinkPrinter.CutMode.DO_NOT_CUT, listener, false)
								posLinkPrinter.print (qrcode, listener, 1, false)
								formatter.clear ()
						  }
					 }
				}
		  }
	 }
	 
	 override fun drawer () {}

	 inner class PaxPrinterListener (): PrintListener {

		  override fun onError (err: ProcessResult): Unit {

				Logger.d ("print error... " + err.getCode () + " " + err.getMessage ())
		  }

		  override fun onSuccess (): Unit {

				Logger.d ("print success... ")
		  }
	 }
}
