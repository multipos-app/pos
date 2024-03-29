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
 
package cloud.multipos.pos

import cloud.multipos.pos.db.*
import cloud.multipos.pos.views.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.services.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*

import android.os.Handler
import android.os.Message

class PosHandler (): Handler () {

	 override fun handleMessage (m: Message) {

		  when (m.what) {
				
				PosConst.CLOUD_READY -> {
					 
					 Pos.app.start ()
				}
				
				PosConst.POS_SETUP -> {
					 
					 Pos.app.setup ()
				}
				
				PosConst.LOGIN -> {

					 Control.factory ("LogIn").action (m.obj as Jar)
				}
				
				PosConst.HOME -> {
					 
					 // PosMenus.home ()
				}
				
				PosConst.SCAN -> {

					 if (Pos.app.loggedIn) {
						  
						  val scan = m.obj as String

						  Logger.d ("scan... " + scan.length + " " + scan)
						  
						  if (Pos.app.controls.size > 0) {

								val control = Pos.app.controls.get (0)
								Pos.app.controls.remove (control)
								control.action (control.jar ().put ("scan", scan))
								return
						  }

						  when (scan.length)  {

								5, 6, 7, 8, 12, 13 -> {
									 
									 val item = DefaultItem ()
									 item.action (Jar ().put ("sku", scan).put ("entry_mode", "scanned"))
								}
								
								11, 36, 108 -> {
									 
									 val recall = RecallByUUID ()
									 recall.action (Jar ().put ("recall_key", scan).put ("entry_mode", "scanned"))
								}
						  }	  
					 }
				}
				
				PosConst.WAIT -> {
				}

				PosConst.REMOTE_TICKET -> { }

				PosConst.TICKET_NOT_FOUND -> {

					 PosDisplays.message (Pos.app.getString ("ticket_not_found"))
				}

				PosConst.DISPLAY_MESSAGE -> {

					 PosDisplays.message (m.obj as String)
				}
				
				PosConst.CONTROL -> {

					 val p = m.obj as Jar
					 val control = Control.factory (p.getString ("class"))
					 control.action (p.get ("jar"))
				}

				PosConst.MESSAGE -> {

					 Pos.app.messages.add (0, m.obj as Jar)
				}
				
				PosConst.PRINTER_COMPLETE, PosConst.PRINTER_ON_LINE -> {

				}

				else -> {
	  
					 Logger.w ("Unknown directive... " + m.what)
				}
		  }
	 }
}
