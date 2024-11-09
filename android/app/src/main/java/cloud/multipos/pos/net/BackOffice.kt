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

import cloud.multipos.pos.Pos
import cloud.multipos.pos.R
import cloud.multipos.pos.PosConst
import cloud.multipos.pos.db.DbResult
import cloud.multipos.pos.views.PosMenus
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.Ticket

import android.os.Looper
import android.os.Handler
import android.os.Message

object BackOffice: Device, DeviceCallback {

	 var instance = this
	 lateinit var handler: Handler
	 
	 var progress: Handler? = null  // updates progress on a gui
	 var downloadTotal = 0
	 var downloadCount = 0
	 var poll = 120000L
	 var deviceStatus = DeviceStatus.Unknown
	 var mqttClient: MqttClient
	 var started = false;
	 val DOWNLOAD = 1
	 
	 override fun deviceStatus (): DeviceStatus { return deviceStatus }
	 override fun deviceClass (): DeviceClass  { return DeviceClass.BackOffice }
	 override fun deviceName (): String { return "BackOffice"  }
	 override fun deviceIcon (): Int { return R.string.fa_www }
	 override fun success (device: Device) { }
	 override fun fail () { }
	 
	 init {

		  instance = this
		  mqttClient = MqttClient ()
		  Pos.app.devices.add (this)

		  if (Pos.app.config.ready () && Pos.app.config.has ("dbname")) {
				
				// subscribe to BU broker
				
				mqttClient.connect ("multipos/" + Pos.app.local.getInt ("merchant_id", 0))
		  }

		  Pos.app.db ().exec ("delete from pos_updates")
	 }
	 
    fun start () {

		  if (started) {

				return
		  }
		  else {

				started = true
		  }
		  
		  handler = object: Handler () {

				override fun handleMessage (message: Message) {
					 
					 val download = Jar ()
						  .put ("update_id", Pos.app.local.getInt ("update_id", 0))
						  .put ("pos_config_id", Pos.app.local.getInt ("pos_config_id", 0))
						  .put ("download_count", downloadCount)
					 
					 Logger.d ("download post... ${downloadTotal} ${download.getInt ("update_id")} ${download.getInt ("download_count")}")
					 
					 Post ("pos-download")
						  .add (download)
						  .exec (fun (result: Jar): Unit {
										 
										 if (result.getInt ("status") == 0) {
										 	  
											  if (downloadTotal == 0) {

													downloadTotal = result.getInt ("total")
											  }
											  										  
											  if (result.getInt ("count") > 0) {
													
													for (update in result.getList ("updates")) {
														 
														 var res = process (update)  // handle the updtes
													}
													
													downloadCount += result.getInt ("count")
																										
													if (progress != null) {

														 // update the progress gui
																					 
														 val m = Message.obtain ()
														 m.obj = ProgressUpdate (downloadCount, downloadTotal, result.getInt ("count"));
														 progress?.sendMessage (m);
													}
													
													handler.sendMessage (Message.obtain ())  // send message to self
											  }
											  else {

													if (progress != null) {

														 // kill the progress gui
																					 
														 val m = Message.obtain ()
														 m.obj = ProgressUpdate (0, 0, 0);
														 progress?.sendMessage (m);
														 progress = null;
													}
													
													handler.sendMessageDelayed (Message.obtain (), poll)
													upload ()
											  }
										 }
										 else {  // error handler, wait and try again...
													
													Logger.w ("error... " + result.getInt ("status") + " " + result.getString ("status_text"))
													handler.sendMessageDelayed (Message.obtain (), 30000L)
										 }
										 
						  })
				}
		  }
	 }
	 
    fun start (handler: Handler) {

		  progress = handler;
		  start ();
	 }
	 
	 override fun start (jar: Jar) {
		  
		  download ();
	 }

