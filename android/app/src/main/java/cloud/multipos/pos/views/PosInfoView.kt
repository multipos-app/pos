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
 
package cloud.multipos.pos.views;

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ListView
import android.widget.Button
import android.widget.TextView
import android.widget.ArrayAdapter

import java.util.regex.Pattern
import java.net.NetworkInterface
import java.net.InetAddress
import android.net.wifi.WifiManager

class PosInfoView (): DialogView ("POS Info") {

	 var version: TextView
	 var merchant: TextView
	 var posNo: TextView
	 var wifiIP: TextView
	 var wifiMac: TextView
	 var ethIP: TextView
	 var ethMac: TextView
	 
	 init {

		  Pos.app.inflater.inflate (R.layout.pos_info_layout, this)
 		  
		  var uploadLog = findViewById (R.id.upload_log) as Button
	  	  uploadLog.setOnClickListener {

				FileUtils.uploadLog ()
		  }
		  
		  version = findViewById (R.id.info_version) as TextView
		  merchant = findViewById (R.id.merchant_id) as TextView
		  posNo = findViewById (R.id.pos_no) as TextView
		  wifiIP = findViewById (R.id.wifi_ip) as TextView
		  wifiMac = findViewById (R.id.wifi_mac) as TextView
		  ethIP = findViewById (R.id.eth_ip) as TextView
		  ethMac = findViewById (R.id.eth_mac) as TextView

		  version.text = Pos.app.getString ("app_version")
		  merchant.text = Pos.app.dbname ()
		  posNo.text = Pos.app.posNo ().toString ()
		  
		  getIPs ()

		  version.setText (Pos.app.getString (R.string.app_version))
		  merchant.text = Pos.app.dbname ()
		  posNo.text = "POS No ${Pos.app.posNo ()}" 
				
		  DialogControl.addView (this)
	 }


	 fun getIPs () {
		  
		  val digit = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
		  val regex = "^/" + digit + "\\." + digit + "\\." + digit + "\\." + digit + "$"
		  var pattern = Pattern.compile (regex)

		  var interfaces = NetworkInterface.getNetworkInterfaces ().iterator ()

		  for (intf in interfaces) {
				
				when (intf.getName ()) {

					 "wlan0", "eth0"	-> {

						  var ipv4Addr = "/not connected"
						  
						  val bytes = intf.getHardwareAddress ();
						  
						  if (bytes != null) {
								
								var mac = StringBuilder ()
								var sep = ""
								
								for (b in bytes) {
									 
									 mac.append (sep).append (String.format ("%02X", b))
									 sep = ":"
								}
								
								if (intf.isUp ()) {
									 for (inetAddr in intf.getInetAddresses ()) {
										  
										  if (pattern.matcher (inetAddr.toString ()).matches ()) {
												ipv4Addr = inetAddr.toString ().substring (0)
										  }
									 }
								}
								
								if (intf.getName () == "wlan0") {
									 
									 wifiIP.text = ipv4Addr.substring (1)
									 wifiMac.text = mac.toString ()
								}
								else {
									 
									 ethIP.text = ipv4Addr.substring (1)
									 ethMac.text = mac.toString ()
								}
						  }
					 }
				}
		  }
	 }
}
