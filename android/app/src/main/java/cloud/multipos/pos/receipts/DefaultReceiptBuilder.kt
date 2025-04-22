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
 
package cloud.multipos.pos.receipts

import cloud.multipos.pos.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.devices.DeviceManager

import java.util.*
import java.text.SimpleDateFormat
import android.os.Build

open class DefaultReceiptBuilder (): ReceiptBuilder () {

	 val bu = Pos.app.config.get ("business_unit") as Jar
	 val params = Pos.app.config.get ("business_unit").get ("params") as Jar
	 val receiptUtils = ReceiptUtils (Pos.app.activity)
	 var sep = "*" // "─"
	 val dateTimeFormat = "EEE d MMM yyyy HH:mm"
	 // val printer = Pos.app.receiptPrinter
	 val tl = "┌"
	 val tr = "┐"
	 val bl = "└"
	 val br = "┘"
	 val vb = "│"
	 val hb = "─"
	 
	 var inBox = false

	 lateinit var ticket: Ticket

	 init {
	 }
	 
	 fun ArrayList <Jar>.append (p: Jar): ArrayList <Jar> {

		  this.add (p)
		  return this
	 }
	 
	 fun ArrayList <PrintCommand>.append (p: PrintCommand): ArrayList <PrintCommand> {

		  this.add (p)
		  return this
	 }

