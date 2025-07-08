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
import cloud.multipos.pos.receipts.*

import android.os.Handler
import android.os.Message
import com.starmicronics.stario.StarIOPort
import com.starmicronics.starioextension.StarIoExtManager
import com.starmicronics.stario.StarPrinterStatus
import com.starmicronics.stario.StarIOPortException
import com.starmicronics.starioextension.IConnectionCallback
import com.starmicronics.starioextension.StarIoExtManagerListener
import com.starmicronics.starioextension.ICommandBuilder
import com.starmicronics.stario.PortInfo
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import android.os.Looper;

open class StarPrinter () : Printer () {
	 
	 lateinit var commandBuilder: StarCommandBuilder
	 
    var starIOPort: StarIOPort? = null
    var starPrinter: StarPrinter? = null

	 private val queueHandler = QueueHandler ()
	 private val jobs = mutableListOf <List <ICommandBuilder>> ()
	 
	 override fun deviceName (): String  { return "Star printer" }
	 
	 override fun start (jar: Jar) {

		  search ()
	 }

	 fun search () {
		  
		  Thread (Runnable {
						  
						  Looper.prepare ()
						  val interfaces = arrayOf ("USB:", "BT:", "TCP:")
						  
						  for (i in interfaces.indices) {
								
								if ((deviceStatus == DeviceStatus.OffLine) || (deviceStatus == DeviceStatus.Unknown)) {
									 
									 val interfaceName = interfaces [i]
									 
									 try {
										  
										  val ports = StarIOPort.searchPrinter (interfaceName, Pos.app)
										  val  manager: StarIoExtManager? = null
										  
										  if (ports.size > 0) {
												
												for (j in ports.indices) {
													 
													 val p = ports.get (j)
													 Logger.i ("star found... " + interfaceName + " " + p.portName + " " + p.modelName)
													 connect (Jar ()
																	  .put ("port", p.portName)
																	  .put ("builder_class", printerClass (p)))
												}
										  }
										  else {
												
												Logger.w ("star connect search fail... " + interfaceName)
										  }
									 }
									 catch (e: Exception) {
										  
										  Logger.i ("start search exception... " + interfaceName + " " + e)
									 }
								}
						  }

						  if (starIOPort == null) {

								fail ()
						  }
						  
		  }).start ()
	 }

    fun connect (jar: Jar) {
		  
        val port: String = jar.getString ("port")
        val printerClass: String? = null
        try {
				
            starIOPort = StarIOPort.getPort (port, "", 500, Pos.app)
            if (starIOPort != null) {

					 commandBuilder = LoadClass.get (jar.getString ("builder_class")) as StarCommandBuilder
					 
					 val manager = StarIoExtManager (StarIoExtManager.Type.Standard,
																port,
																null,
																500,
																Pos.app
					 )
					 
					 width = commandBuilder.width ()
					 boldWidth = commandBuilder.boldWidth ()
					 quantityWidth = commandBuilder.quantityWidth ()
					 descWidth = commandBuilder.descWidth ()
					 amountWidth = commandBuilder.amountWidth ()
					 
					 manager.connect (ConnectCallback (manager))
					 deviceStatus = DeviceStatus.OnLine
					 success (this)
            }
        }
		  catch (e: Exception) {
				
            Logger.w ("star connect error... " + e)
            deviceStatus = DeviceStatus.Error
            return
        }
		  
        Pos.app.local.put (
				
            "receipt_printer",
            Jar ()
                .put ("printer_name", "star_printer")
                .put ("builder_class", jar.getString ("builder_class"))
                .put ("port", jar.getString ("port"))
                .toString ()
        )
		  
        Logger.i ("star connect complete... ")

		  val printCommands = PrintCommands ()
		  printCommands
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
				.add (PrintCommand.getInstance ().directive (PrintCommand.CENTER_TEXT).text (Pos.app.getString ("printer_on_line")))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
			  	.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
		  		.add (PrintCommand.getInstance ().directive (PrintCommand.LEFT_TEXT).text ("\n"))
				.add (PrintCommand.getInstance ().directive (PrintCommand.CUT))

		  DeviceManager.printer.queue (printCommands)
    }
	 
	 override fun drawer () {

		  	 val printCommands = PrintCommands ()
			 printCommands
				  .add (PrintCommand.getInstance ().directive (PrintCommand.OPEN_DRAWER))
			 
			 queue (printCommands)

	 }

	 override fun print (title: String, report: String, ticket: Ticket) { }
	 override fun print (ticket: Ticket, type: Int) { }
	 override fun qrcode (): Boolean { return true }
	 override fun barcode (): Boolean { return true }