	 private fun process (update: Jar): Boolean {
		  
		  if (update.getString ("update_table").equals ("pos_messages")) {
				
				message (update.get ("update").get ("message"))
		  }
		  else {

				if (!Pos.app.db.hasTable (update.getString ("update_table"))) {

					 Logger.w ("handler updates, missing table... " + update.getString ("update_table"))
					 return true
				}
		  }

		  Pos.app.local.put ("update_id", update.getInt ("id"))
		  
		  when (update.getInt ("update_action")) {

				0 -> {
					 
					 val row = Jar (update.get ("update").toString ())

					 if (Pos.app.db ().has (update.getString ("update_table"))) {

						  Pos.app.db ().delete (update.getString ("update_table"), update.getInt ("update_id"))

						  if (update.getString ("update_table").equals ("items")) {
								
								Pos.app.db ().exec ("delete from item_prices where item_id = " + update.getInt ("update_id"))
								Pos.app.db ().exec ("delete from item_links where item_id = " + update.getInt ("update_id"))
						  }
					 }
					 
					 when (update.getString ("update_table")) {

						  "pos_configs" -> {
								
								Pos.app.db ().exec ("delete from pos_configs")
								
								var config = Config ()
								var json = row.get ("config").toString ()

								Logger.d ("pos config update... ${update.getInt ("id")}")
								
								config.parse (json)
								config
									 .put ("dbname", Pos.app.dbname ())
									 .put ("business_unit", Pos.app.config.get ("business_unit"))
									 .put ("pos_unit", Pos.app.config.get ("pos_unit"))
									 .put ("pos_config_id", Pos.app.posConfigID ())

								Pos.app.config = config
								row.put ("config", config.toString ())
								Pos.app.db ().insert ("pos_configs", row)
								
								config.initialize ()
								Pos.app.config.put ("config_loaded", true)

								PosMenus.reload ()
								
								// Pos.app.updateMenus ()

								// may be a problem with re-loading the config in the background...

								// Logger.x ("config before.... " + Pos.app.config.get ("pos_menus").get ("main_menu"))
								
								// Pos.app.config.parse (row.get ("config").toString ())
								// Pos.app.config.initialize ()
								// Pos.app.config
								// 	 .put ("dbname", Pos.app.dbname ())
								// 	 .put ("business_unit", Pos.app.config.get ("business_unit"))
								// 	 .put ("pos_unit", Pos.app.config.get ("pos_unit"))
								// 	 .put ("pos_config_id", Pos.app.posConfigID ())

								// Pos.app.db ().exec ("delete from pos_configs")
								// Pos.app.db ().insert ("pos_configs", Pos.app.config)

								// // PosMenus.reload ()  // may be some menu changes
								
								// Logger.x ("config after...." + Pos.app.config.get ("posMenus").get ("main_menu"))
						  }

						  "local_item" -> {

								val itemResult = DbResult ("select id from items where sku = '" + row.getString ("sku") + "'", Pos.app.db ())
								if (itemResult.fetchRow ()) {

									 val item = itemResult.row ()
								}
								
								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }
						  
						  "items" -> {
								
								Pos.app.db ().insert ( "items", row)
								
								val itemPrices = update.get ("update").getList ("item_prices")
								for (ip in itemPrices) {
									 
									 ip.put ("pricing", ip.get ("pricing").toString ())  // stringify pricing before saving									 
									 Pos.app.db ().insert ("item_prices", ip)
								}
								
								val itemLinks = update.get ("update").getList ("item_links")
								for (il in itemLinks) {
									 
									 Pos.app.db ().insert ("item_links", il)
								}
						  }
						  
						  "item_addons" -> {

								row.put ("jar", row.get ("jar").toString ())
								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }

						  
						  "business_units" -> {
								
								if (update.getInt ("update_id") == Pos.app.buID ()) {
									 
									 // Logger.x ("bu update... " + update.get ("update"))

									 Pos.app.config.put ("business_unit", update.get ("update"))
									 Pos.app.config.update ()
								}
						  }
						  
						  "profiles" -> {

								Pos.app.db ().exec ("delete from profile_permissions where profile_id = " + update.getInt ("update_id"))
								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }
						  
						  "profile_permissions" -> {

								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }
						  
						  else -> {
								
								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }

					 }
				}
				
				1 -> {
					 
					 Pos.app.db ().delete (update.getString ("update_table"), update.getInt ("update_id"))
				}
		  }
		  
		  update.remove ("update")

		  return true
	 }
	 
	 private fun message (message: Jar) {
		  
        val m = message.get (message.getString ("method"));

		  when (message.getString ("method")) {
				
				"control" -> {

					 Pos.app.sendMessage (PosConst.CONTROL, m);
				}
				
				"exit" -> {
					 
					 Pos.app.stop ();
				}
				
				"message" -> {
					 
					 Pos.app.sendMessage (PosConst.MESSAGE, m);
				}
								
				"sql" -> {

					 Logger.x ("sql... " + message.getString ("sql"));
					 Pos.app.db.exec (message.getString ("sql"));
				}
		  }
	 }

	 fun download () {
		  
		  handler.sendMessage (Message.obtain ())
	 }

	 fun upload () {
		  
		  var updateResult = DbResult ("select * from pos_updates", Pos.app.db ())
		  while (updateResult.fetchRow ()) {
				
				val update = updateResult.row ()
				
				var ticket = Ticket (update.getInt ("update_id"), Ticket.COMPLETE)
				Upload ()
					 .add (ticket)
					 .exec ()
		  }
	 }
}
