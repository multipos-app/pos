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
 
package cloud.multipos.pos.util

import cloud.multipos.pos.Pos

import android.content.SharedPreferences
import android.content.Context
import android.util.Base64
import java.net.NetworkInterface
import java.net.InetAddress
import android.net.wifi.WifiManager
import java.util.regex.Pattern

lateinit private var prefs: SharedPreferences

class Local () {

	 init {
		  
		  prefs = Pos.app.activity.getPreferences (Context.MODE_PRIVATE)
		  initNetwork ()
	 }
	 
	 public fun put (key: String, value: String): Local {
		  
		  prefs.edit ().putString (key, value).apply ()
		  return this
	 }
	 
	 public fun put (key: String, value: Boolean): Local {
		  
		  prefs.edit ().putBoolean (key, value).commit ()
		  return this
	 }

	 public fun put (key: String, value: Int): Local {
		  
		  prefs.edit ().putInt (key, value).commit ()
		  return this
	 }

	 fun get (key: String): Jar {

		  var p = prefs.getString (key, "{}")
		  return Jar (p)
	 }
	 
	 fun getString (key: String, defValue: String): String {

		  if (prefs.contains (key)) {

				// val tmp = prefs.getString (key, defValue)
				// return tmp!!
				return prefs.getString (key, defValue)!!
		  }
		  else {

				return defValue
		  }
	 }

	 fun getInt (key: String, defValue: Int): Int {

		  return prefs.getInt (key, defValue)
	 }

	 fun has (key: String): Boolean {

		  return prefs.contains (key)
	 }
	 
	 public fun clear (): Local {
		  
		  prefs.edit ().clear ().commit ()
		  return this
	 }

	 public fun clear (key: String): Local {
		  
		  prefs.edit ().remove (key).commit ()
		  return this
	 }

 	 fun initNetwork () {

		  val digit = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
		  val regex = "^" + digit + "\\." + digit + "\\." + digit + "\\." + digit + "$"
		  val pattern = Pattern.compile (regex)

		  var macAddr = ""
		  var ipAddr = ""
		  var baseIPAddr = ""
		  var broadcastIPAddr = ""

		  for (inf in NetworkInterface.getNetworkInterfaces ()) {
				

            if (inf.getName ().equals ("wlan0")) {

					 macAddr = inf.getHardwareAddress ().toMacString ()
            }
								
				if (!inf.isLoopback ()) {
					 
					 for (addr in  inf.getInetAddresses ()) {
						  						  
						  var ipaddress = addr.getHostAddress ()

		   			  if (pattern.matcher (ipaddress.toString ()).matches ()) {
								
								ipAddr = addr.toString ().substring (1)  // strip initial '/'
						  }
					 }
				}
		  }
		  
		  if (ipAddr.length > 0) {
				
				val ips = ipAddr.split (".")
				baseIPAddr =  ips [0] + "." + ips [1] + "." + ips [2] + "."
				broadcastIPAddr =  ips [0] + "." + ips [1] + ".255.255"
		  }

		  put ("mac_addr", macAddr)
		  put ("ip_addr", ipAddr)
		  put ("base_ip_addr", baseIPAddr)
		  put ("broadcast_ip_addr", broadcastIPAddr)
	 }
	 
	 fun ByteArray.toMacString () = joinToString (":") {
		  
		  "%02x".format (it)
	 }
}