    override fun queue (commands: PrintCommands) {
		  				
 		  if (this::commandBuilder.isInitialized) {
				
				jobs.add (commandBuilder.build (commands))
				dequeue ()
		  }
    }

    private fun dequeue () {
		  
        if (jobs.size > 0) {
				
				printCommands (jobs.removeAt (0))
        }
    }

    private fun printing (value: Boolean) {
    }

    fun printCommands (builders: List <ICommandBuilder>) {
		  		  
        if (starIOPort == null) {
				
            Logger.i ("star thread, void port...")
            return
        }
		  		  
        try {
				
            Thread (label@ Runnable {
								
								Logger.i ("star print start... " + builders!!.size)
								printing (true)
								var i = 0
								
								while (i < builders!!.size) {
									 
									 val command = builders!! [i].commands
									 try {
										  
										  val status = starIOPort!!.retreiveStatus ()
										  if (status.rawLength == 0) {
												
												Logger.i ("printer status error... ")
										  }
										  
										  starIOPort!!.writePort (command, 0, command.size)
										  
										  if (status.coverOpen) {
												
												Logger.w (Logger.HARDWARE, Pos.app.getString ("printer_cover_open"))
										  }
										  
										  else if (status.receiptPaperEmpty) {
												
												Logger.w (Logger.HARDWARE, Pos.app.getString ("paper_empty"))
										  }
										  
										  else if (status.offline) {
												
												Logger.w (Logger.HARDWARE, Pos.app.getString ("printer_off_line"))
										  }
									 }
									 catch (e: StarIOPortException) {
										  
										  deviceStatus = DeviceStatus.OffLine
										  Logger.w ("star print  exception... $e")
									 }
									 i++
								}
								
								Logger.i ("star print complete... " + builders!!.size)
								printing (false)
								queueHandler.sendMessage (Message.obtain ())
								
            }).start ()
        }
		  finally { }
    }
	 
    fun close () {
		  
        if (starIOPort == null) {
				
            Logger.i ("star close, void port...")
            return
        }
        try {
				
            StarIOPort.releasePort (starIOPort)
            Logger.i ("star close...")
        }
		  catch (e: StarIOPortException) {
				
            deviceStatus = DeviceStatus.OffLine
            Logger.w ("error releasing port... $e")
        }
    }

    private inner class QueueHandler : Handler (Looper.getMainLooper ()) {
		  
        override fun handleMessage (m: Message) {
				
            Logger.i ("star queue handler... $m")
            dequeue ()
        }
    }

    private inner class ConnectCallback (val manager: StarIoExtManager) : IConnectionCallback {
		  
        override fun onConnected (result: IConnectionCallback.ConnectResult) {
				
            Logger.i ("star on connected callback...")
            manager!!.setListener (listener)
        }

        override fun onDisconnected () {
				
            Logger.i ("star disconnect callback...")
        }

    }

    protected val listener: StarIoExtManagerListener = object : StarIoExtManagerListener () {
		  
        override fun onPrinterImpossible () {
				
            Logger.i (deviceName () + ": " + Pos.app.getString ("printer_fail"))
        }

        override fun onPrinterOnline () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("printer_on_line"))
        }

        override fun onPrinterOffline () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("printer_off_line"))
        }

        override fun onPrinterPaperReady () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("paper_ready"))
        }

        override fun onPrinterPaperNearEmpty () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("paper_low"))
        }

        override fun onPrinterPaperEmpty () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("paper_empty"))
        }

        override fun onPrinterCoverOpen () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("printer_cover_open"))
        }

        override fun onPrinterCoverClose () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("printer_cover_close"))
        }

        override fun onAccessoryConnectSuccess () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("cash_drawer_connect"))
        }

		  
        override fun onAccessoryConnectFailure () {
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("cash_drawer_fail"))
        }

        override fun onAccessoryDisconnect () {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("cash_drawer_disconnect"))
        }

        override fun onStatusUpdate (status: String) {
				
            Logger.i ("star listener " + deviceName () + ": " + Pos.app.getString ("cash_drawer_disconnect"))
        }
    }

    fun printerClass (portInfo: PortInfo): String? {
		  
        var printerID: String? = null

		  
        if (portInfo.modelName.length > 0) {
            printerID = portInfo.modelName
        }
		  else if (portInfo.portName.contains ("TSP100")) {
            printerID = portInfo.portName
        }
		  
        if (printerID!!.startsWith ("MCP")) {
				
            return "cloud.multipos.pos.devices.StarMCP2"
        }
		  else if (printerID.indexOf ("TSP143") > 0 || printerID.indexOf ("TSP100") > 0) {

            return "cloud.multipos.pos.devices.StarTSP100"
        }
		  
        return null
    }
}
