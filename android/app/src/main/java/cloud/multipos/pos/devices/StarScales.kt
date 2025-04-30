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
 
package cloud.multipos.pos.devices;

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import java.util.Random

import com.starmicronics.starmgsio.StarDeviceManager;
import com.starmicronics.starmgsio.StarDeviceManagerCallback;
import com.starmicronics.starmgsio.ConnectionInfo;
import com.starmicronics.starmgsio.Scale;
import com.starmicronics.starmgsio.ScaleCallback;
import com.starmicronics.starmgsio.ScaleData;
import com.starmicronics.starmgsio.ScaleOutputConditionSetting;
import com.starmicronics.starmgsio.ScaleSetting;
import com.starmicronics.starmgsio.ScaleType;

/**
 *
 */

class StarScales (): Scales () {

	 lateinit var deviceManager: StarDeviceManager
	 lateinit var appCallback: ScalesCallback

	 var weight = 0.0
	 var starScales: StarScales
	 var capture = false
	 var params: Jar

	 init {
		  
		  starScales = this
		  deviceStatus = DeviceStatus.OffLine
		  Pos.app.devices.add (this)
		  params = Jar ()
	 }
	 
	 override fun deviceName (): String  { return "Star Scale" }
	 
	 /**
	  * Scale
	  */
	 
	 override fun startCapture (appCallback: ScalesCallback) {
		  
		  this.appCallback = appCallback
		  capture = true
	 }
	 
	 override fun stopCapture () {

		  capture = false
	 }

	 /**
	  * Device
	  */
	 
	 override fun start (jar: Jar) {
		  
		  Logger.d ("start star scale... ${jar}")

		  params = jar
		  
		  var deviceManagerCallback: StarDeviceManagerCallback = object: StarDeviceManagerCallback () {
				
				override fun onDiscoverScale (connectionInfo: ConnectionInfo) {
					 
					 val interfaceType = connectionInfo.getInterfaceType ().name
					 val deviceName = connectionInfo.getDeviceName ()
					 val ident = connectionInfo.getIdentifier ()
					 val scaleType = connectionInfo.getScaleType ().name

					 Logger.x ("scale connect info... " + interfaceType + " " +
								  deviceName + " " +
								  ident + " " +
								  scaleType)

					 val ci = ConnectionInfo.Builder ()
						  .setBleInfo (ident)
						  .build ()
					 
					 deviceManager = StarDeviceManager (Pos.app)
					 val scale = deviceManager.createScale (ci)
					 scale.connect (scaleCallback)
				}
		  }
		  
        deviceManager = StarDeviceManager (Pos.app, StarDeviceManager.InterfaceType.All)
		  deviceManager.scanForScales (deviceManagerCallback);
	 }
	 	 
	 /**
	  * Stop
	  */

	 override fun stop () {

		  Logger.d ("star scale stop")
	 }

	 override fun weight (): Double {


		  if (deviceStatus == DeviceStatus.OnLine) {

				// get it from the device

				return weight
		  }
		  return 0.0
	 }
	 
	 /**
	  * Star scale callback
	  */
	 
	 var scaleCallback: ScaleCallback = object: ScaleCallback () {

		  override fun onConnect (scale: com.starmicronics.starmgsio.Scale, status: Int) {

				Logger.d ("scale connect... " + status)
				deviceManager.stopScan ()
				
				when (status) {
					 
                Scale.CONNECT_SUCCESS -> {
						  
                    Logger.i ("star scales connect success....")
						  
						  scale.updateOutputConditionSetting (ScaleOutputConditionSetting.ContinuousOutputAtStableTimes)  // send single weight when stable
						  starScales.success (starScales)
						  return
                }

                Scale.CONNECT_NOT_AVAILABLE -> {
						  
                    Logger.w ("star scales failed to connect. (Not available)...")
                }

                Scale.CONNECT_ALREADY_CONNECTED -> {
						  
                    Logger.w ("star scales failed to connect. (Already connected)...")
                }
					 
                Scale.CONNECT_TIMEOUT -> {
						  
                    Logger.w ("star scales failed to connect. (Timeout)...")
                }

                Scale.CONNECT_READ_WRITE_ERROR -> {
						  
                    Logger.w ("star scales failed to connect. (Read Write error)...")
                }

                Scale.CONNECT_NOT_SUPPORTED -> {
						  
                    Logger.w ("star scales failed to connect. (Not supported device)...")
                }

                Scale.CONNECT_NOT_GRANTED_PERMISSION -> {
						  
                    Logger.w ("star scales failed to connect. (Not granted permission)...")
                }

						  
                Scale.CONNECT_UNEXPECTED_ERROR -> {
						  
                    Logger.w ("star scales failed to connect. (Unexpected error)...")
                }

					 else -> {
						  
                    Logger.w ("star scales failed to connect. (unknown error)...")
					 }
				}
				
				deviceStatus = DeviceStatus.OffLine
		  }

		  override fun  onDisconnect (scale: com.starmicronics.starmgsio.Scale, status: Int) {

				Logger.d ("star scale disconnect... " + status)
				deviceStatus = DeviceStatus.OffLine
		  }
		  
		  override fun  onReadScaleData (scale: com.starmicronics.starmgsio.Scale, scaleData: ScaleData) {
									 
				Logger.d ("star scale read... " + scaleData.getWeight ())
				
				weight = scaleData.getWeight ()
				appCallback.scaleData (weight)
		  }
	 }
}
