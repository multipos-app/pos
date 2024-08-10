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
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.graphics.Color;

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

import android.os.Handler
import android.os.Message

class PosAppBar (context: Context, attrs: AttributeSet): PosLayout (context, attrs), PosDisplay {

	 var p: ImageView
	 var clock: PosText
	 var customerLayout: LinearLayout? = null
	 var customerName: PosText? = null
	 val devices = mutableListOf <DeviceIcon> ()		  
	 var tray: LinearLayout
	 
	 init {

		  Pos.app.posAppBar = this

		  if (Pos.app.config.getBoolean ("customers")) {
				
				Pos.app.inflater.inflate (Pos.app.resourceID ("pos_app_bar_customer", "layout"), this)
				
				// customer
		  
				customerLayout = findViewById (R.id.app_bar_customer_layout) as LinearLayout?
				customerName = findViewById (R.id.app_bar_customer_name) as PosText?
		  	 	customerName?.setText (Pos.app.getString ("search_customer"))

				customerLayout?.setOnClickListener {
					 
					 if (Pos.app.customer != null) {
						  
						  CustomerEditView (Pos.app.ticket.get ("customer"))
					 }
					 else {
						  
						  CustomerSearchView (null)
					 }
				}
		  }
		  else {

				Pos.app.inflater.inflate (Pos.app.resourceID ("pos_app_bar", "layout"), this)
		  }
		  
		  // app icon
		  
		  p = findViewById (Pos.app.resourceID ("pos_info", "id"))	
		  p?.setOnClickListener {
						  
				if (Pos.app.messages.size > 0) {
					 
					 // MessageDialog ()
				}
				else {
					 
					 PosInfoView ()
				}
		  }

		  // time/date
		  
		  clock = findViewById (Pos.app.resourceID ("app_bar_clock", "id"))
		  clock?.setTypeface (Views.receiptFont ())
		  clock?.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.white))
		  
		  // devices
		  				
		  tray = findViewById (Pos.app.resourceID ("app_bar_tray", "id"))
		  
		  for (device in Pos.app.devices) {
				
				var deviceIcon = DeviceIcon (device)
				devices.add (deviceIcon)
				tray.addView (deviceIcon, 0)
				
				Logger.x ("pos app bar init... " + device)

		  }

		  // handler,  clock and device update thread
		  
		  var handler: Handler = object: Handler () {

				override fun handleMessage (message: Message) {

					 if (Pos.app.messages.size > 0) {
					 
						  var resource = if (message.what == 0) R.drawable.app_icon else R.drawable.app_icon_transparent
						  p?.setImageDrawable (Pos.app.getResources ().getDrawable (resource))
					 }
					 else {
						  p?.setImageDrawable (Pos.app.activity.getResources ().getDrawable (R.drawable.app_icon))
					 }
						  
					 val df = SimpleDateFormat ("EEE d MMM HH:mm:ss", Pos.app.locale)
					 clock.setText (df.format (Date ()))

					 for (icon in devices) {
						  
						  when (icon.device.deviceStatus ()) {
								
								DeviceStatus.Unknown -> {
									 
									 icon.text ().setTextColor (Color.parseColor (Pos.app.getString (R.color.pos_secondary)))
								}
								DeviceStatus.OnLine -> {
									 
									 icon.text ().setTextColor (Color.parseColor (Pos.app.getString (R.color.white)))
								}
								DeviceStatus.OffLine -> {
									 									 
									 icon.text ().setTextColor (Color.parseColor (Pos.app.getString (R.color.pos_danger)))
								}
								DeviceStatus.Invisible -> {
									 									 
									 icon.text ().setTextColor (Color.parseColor ("#00000000"))
								}
						  }
					 }
				}
		  }
					 
		  Thread (Runnable {
						  
						  var toggle = 0
						  while (true) {
								
								Thread.sleep (500)
								
								val message = Message.obtain ()
								message.what = toggle % 2
								handler.sendMessage (message)
								toggle ++
						  }
						  
		  }).start ()
	 }

	 override fun update () {

		  if (Pos.app.ticket.has ("customer")) {

	 			customerName?.setText (Customer (Pos.app.ticket.get ("customer")).display ()) 
		  }
		  else {

	 			customerName?.setText (Pos.app.getString ("search_customer"))
		  }
	 }
	 
	 override fun view (): View { return this }
	 override fun clear () { }
	 override fun message (message: String) { }
	 override fun message (message: Jar) { }
	 public fun customer (customer: String) {

	 	  customerName?.setText (customer)
	 }

	 inner class DeviceIcon (val device: Device): LinearLayout (Pos.app) {

		  val icon: PosIconText
		  
		  init {
								
				Pos.app.inflater.inflate (Pos.app.resourceID ("pos_app_device", "layout"), this)
				
				icon = findViewById (R.id.app_bar_icon)
				icon.setText (device.deviceIcon ())
            icon.setTextColor (Color.DKGRAY)

				icon.setOnClickListener {
					 
					 device.start (Jar ())
				}
		  }

		  fun text (): PosIconText { return icon }
	 }
}
