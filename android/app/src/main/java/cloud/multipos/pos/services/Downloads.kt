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
 
package cloud.multipos.pos.services

import cloud.multipos.pos.Pos
import cloud.multipos.pos.PosConst
import cloud.multipos.pos.db.DbResult
import cloud.multipos.pos.views.PosMenus
import cloud.multipos.pos.util.*

import android.os.Looper
import android.os.Handler
import android.os.Message

object Downloads {

	 var instance = this
	 lateinit var handler: Handler
	 
	 var progress: Handler? = null  // updates progress on a gui
	 var downloadTotal = 0
	 var downloadCount = 0
	 var poll = 60000L
	 
	 var started = false;
	 val DOWNLOAD = 1

	 init {

		  instance = this
	 }
	 
    fun start () {

		  if (started) {

				return
		  }
		  else {

				started = true
		  }

		  Logger.d ("download start... " + Pos.app.local.getInt ("update_id", 0))
		  
		  handler = object: Handler () {

				override fun handleMessage (message: Message) {
					 
					 val download = Jar ()
						  .put ("dbname", Pos.app.dbname ())
						  .put ("business_unit_id", Pos.app.buID ())
						  .put ("pos_no", Pos.app.posNo ())
						  .put ("pos_unit_id", Pos.app.posUnitID ())
						  .put ("pos_config_id", Pos.app.posConfigID ())
						  .put ("update_id", Pos.app.local.getInt ("update_id", 0))
						  .put ("has_config", true)
						  .put ("download_count", downloadCount)
						  .put ("android_id", Pos.app.config.getString ("android_id"))
						  .put ("device_data", Pos.app.config.deviceData ())
					 
					 Cloud ()
						  .add ("download", download)
						  .post (listOf ("pos-download"), fun (result: Jar): Unit {
										 
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
														 m.obj = ProgressUpdate (downloadCount, downloadTotal);
														 progress?.sendMessage (m);
													}
													
													handler.sendMessage (Message.obtain ())  // send message to self
											  }
											  else {

													if (progress != null) {

														 // kill the progress gue
																					 
														 val m = Message.obtain ()
														 m.obj = ProgressUpdate (0, 0);
														 progress?.sendMessage (m);
														 progress = null;
													}
													
													handler.sendMessageDelayed (Message.obtain (), poll)
											  }
										 }
										 else {  // error handler, wait and try again...
													
													Logger.w ("error... " + result.getInt ("status") + " " + result.getString ("status_text"))
													handler.sendMessageDelayed (Message.obtain (), 3000L)
										 }
										 
						  })
				}
		  }

		  handler.sendMessageDelayed (Message.obtain (), poll)
	 }
	 
    fun start (handler: Handler) {

		  progress = handler;
		  start ();
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
								
								config.parse (json)
								config
									 .put ("dbname", Pos.app.dbname ())
									 .put ("business_unit", Pos.app.config.get ("business_unit"))
									 .put ("pos_unit", Pos.app.config.get ("pos_unit"))
									 .put ("pos_config_id", Pos.app.posConfigID ())

								Pos.app.config = config
								row.put ("config", config.toString ())
								Pos.app.db ().insert (update.getString ("update_table"), row)
								
								config.initialize ()
								Pos.app.config.put ("config_loaded", true)

								Pos.app.updateMenus ()								
						  }

						  "local_item" -> {

								val itemResult =DbResult ("select id from items where sku = '" + row.getString ("sku") + "'", Pos.app.db ())
								if (itemResult.fetchRow ()) {

									 val item = itemResult.row ()
								}
								
								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }
						  
						  "item_prices" -> {
								
								row.put ("pricing", row.get ("pricing").toString ())
								Pos.app.db ().insert (update.getString ("update_table"), row)

								var select = "select * from items where id = ${row.getInt ("item_id")}"
								var itemResult = DbResult (select, Pos.app.db)
								while (itemResult.fetchRow ()) {
				
									 val ie = itemResult.row ()
								}

								select = "select * from item_prices where item_id = ${row.getInt ("item_id")}"
								itemResult = DbResult (select, Pos.app.db)
								while (itemResult.fetchRow ()) {
				
									 val ie = itemResult.row ()
								}
						  }
						  
						  "item_addons" -> {

								row.put ("jar", row.get ("jar").toString ())
								Pos.app.db ().insert (update.getString ("update_table"), row)
						  }

						  
						  "business_units" -> {
								
								if (update.get ("update").getInt ("id") == Pos.app.config.get ("business_unit").getInt ("id")) {

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
				
				"send_log" -> {
					 
					 FileUtils.uploadLog ();
				}
				
				"pipe" -> {
					 
					 FileUtils.pipe (m.getString ("command"));
				}

				"sql" -> {

					 Logger.x ("sql... " + message.getString ("sql"));
					 Pos.app.db.exec (message.getString ("sql"));
				}
		  }
	 }

	 fun nudge () {
		  
		  handler.sendMessage (Message.obtain ())
	 }
}
