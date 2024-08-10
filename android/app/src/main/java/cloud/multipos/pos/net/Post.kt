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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.DeviceStatus

import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.DefaultRetryPolicy

public class Post (val url: String) {

	 var jar = Jar ()
	 companion object {

		  val queue = Volley.newRequestQueue (Pos.app.activity)
	 }

	 init {
				
		  jar
				.put ("version", Pos.app.getString ("app_version"))
				.put ("dbname", Pos.app.local.getString ("dbname", ""))
				.put ("business_unit_id", Pos.app.local.getInt ("business_unit_id", 0))
				.put ("device_id", Pos.app.local.getInt ("id", 0))
				.put ("access_token", Pos.app.local.getString ("access_token", ""))
	 }
	 
	 fun exec (fn: (p: Jar) -> Unit ) {

		  var url = Pos.app.getString ("posapi") + url
		  var sep = ""
		  
		  val req = JsonObjectRequest (Request.Method.POST,
												 url,
												 jar.json (),
												 Response.Listener {
													  response -> run {
															
															response.put ("status", 0)
															fn (Jar (response))
															BackOffice.deviceStatus = DeviceStatus.OnLine
													  }
												 },
												 Response.ErrorListener {

													  error -> run {
															
															Logger.x ("post error... " + error.toString ())

															val response = Jar ()
																 .put ("status", 1)
																 .put ("status_text", error.toString ())
															
															fn (response)

															BackOffice.deviceStatus = DeviceStatus.OffLine
													  }
												 })

		  val DEFAULT_BACKOFF_MULT = 1f;
		  req.setRetryPolicy (DefaultRetryPolicy (20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		  queue.add (req);
	 }

	 fun add (p: Jar): Post {

		  jar = p
		  jar
				.put ("version", Pos.app.getString ("app_version"))
				.put ("merchant_id", Pos.app.local.getInt ("merchant_id", 0))
				.put ("business_unit_id", Pos.app.local.getInt ("business_unit_id", 0))
				.put ("device_id", Pos.app.local.getInt ("id", 0))
				.put ("access_token", Pos.app.local.getString ("access_token", ""))
		  
		  return this
	 }
	 
}
