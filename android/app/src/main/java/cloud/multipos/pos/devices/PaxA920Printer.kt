package cloud.multipos.pos.devices

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.Views
import cloud.multipos.pos.receipts.*
import cloud.multipos.pos.models.*

import com.pax.poslink.broadpos.BroadPOSReceiverHelper
import com.pax.poslink.broadpos.ReceiverResult
import com.pax.poslink.peripheries.DeviceModel
import com.pax.poslink.CommSetting
import com.pax.poslink.PosLink
import com.pax.poslink.POSLinkAndroid
import com.pax.poslink.poslink.POSLinkCreator
import com.pax.poslink.PaymentRequest
import com.pax.poslink.ProcessTransResult
import com.pax.poslink.proxy.POSListenerLauncher
import com.pax.poslink.peripheries.POSLinkPrinter
import com.pax.poslink.peripheries.POSLinkPrinter.PrintListener

import android.os.Handler
import android.os.Message
import android.os.Build

class PaxA920Printer (val pl: PrintListener): Printer () {

    lateinit var printer: POSLinkPrinter

	 init {
			 
		  printer = POSLinkPrinter.getInstance (Pos.app.activity ())
		  printer.setTypeFace (Views.receiptFont ())
		  deviceStatus = DeviceStatus.Unknown

		  Logger.d ("pax a920 printer... ")

		  // listener = POSLinkPrinter.PrintListener {
				
		  // 		override fun onSuccess () {
					 
		  // 			 Logger.d ("print success...")
		  // 		}
				
		  // 		override fun oneError (processResult: ProcessResult) {
					 
		  // 			 Logger.d ("print error..." + processResult)
		  // 		}
		  // }

		  // printer.print ("0123456789012345678901234567890123456789\n\n\n\n\n\n", null, false)
	 }
	 
	 override fun deviceClass (): DeviceClass { return DeviceClass.Printer }
	 
	 fun search (): Boolean {

		  Logger.d ("printer search a920... " + printer)

		  if (printer != null) {
				
				deviceStatus = DeviceStatus.OnLine
				// pl.onSuccess (this);

				Pos.app.local.put ("receipt_printer",
										  Jar ()
												.put ("printer_name", "pax_a920")
												.toString ());
		  
				return true
		  }
		  else {

				deviceStatus = DeviceStatus.OffLine
				return false
		  }
	 }
	 
	 override fun queue (commands: PrintCommands) {
		  
		  for (pc in commands.list) {

				Logger.d ("queue... " + pc.text)
				when (pc.directive) {
					 
					 PrintCommand.CENTER_TEXT -> {
					 
						  //printer.setAlignment (Alignment.CENTER);
						  printer.print (Strings.center (pc.text, width (), " "), null, false);
						  // reset ();
					 }
					 
					 PrintCommand.LEFT_TEXT -> {
						  
						  //printer.setAlignment (Alignment.LEFT);
						  printer.print (pc.text.trim (), null, false);
						  //printer.setFontSize (0, 0);
					 }
					 
					 PrintCommand.RIGHT_TEXT -> {
						  
						  //printer.setAlignment (Alignment.RIGHT);
						  printer.print ((pc.text), null, false);
						  // reset ();
					 }
					 
					 PrintCommand.SMALL_TEXT, PrintCommand.NORMAL_TEXT -> {
						  
						  //printer.setFontSize (0, 0);
					 }
					 
					 PrintCommand.BIG_TEXT -> {
						  
						  //printer.setFontSize (1, 1);
					 }
					 
					 PrintCommand.BOLD_TEXT -> {
						  
						  //printer.setBold (true);
					 }
					 
					 PrintCommand.INVERSE_TEXT -> {
					 }
					 
					 PrintCommand.QR_CODE -> {
					 }
					 
					 PrintCommand.BARCODE -> {
						  
						  //printer.printBarcode (pc.text, false, BARCODE_WIDTH, BARCODE_HEIGHT);
					 }
					 
					 PrintCommand.FEED -> {
						  
						  //printer.setFontSize (0, 0);
						  for (i in 0..pc.value) {
								
								printer.print ("\n", null, false);
						  }
					 }
					 
					 PrintCommand.OPEN_DRAWER -> {
						  
					 }
					 
					 PrintCommand.CUT -> {
						  
						  printer.print ("\n\n\n\n", null, false);
					 }
				}
		  }
	 }

	 override fun width (): Int { return 32 }
	 override fun boldWidth (): Int { return 16 }
	 override fun quantityWidth (): Int { return 4 }
	 override fun descWidth (): Int { return 18 }
	 override fun amountWidth (): Int { return 10 }
	 override fun deviceName (): String { return "PAX A920 Printer" }
	 override fun print (title: String, report: String, ticket: Ticket) { }
	 override fun print (ticket: Ticket, type: Int) { }
	 override fun drawer () { }
	 override fun qrcode (): Boolean { return true }
	 override fun barcode (): Boolean { return true }

	 fun connected (): Boolean { return true }
}
