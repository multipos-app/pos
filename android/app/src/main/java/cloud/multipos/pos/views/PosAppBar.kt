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
import cloud.multipos.pos.net.Post

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import androidx.core.content.ContextCompat
import android.graphics.Color;

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

import android.os.Handler
import android.os.Message
import android.view.KeyEvent

class PosAppBar (context: Context, attrs: AttributeSet): PosLayout (context, attrs), PosDisplay, ThemeListener {

	 // var p: ImageView
	 var clock: PosText
	 var customerLayout: LinearLayout? = null
	 var customerName: PosText? = null
	 var customerClear: PosIconText? = null
	 val devices = mutableListOf <DeviceIcon> ()
	 var themeIcon = ThemeIcon ()
	 var handler = AppHandler ()
	 val sb = StringBuffer () // for keyboard input

	 lateinit var rootLayout: LinearLayout
	 lateinit var updateThread: Thread
	 var running = true
	 
	 init {

		  Pos.app.posAppBar = this
		  
		  if (Pos.app.config.getBoolean ("customers")) {
				
				Pos.app.inflater.inflate (Pos.app.resourceID ("pos_app_bar_customer", "layout"), this)
		  
				customerLayout = findViewById (R.id.app_bar_customer_layout) as LinearLayout?
				customerName = findViewById (R.id.app_bar_customer_name) as PosText?
		  	 	customerName?.setText (Pos.app.getString ("search_customer"))

				customerLayout?.setOnClickListener {
					 
					 if (Pos.app.ticket.getInt ("customer_id") > 0) {
						  
						  CustomerEditView (Pos.app.ticket.getInt ("customer_id"))
					 }
					 else {
						  
						  CustomerSearchView ()
					 }
				}

				customerClear = findViewById (R.id.app_bar_customer_clear) as PosIconText
				customerClear?.setOnClickListener {

					 Pos.app.posAppBar.customer ("")
					 Pos.app.ticket.put ("customer_id", 0)
					 Pos.app.ticket.remove ("customer")
					 customerName?.setText (Pos.app.getString ("search_customer"))
					 customerClear?.visibility = View.INVISIBLE
				}
		  }
		  else {

				Pos.app.inflater.inflate (Pos.app.resourceID ("pos_app_bar", "layout"), this)
		  }
		  
		  rootLayout = findViewById (R.id.app_bar_root) as LinearLayout
		  
		  // time/date
		  
		  clock = findViewById (Pos.app.resourceID ("app_bar_clock", "id"))
		  clock?.setTypeface (Views.receiptFont ())
		  clock?.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.white))
		  
		  // devices
		  
		  addDevices ()

		  // press the mp icon to send pos info to server
		  
		  val posInfo = findViewById (R.id.pos_info) as View?
		  posInfo?.setOnClickListener {

				Logger.d ("pos info...")
				
				Post ("pos/pos-config")
					 .add (Jar ()
								  .put ("results", Pos.app.config))
					 .exec (fun (result: Jar): Unit { }
					 )
		  }

		  PosDisplays.add (this)
		  Themed.add (this)
		  onResume ()
	 }

	 /**
	  *
	  * handler, clock and device update thread
	  *
	  */
	 
	 inner class AppHandler (): Handler () {
				
		  override fun handleMessage (message: Message) {
				
				if (Pos.app.messages.size > 0) {
					 
					 var resource = if (message.what == 0) R.drawable.app_icon else R.drawable.app_icon_transparent
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
						  else -> { }
					 }
				}
		  }
	 }
	 
	 fun addDevices () {
		  
		  devices.clear ()
		  
		  val tray = findViewById (Pos.app.resourceID ("app_bar_tray", "id")) as LinearLayout
		  tray.removeAllViews ();

		  if (Pos.app.config.getBoolean ("themed")) {
				
				tray.addView (themeIcon, 0)
		  }
		  
		  for (device in Pos.app.devices) {
				
				var deviceIcon = DeviceIcon (device)
				devices.add (deviceIcon)
				tray.addView (deviceIcon, 0)
		  }
	 }

	 fun onPause () {

		  devices.clear ()
		  running = false
	 }

	 fun onResume () {
		  		  
		  running = true			 
		  Thread (Runnable {
						  
						  var toggle = 0
						  while (running) {
																								
								val message = Message.obtain ()
								message.what = toggle % 2
								handler.sendMessage (message)
								toggle ++
								
								Thread.sleep (1000)
						  }
		  }).start ()
	 }

	 /**
	  *
	  * PosDisplay impl
	  *
	  */
	 
	 override fun update () {
		  
		  if (Pos.app.ticket.getInt ("customer_id") > 0) {

				var customer = Customer ().select (Pos.app.ticket.getInt ("customer_id"))
	 			customerName?.setText (customer.display ())
		  }
		  else {

	 			clear ()
		  }
	 }
	 
	 override fun clear () {

		  if (!Pos.app.ticket.has ("customer")) {
				
				customerName?.setText (Pos.app.getString ("search_customer"))
				customerClear?.visibility = View.INVISIBLE
		  }
	 }
	 
	 override fun message (message: String) { }
	 override fun message (message: Jar) { }
	 public fun customer (customer: String) {

	 	  customerName?.setText (customer)
		  customerClear?.visibility = View.VISIBLE
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
	 
	 inner class ThemeIcon (): LinearLayout (Pos.app) {
		  
		  val icon: PosIconText

		  init {
		  
				Pos.app.inflater.inflate (Pos.app.resourceID ("pos_app_device", "layout"), this)
				
				icon = findViewById (R.id.app_bar_icon)
				icon.setText (if (Themed.theme == Themes.Light) R.string.fa_night else  R.string.fa_day)
				icon.setTextColor (Color.WHITE)
				
				icon.setOnClickListener {

					 Themed.toggle ()
					 icon.setText (if (Themed.theme == Themes.Light) R.string.fa_night else  R.string.fa_day)
				}
		  }
	 }

	 override fun update (theme: Themes) {
		  
		  customerName?.setTextColor (Color.WHITE)
		  customerClear?.setTextColor (Color.RED)
	 }
}
