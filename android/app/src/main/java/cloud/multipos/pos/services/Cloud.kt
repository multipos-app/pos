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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.DeviceStatus

import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;

public class Cloud () {

	 var jar = Jar ()
	 companion object {

		  val queue = Volley.newRequestQueue (Pos.app.activity)
	 }

	 init {

		  jar
				.put ("version", "1.0")
				.put ("dbname", Pos.app.dbname ())
				.put ("business_unit_id", Pos.app.buID ())
				.put ("pos_no", Pos.app.posNo ())
				.put ("pos_unit_id", Pos.app.posUnitID ())
	 }
	 
	 fun post (query: List <String>, fn: (p: Jar) -> Unit ) {

		  var url = Pos.app.getString ("posapi")
		  var sep = ""
		  
		  query.forEach {

				url += sep + it
				sep = "/"
		  }
		  
		  val req = JsonObjectRequest (Request.Method.POST,
												 url,
												 jar.json (),
												 Response.Listener {
													  response -> run {
															
															response.put ("status", 0)
															fn (Jar (response))
															Pos.app.cloudService.deviceStatus (DeviceStatus.OnLine)
													  }
												 },
												 Response.ErrorListener {

													  error -> run {
															
															Logger.x ("cloud error... " + error.toString ())

															val response = Jar ()
																 .put ("status", 1)
																 .put ("status_text", error.toString ())
															
															fn (response)

															Pos.app.cloudService.deviceStatus (DeviceStatus.OffLine)
													  }
												 })
		  
		  queue.add (req);
	 }

	 fun add (name: String, p: List <Jar>): Cloud {

		  jar.put (name, p)
		  return this
	 }
	 
	 fun add (name: String, p: Jar): Cloud {

		  jar.put (name, p)
		  return this
	 }
}
