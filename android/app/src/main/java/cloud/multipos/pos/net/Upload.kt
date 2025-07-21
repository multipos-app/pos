/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.PosMenus
import cloud.multipos.pos.devices.*

import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray

import com.android.volley.*
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest

import java.util.HashMap
import java.util.ArrayList

import android.os.Handler
import android.os.Message
import java.util.concurrent.locks.*

import java.net.HttpURLConnection
import java.io.*
import java.net.URL
import java.net.MalformedURLException
import java.util.zip.GZIPInputStream
import android.util.Base64
import java.util.zip.ZipInputStream
import java.time.ZonedDateTime

class Upload () {

	 var updateID = -1
	 var downloadCount = 0
	 var posapi = ""
	 var url = ""
	 var upload = Jar ()

	 init { }

	 fun add (ticket: Ticket): Upload {

		  upload = Jar ()
				.put ("id", ticket.getInt ("id"))
				.put ("ticket", ticket)

		  url = "pos/ticket"
		  return this
	 }

	 fun add (item: Item): Upload  {
		  
		  upload = Jar ()
		  url = "pos/add-item"
		  return this
	 }
	 
	 fun add (customer: Customer): Upload  {
		  
		  upload = Jar ()
		  url = "pos/add-customer"
		  return this
	 }
	 
	 fun exec () {
		  
		  Post (url)
				.add (upload)
				.exec (fun (result: Jar): Unit {
							  
							  if (result.getInt ("status") == 0) {
																		
									if (upload.has ("ticket")) {
										 
		   							 Pos.app.db ().exec ("delete from pos_updates where id = " + upload.get ("ticket").getInt ("id"))
									}
							  }
							  else {
		  
									Pos.app.db ().insert ("pos_updates", Jar ()
																	  .put ("status", 0)
																	  .put ("update_table", "tickets")
																	  .put ("update_id", upload.getInt ("id"))
																	  .put ("update_action", 0))	
							  }
				})
		  
	 }
}