	 override fun ticket (t: Ticket, type: Int): ReceiptBuilder {
		  
		  printCommands.clear ()
		  ticket = t
		  
		  header ()
		  customer ()
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.NORMAL_TEXT))
				.add (PrintCommand.getInstance ().directive (PrintCommand.SMALL_TEXT))
		  
		  separator ()

		  when (type) {

				PosConst.PRINTER_RECEIPT -> {

					 items ()
					 summary ()
					 tender ()
				}
				
	 			PosConst.PRINTER_EXCHANGE_RECEIPT -> { }
				PosConst.PRINTER_REPRINT -> { }
	 			PosConst.PRINTER_REPORT -> {

					 session ()
				}
				
	 			PosConst.PRINTER_ORDER -> {

					 orderItems ()
				}

		  }
		  
		  notes ()
		  footer ()
		  
		  return this
	 }
	 
	 override fun header (): ReceiptBuilder {
		  
		  val header = arrayListOf <Jar> ()
		  var ticketType = ""
		  
		  if (params.has ("receipt_header")) {

				add (params.getList ("receipt_header"))
		  }
		  else {
				
				header
					 .append (Jar ()
									  .put ("text", bu.getString ("business_name"))
									  .put ("justify",  "center")
									  .put ("feed", 0)
									  .put ("font", "bold")
									  .put ("size", "big"))

				if (bu.has ("city")) {

					 header
						  .append (Jar ()
											.put ("text", bu.getString ("city") + ", " + bu.getString ("state") + " " + bu.getString ("postal_code"))
											.put ("justify",  "center")
											.put ("feed", 0)
											.put ("font", "normal")
											.put ("size", "normal"))
				}

				if (bu.has ("phone_1")) {

					 header
						  .append (Jar ()
											.put ("text", bu.getString ("phone_1").phone ())
											.put ("justify",  "center")
											.put ("feed", 0)
											.put ("font", "normal")
											.put ("size", "normal"))
				}
			  	
		  }
		  
		  add (header)
		  
		  when (ticket.getInt ("ticket_type")) {
				
				Ticket.SALE_NONTAX -> {
					 
					 ticketType = Pos.app.getString ("non_tax_sale")
				}
				
				Ticket.VOID -> {
					 
					 ticketType = Pos.app.getString ("void_sale")
				}
				
				Ticket.CREDIT_REFUND -> {
					 
					 ticketType = Pos.app.getString ("return_sale")
				}
				
				Ticket.CREDIT_REVERSE -> {
					 
					 ticketType = Pos.app.getString ("credit_reverse")
				}
				
				Ticket.BANK -> {
					 
					 ticketType = Pos.app.getString ("bank")
				}
				
				Ticket.X_SESSION -> {
					 
					 ticketType = Pos.app.getString ("x_report")
				}
				
				Ticket.Z_SESSION -> {
					 
					 ticketType = Pos.app.getString ("z_report")
				}		
		  }
		  
		  if (ticketType.length > 0) {

		  		printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
		  			 .add (PrintCommand.getInstance ().directive (PrintCommand.BIG_TEXT))
		  			 .add (PrintCommand.getInstance ().directive (PrintCommand.BOLD_TEXT))
		  			 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (ticketType))	
		  }

		  return this
	 }

	 override fun items (): ReceiptBuilder {
		  
		  for (ti in ticket.items) {

				when (ti.getInt ("state")) {

					 TicketItem.STANDARD -> {
						  
						  printCommands.addAll (ReceiptItem.factory (ti).printCommands)
					 }
				}
		  }

		  return this
	 }
	 
	 override fun session (): ReceiptBuilder {

		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.NORMAL_TEXT))
				.add (PrintCommand.getInstance ().directive (PrintCommand.SMALL_TEXT))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.LINE))

		  val sessionFormat = "%" + DeviceManager.printer.quantityWidth () + "s %-" + DeviceManager.printer.descWidth () + "s%" + DeviceManager.printer.amountWidth () + "s"

		  for (t in listOf <String> ("totals", "departments",  "cards", "exceptions")) {
				
				if (ticket.has (t) && (ticket.getList (t).size > 0)) {

					 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (t.uppercase ()))
					 	  .add (PrintCommand.getInstance ().directive (PrintCommand.LINE))

					 for (total in ticket.getList (t)) {
						  
						  val quantity: String = if (total.getInt ("quantity") > 0) total.getInt ("quantity").toString () else ""

						  printCommands
								.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (sessionFormat,
																																						quantity,
																																						total.getString ("desc"),
																																						total.getDouble ("amount").currency ())))
						  
					 }
					 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.LINE))
				}
		  }

		  if (ticket.has ("cash_management")) {
				
				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LINE))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (Pos.app.getString ("drawer_counts")))	
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LINE))

				for (count in ticket.get ("cash_management").getList ("counts")) {
					 
					 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (sessionFormat,
																																				  "",
																																				  count.getString ("denom_name"),
																																				  count.getInt ("denom_count"), false)))
				}
				
				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LINE))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (sessionFormat,
																																			 "",
																																			 Pos.app.getString ("cash_sales"),
																																			 ticket.get ("cash_management").getDouble ("cash_sales").currency (), false)))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (sessionFormat,
																																			 "",
																																			 Pos.app.getString ("drawer_count"),
																																			 ticket.get ("cash_management").getDouble ("drawer_count").currency (), false)))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (sessionFormat,
																																			 "",
																																			 Pos.app.getString ("over_short"),
																																			 ticket.get ("cash_management").getDouble ("over_short").currency (), false)))
		  }
		  
		  return this
	 }
	 
	 override fun orderItems (): ReceiptBuilder {
		  
		  val qtyFormat = "%7s %8d " + "_".fill (DeviceManager.printer.width () - 17) + "\n\n"
		  val costFormat = "%7s %8s " + "_".fill (DeviceManager.printer.width () - 17) + "\n"
		  var totalCost = 0.0
		  var totalQuantity = 0
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.NORMAL_TEXT))
				.add (PrintCommand.getInstance ().directive (PrintCommand.SMALL_TEXT))
		  
		  for (item in Pos.app.ticket.getList ("order_items")) {

				totalQuantity += item.getInt ("order_quantity")
				totalCost += item.getDouble ("cost") * item.getInt ("order_quantity")

				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.BARCODE).text (item.getString ("sku")))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (item.getString ("sku")))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (item.getString ("item_desc") + "\n"))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (qtyFormat, "QTY:", item.getInt ("order_quantity"))))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (costFormat, "COST:", item.getDouble ("cost").currency ())))
		  }
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n" + " " + Pos.app.getString ("totals") + " ".center (DeviceManager.printer.width (), "*") + "\n"))
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (qtyFormat, "QTY:", totalQuantity)))
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (costFormat, "COST:", totalCost.currency ())))
		  
		  return this
	 }

	 override fun summary (): ReceiptBuilder {

		  val summaryFormat = "%-" + (DeviceManager.printer.quantityWidth () + DeviceManager.printer.descWidth ()) + "s%" + DeviceManager.printer.amountWidth () + "s"
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (summaryFormat,
																																		Pos.app.getString ("sub_total"),
																																		ticket.getDouble ("sub_total").currency ())))
		  
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (summaryFormat,
																																		Pos.app.getString ("tax"),
																																		ticket.getDouble ("tax_total").currency ())))
		  
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (summaryFormat,
																																		Pos.app.getString ("total"),
																																		ticket.getDouble ("total").currency ())))
		  return this
	 }
	 
	 override fun tender (): ReceiptBuilder {

		  val tenderFormat = "%-" + (DeviceManager.printer.quantityWidth () + DeviceManager.printer.descWidth ()) + "s%" + DeviceManager.printer.amountWidth () + "s"
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
		  
		  for (t in ticket.tenders) {

				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (tenderFormat,
																																			Pos.app.getString (t.getString ("tender_type")),
																																			t.getDouble ("tendered_amount").currency ())))
				
				if (t.getDouble ("returned_amount") != 0.0) {

					 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (tenderFormat,
																																				  Pos.app.getString ("change"),
																																				  t.getDouble ("returned_amount").currency ())))
				}
				
				val dc = t.get ("data_capture")

				if (dc.has ("card_brand")) {

					 for (p in listOf <String> ("card_brand", "card_number", "approval_code", "entry_type")) {

						  if (dc.has (p)) {
								
								printCommands.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (String.format (tenderFormat,
																																										 Pos.app.getString (p),
																																										 dc.getString (p))))
						  }
					 }
				}
		  }
		  																																		
		  return this
	 }

	 override fun notes (): ReceiptBuilder {

		  if (ticket.other.size > 0) {
				
				feed (1)
				for (o in ticket.other) {

		  			 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.ITALIC_TEXT))
		  				  .add (PrintCommand.getInstance ().directive (PrintCommand.BIG_TEXT))
		  				  .add (PrintCommand.getInstance ().directive (PrintCommand.BOLD_TEXT))
		  				  .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("* " + o.getString ("comment")))	
				}
				
				feed (1)
				separator ()
		  }
		  return this
	 }
	 
	 override fun footer (): ReceiptBuilder {

		  var dateFormat = SimpleDateFormat (dateTimeFormat, locale ())
		  
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (receiptUtils.tag (ticket)))
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (dateFormat.format (Date ())))

		  if (ticket.has ("clerk")) {

				var clerk = ticket.get ("clerk") as Employee

				feed (1)
				separator ()
				printCommands.addAll (clerk.receipt ())
				separator ()
				feed (1)
		  }
		  
		  if (DeviceManager.printer.qrcode ()) {

				if (ticket.has ("customer") && (ticket.get ("customer").getString ("uuid").length > 0)) {

					 var loyalty = ticket.get ("customer").getString ("uuid")

					 Logger.d ("print loyalty... ${loyalty}")
					 
					 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
						  .add (PrintCommand.getInstance ().directive (PrintCommand.QR_CODE).text (loyalty))
				}
				else {
					 
					 printCommands
						  .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
						  .add (PrintCommand.getInstance ().directive (PrintCommand.QR_CODE).text (ticket.getString ("recall_key")))
				}
		  }
		  else {
				printCommands
					 .add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
					 .add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (Pos.app.getString ("ticket_no") + " " + ticket.getString ("recall_key")))
		  }
		  
		  if (params.has ("receipt_footer")) {
				
				feed (1)
				add (params.getList ("receipt_footer"))
		  }

		  feed (4)
		  printCommands
				.add (PrintCommand.getInstance ().directive (PrintCommand.CUT))
	  
		  return this
	 }

	 override fun separator (): ReceiptBuilder {
		  
		  printCommands.add (PrintCommand.getInstance ()
										 .directive (PrintCommand.LINE))
		  return this
	 }
	 
	 override fun separator (label: String): ReceiptBuilder {

		  var width = DeviceManager.printer.width () - label.length - 2
		  val sb = StringBuffer ()
		  
		  if (sep.toByteArray () [0] < 0) {

				width -= 9
		  }

		  width /= 2

		  if (width % 2 == 1) {

				width --
		  }
		  
		  for (i in 0..width) {

				sb.append (sep)
		  }

		  sb
				.append (" ")
				.append (label)
				.append (" ")
		  
		  for (i in 0..width) {

				sb.append (sep)
		  }

		  printCommands.add (PrintCommand.getInstance ()
										 .directive (PrintCommand.MEDIUM_TEXT)
										 .directive (PrintCommand.LEFT_TEXT)
										 .text (sb.toString ()))
		  return this
	 }

	 override fun feed (lines: Int): ReceiptBuilder {

		  for (feed in 1..lines) {
				
				printCommands.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
		  }
		  
		  return this
	 }

	 override fun cut (): ReceiptBuilder {
		  
		  return this
	 }

	 override fun print () {

		  Logger.i ("receipt print...")
		  
		  if (printCommands.list.size > 0) {
				
	 			DeviceManager.printer.queue (printCommands)
	 	  }
	 }
	 
	 override fun text (): String {
		  
		  val sb = StringBuffer ()
		  
		  for (pc in printCommands.list) {

				when (pc.directive) {

					 PrintCommand.CENTER_TEXT -> {

						  sb.append (pc.text.center (DeviceManager.printer.width (), " ")).append ("\n")
					 }
					 
					 PrintCommand.LEFT_TEXT -> {

						  sb.append (pc.text).append ("\n")
					 }
					 
					 PrintCommand.LINE -> {

						  sb.append ("—".fill (DeviceManager.printer.width ())).append ("\n")
					 }
				}
		  }
		  
		  return sb.toString ().replace ("'", "`")
	 }

	 /**
	  *
	  *  Local methods
	  *
	  */

	 fun box (toggle: Boolean) {

		  inBox = toggle
		  
		  if (toggle) {

				val sb = StringBuffer ()

				sb
					 .append (tl)

				for (i in 0..(DeviceManager.printer.width () - 9)) {

					 sb.append (hb)
				}

				sb.append (tr)
				
				printCommands.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text (sb.toString ()))
		  }
	 }
}
