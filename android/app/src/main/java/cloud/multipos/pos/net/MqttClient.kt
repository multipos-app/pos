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
 
package cloud.multipos.pos.net;

import cloud.multipos.pos.R;
import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.views.PosMenus;
import cloud.multipos.pos.devices.*;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttClient () {

	 val MULTIPOS_CLIENT_USER_NAME = "<user-name>"
	 val MULTIPOS_CLIENT_PASSWORD = "<password>"
	 val MULTIPOS_MQTT_HOST = "ec2-35-83-141-32.us-west-2.compute.amazonaws.com"
	 
	 val MULTIPOS_CONNECTION_TIMEOUT = 3
	 val MULTIPOS_CONNECTION_KEEP_ALIVE_INTERVAL = 60
	 val MULTIPOS_CONNECTION_CLEAN_SESSION = true
	 val MULTIPOS_CONNECTION_RECONNECT = false
	 val QOS = 1
	 
    var mqttClient: MqttAndroidClient
    private val clientId: String = "b442b63190ef7c508557689227"
	 // private val serverUri = "tcp://ec2-35-83-141-32.us-west-2.compute.amazonaws.com:1883"  // multipos.cloud
	 private val serverUri = "tcp://ec2-54-148-117-191.us-west-2.compute.amazonaws.com:1883"
	 private var topic = ""
	 
	 init {

		  mqttClient = MqttAndroidClient (Pos.app, serverUri, clientId)
		  setMqttCallBack ()
	 }
	 
	 private fun setMqttCallBack () {
		  
        mqttClient.setCallback (object : MqttCallbackExtended {
												override fun connectionLost (cause: Throwable?) {

													 Logger.i ("mqtt connection lost...")
													 Thread.sleep (1000)
													 connect (topic)
												}
												
												@Throws (Exception::class)
												override fun messageArrived (topic: String?, message: MqttMessage?) {
													 
													 Logger.i ("mqtt message arrived... " + message)
													 BackOffice.download ()
												}

												override fun deliveryComplete (token: IMqttDeliveryToken?) {
													 
													 Logger.i ("mqtt delivery complete...")
												}

												override fun connectComplete (reconnect: Boolean, serverURI: String?) {
													 
													 Logger.i ("mqtt connect complete...")
													 subscribe (topic, 0)
												}

        })
	 }

	 public fun connect (t: String) {

		  topic = t
        Logger.i ("mqtt connect: Trying to call connect function")
        val mqttConnectOptions = MqttConnectOptions ()
        mqttConnectOptions.isAutomaticReconnect = MULTIPOS_CONNECTION_RECONNECT
        mqttConnectOptions.isCleanSession = MULTIPOS_CONNECTION_CLEAN_SESSION
        mqttConnectOptions.connectionTimeout = MULTIPOS_CONNECTION_TIMEOUT
        mqttConnectOptions.keepAliveInterval = MULTIPOS_CONNECTION_KEEP_ALIVE_INTERVAL

        try {
            Logger.i ( "mqtt connect: Inside the try block")

            mqttClient.connect (
                mqttConnectOptions, null, object : IMqttActionListener {
						  
                    override fun onSuccess (asyncActionToken: IMqttToken?) {

                        Logger.i ("mqtt onSuccess: Successfully connected to the broker")
                        val disconnectBufferOptions = DisconnectedBufferOptions ()
                        disconnectBufferOptions.isBufferEnabled = true
                        disconnectBufferOptions.bufferSize = 100
                        disconnectBufferOptions.isPersistBuffer = false
                        disconnectBufferOptions.isDeleteOldestMessages = false
                        mqttClient.setBufferOpts (disconnectBufferOptions)
                    }

                    override fun onFailure (asyncActionToken: IMqttToken?, exception: Throwable?) {

                        Logger.w ("mqtt Failed to connect to: $serverUri; ${exception}")
                    }

                }
            )
        }
		  catch (ex: MqttException) {
				
            ex.printStackTrace ()
        }
    }

    fun subscribe (subscriptionTopic: String, qos: Int = QOS) {
		  
        try {
				
            mqttClient.subscribe (subscriptionTopic, qos, null, object : IMqttActionListener {
												  
												  override fun onSuccess (asyncActionToken: IMqttToken?) {
														
														Logger.i ("mqtt subscribed to topic, $subscriptionTopic")
												  }

												  override fun onFailure (asyncActionToken: IMqttToken?, exception: Throwable?) {
														Logger.w ("mqtt subscription to topic $subscriptionTopic failed!")
												  }
            })
        }
		  catch (ex: MqttException) {
				
            System.err.println ("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace ()
        }
    }
}
