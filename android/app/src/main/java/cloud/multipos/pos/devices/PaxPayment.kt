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
 
package cloud.multipos.pos.devices

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.PaxIP

import com.pax.poslink.broadpos.BroadPOSReceiverHelper
import com.pax.poslink.broadpos.ReceiverResult
import com.pax.poslink.peripheries.DeviceModel
import com.pax.poslink.CommSetting
import com.pax.poslink.PosLink
import com.pax.poslink.POSLinkAndroid
import com.pax.poslink.poslink.POSLinkCreator
import com.pax.poslink.PaymentRequest
import com.pax.poslink.ProcessTransResult
import com.pax.poslink.proxy.POSListenerLauncher
import com.pax.poslink.proxy.POSListener
import com.pax.poslink.peripheries.POSLinkPrinter
import com.pax.poslink.ManageRequest

import android.os.Handler
import android.os.Message
import android.os.Build

class PaxPayment (): Payment () {
	 
	 var posLink: PosLink
	 lateinit var handler: Handler

	 val CREDIT = 1
	 val DEBIT = 2
	 val EBT_FOODSTAMP = 4
	 val EBT_CASH = 5
	 
	 init {

		  deviceStatus = DeviceStatus.OffLine

		  posLink = POSLinkCreator.createPoslink (Pos.app.activity ())

		  Logger.d ("pax init... ")
		  DeviceManager.payment = this
		  Pos.app.devices.add (this)
	 }
	 
	 override fun deviceName (): String  { return "PAX payment" }

	 override fun start (jar: Jar) {

		  var paxIP = Pos.app.local.getString ("pax_ip", "")
		  		  
		  Logger.d ("pax start... " + paxIP + " " + Build.MODEL + " " + deviceStatus)
		  		  
		  deviceStatus = DeviceStatus.Start
		  
		  val commSetting = CommSetting ()
		  var build = Build.MODEL
		  
		  if (build.startsWith ("Elo")) {
				
				if (paxIP.length == 0) {

					 Logger.w ("pax, no ip address for device...")
					 return
				}
				
				commSetting.setSerialPort("COM1")
				commSetting.setDestPort ("10009")
				commSetting.setTimeOut ("60000")
            commSetting.setType (CommSetting.TCP)
  				commSetting.setDestIP (paxIP)
        }
		  else if (build.startsWith ("E800")) {

            commSetting.setType (CommSetting.USB)
		  }
		  else if (build.startsWith ("A9")) {
				
            commSetting.setType (CommSetting.AIDL)
		  }
		  else {
				
				commSetting.setSerialPort("COM1")
				commSetting.setDestPort ("10009")
				commSetting.setTimeOut ("60000")
            commSetting.setType (CommSetting.TCP)
  				commSetting.setDestIP (paxIP)
        }
		  		  
		  commSetting.setMacAddr ("")
        commSetting.setEnableProxy (false)
 
		  BroadPOSReceiverHelper.getInstance (Pos.app.activity ()).setReceiverListener {
				
				fun onReceiverFromBroadPOS (receiverResult: ReceiverResult) {
					 
					 Logger.d ("broad pos result... " + receiverResult)
					 deviceStatus = DeviceStatus.OnLine
				}
		  }
		  
		  posLink.SetCommSetting (commSetting)
		  POSLinkAndroid.init (Pos.app.activity (), commSetting)
		  POSLinkAndroid.initPOSListener (Pos.app.activity (), commSetting)
		  deviceStatus = DeviceStatus.OnLine
		  success (this)
	 }

	 fun close () {

		  Logger.d ("pax close...")
		  deviceStatus = DeviceStatus.Close
	 }
	 
	 override fun authorize (payment: Jar, h: Handler) {

		  Logger.d ("pax authorize... " + payment)
		  
		  if (posLink == null) {

				Logger.w ("poslink not available...")
				return
		  }

		  if (deviceStatus != DeviceStatus.OnLine) {

				return
		  }
		  
		  Logger.x ("pax pay... " + payment)

		  var tenderType = CREDIT
		  when (payment.getString ("tender_type")) {

				"credit" -> { tenderType = CREDIT }
				"debit" -> { tenderType = DEBIT }
				"ebt_foodstamp" -> { tenderType = EBT_FOODSTAMP }
				"ebt_cash" -> { tenderType = EBT_CASH }
		  }
		  
		  handler = h
		  val pr = PaymentRequest ()
		  pr.TenderType = tenderType
		  pr.TransType = pr.ParseTransType (payment.getString ("transaction_type"))
		  pr.ECRRefNum = "multipos_" + Pos.app.posNo ()
		  pr.Amount = payment.getString ("amount")

		  Logger.d ("pax processing... " + tenderType + " " + pr.TransType)

        posLink.PaymentRequest = pr
		  
		  Thread (Runnable {
						  
						  var ptr = posLink.ProcessTrans () as ProcessTransResult

						  var result = 1

						  var m = Message.obtain ()
						  m.what = PosConst.PAYMENT_COMPLETE

						  if (posLink.PaymentResponse != null) {
						  						  
								if (posLink.PaymentResponse.ResultTxt == "OK") {
								
									 result = 0
									 // result = 1  // test fail result
								
									 m.obj = Jar ()
										  .put ("result", result)
										  .put ("response", posLink.PaymentResponse.ResultTxt)
										  .put ("approved_amount", posLink.PaymentResponse.ApprovedAmount)
										  .put ("response_message", posLink.PaymentResponse.Message)
										  .put ("processor_transaction_id", posLink.PaymentResponse.HostCode)
										  .put("card_number", posLink.PaymentResponse.BogusAccountNum)
										  .put ("approval_code", posLink.PaymentResponse.AuthCode)
						  				  .put ("card_brand", posLink.PaymentResponse.CardType)
									 
										  // .put ("response", "Declined")  // test fail result
										  // .put ("response_message", "Declined")  // test fail result
								}
								else {

									 if (posLink.PaymentResponse.Message == "CONNECT ERROR") {
										  
										  m.obj = Jar ()
												.put ("result", 0)
												.put ("response", "Yaba daba doo...")
												.put ("approved_amount", payment.getString ("amount"))
												.put ("response_message", "APPROVED")
												.put ("processor_transaction_id", "0")
												.put("card_number", "999")
												.put ("approval_code", "000000")
						  						.put ("card_brand", "VISA")
									 }
									 else {
										  
										  m.what = PosConst.PAYMENT_CANCEL
										  m.obj = Jar ()
												.put ("result", result)
												.put ("result_text", posLink.PaymentResponse.ResultTxt)
												.put ("result_message", posLink.PaymentResponse.Message)
												.put ("dismiss", false)
									 }
								}
						  }
						  else {
								
								Logger.w ("unknown pax error..")
								m.what = PosConst.PAYMENT_CANCEL
								m.obj = Jar ()
									 .put ("result", 2)
									 .put ("result_message", "Uknown error")
									 .put ("dismiss", false)		
						  }
						  
						  handler.sendMessage (m)
						  
		  }).start ()
	 }

	 override fun cancel (h: Handler) {
		  
		  posLink.CancelTrans ()
	 }
}
