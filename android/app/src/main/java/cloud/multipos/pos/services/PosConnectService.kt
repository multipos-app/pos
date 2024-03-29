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
import cloud.multipos.pos.models.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*

import android.content.Context
import android.net.nsd.NsdServiceInfo
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdManager.ResolveListener

import java.net.ServerSocket
import java.net.InetAddress
import java.io.IOException

class PosConnectService (): Service () {
	 
	 val SERVICE_NAME = "PosAppConnect"
	 val SERVICE_TYPE = "_posappconnect._tcp"
	 
	 var ready = false
	 var port: Int = 0
	 var sock: ServerSocket

	 init {
		  
		  sock = ServerSocket (0)
		  port = sock.getLocalPort ()
		  
		  var registrationListener: NsdManager.RegistrationListener = object: NsdManager.RegistrationListener {

				override fun onServiceRegistered (nsdServiceInfo: NsdServiceInfo) {
					 
					 var serviceName = nsdServiceInfo.getServiceName ()
					 Logger.d ("pos connect registered... " + nsdServiceInfo)
				}
				
				override fun onRegistrationFailed (serviceInfo: NsdServiceInfo, errorCode: Int) {
				}
				
				override fun onServiceUnregistered (arg0: NsdServiceInfo) {
				}
				
				override fun onUnregistrationFailed (serviceInfo: NsdServiceInfo, errorCode: Int) {
				}
		  }

		  val serviceInfo = NsdServiceInfo ()
		  serviceInfo.setServiceName (SERVICE_NAME)
		  serviceInfo.setServiceType (SERVICE_TYPE)
		  serviceInfo.setPort (port)
		  val nsdManager = Pos.app.activity.getSystemService (Context.NSD_SERVICE) as NsdManager
		  nsdManager.registerService (serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
		  
		  Logger.d ("pos connect service... " + serviceInfo)

		  // Look for services
		  
		  val resolveListener: NsdManager.ResolveListener = object: NsdManager.ResolveListener {

				override fun onResolveFailed (serviceInfo: NsdServiceInfo, errorCode: Int) {
					 
					 Logger.w ("pos connect resolve failed... " + errorCode)
				}

				override fun onServiceResolved (serviceInfo: NsdServiceInfo) {
					 
					 Logger.i ("pos connect resolve succeeded... " + serviceInfo)

					 if (serviceInfo.getServiceName ().equals (SERVICE_NAME)) {
						  
						  Logger.d ("pos connect resolve same IP...")
						  return
					 }

					 var remote = serviceInfo as NsdServiceInfo
					 var port = remote.getPort () as Int
					 var host = remote.getHost () as InetAddress
					 
					 Logger.d ("pos connect resolve... " + host + " " + port)
				}
		  }

		  val discoveryListener: NsdManager.DiscoveryListener = object: NsdManager.DiscoveryListener {

				override fun onDiscoveryStarted (regType: String) {
					 
					 Logger.d ("pos connect service discovery started...")
				}

				override fun onServiceFound (service: NsdServiceInfo) {
					 
					 Logger.d ("pos connect pos connect service discovery success... " + service.getServiceType () + " " + service.getServiceName () + " " + service.getAttributes ())
					 
					 if (service.getServiceName ().equals (SERVICE_NAME)) {
								
						  Logger.d ("pos connect same machine... " + service)
					 }
					 else if (service.getServiceName ().contains (SERVICE_NAME)) {
								
						  Logger.d ("pos connect other machine... " + service)
								
						  // nsdManager.resolveService (service, resolveListener)
					 }
				}
				
				override fun onServiceLost (service: NsdServiceInfo) {
					 
					 Logger.w ("pos connect service lost... " + service)
				}
				
				override fun onDiscoveryStopped (serviceType: String) {
					 
					 Logger.i ("pos connect discovery stopped... " + serviceType)
				}
				
				override fun onStartDiscoveryFailed (serviceType: String, errorCode: Int) {
					 
					 Logger.w ("pos connect discovery failed: Error code... " + errorCode)
					 nsdManager.stopServiceDiscovery (this)
				}
				
				override fun onStopDiscoveryFailed (serviceType: String, errorCode: Int) {
					 
					 Logger.w ("pos connect discovery failed: Error code... " + errorCode)
					 nsdManager.stopServiceDiscovery (this)
				}
		  }
		  
		  nsdManager.discoverServices (SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
		  ready = true
	 }

	 override fun ready (): Boolean { return ready }
	 
	 override fun process (message: ServiceMessage) : Boolean {

		  return true
	 }	 
}
