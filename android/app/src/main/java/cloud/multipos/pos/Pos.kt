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
 
package cloud.multipos.pos

import cloud.multipos.pos.db.*
import cloud.multipos.pos.views.*
import cloud.multipos.pos.controls.*

import cloud.multipos.pos.net.*
import cloud.multipos.pos.services.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.receipts.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.surveillance.*

import android.os.Bundle
import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Intent
import android.content.BroadcastReceiver
import android.view.View
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.content.res.Resources
import android.os.Handler
import android.os.Message
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.view.Window
import android.view.WindowManager
import android.view.KeyEvent
import android.os.Build
import java.util.Locale;
import android.content.res.Configuration
import java.util.Stack
import java.io.*

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf (Manifest.permission.CAMERA)

class Pos (): AppCompatActivity () {

	 lateinit var activity: Activity
	 lateinit var lastTicket: Ticket
	 
	 @JvmField var db: DB
	 
	 lateinit var rootView: View
	 lateinit var config: Config
	 lateinit var bu: Jar
	 lateinit var local: Local
	 lateinit var input: Input
	 lateinit var posSession: PosSession
	 lateinit var totalsService: TotalsService
	 lateinit var ticket: Ticket
	 lateinit var inflater: LayoutInflater
	 lateinit var handler: Handler
    lateinit var posAppBar: PosAppBar
    lateinit var controlLayout: PosControlSwipeLayout
    lateinit var controlHomeLayout: PosControlHomeLayout
    lateinit var dialogView: DialogView
	 lateinit var posInit: Jar
	 lateinit var overlay: LinearLayout

	 // default locale
	 
	 var locale = Locale.US

	 val inventoryList = mutableListOf <Device> ()
	 val posMenus = mutableListOf <PosMenus> ()
	 val keyboardListeners = mutableListOf <KeyboardListener> ()
	 @JvmField val devices = mutableListOf <Device> ()

	 var receiptBuilder = ReceiptBuilder ()

	 @JvmField var controls = Stack <Control> ()
	 @JvmField var serverLog: Boolean = false
	 @JvmField var businessUnits: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var configs: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var drawerCounts: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var selectValues: ArrayList <Int> = ArrayList <Int> ()
	 @JvmField var messages: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var loggedIn: Boolean = false
	 @JvmField var quantity = 1
	 
	 @JvmField var employee: Employee? = null
	 @JvmField var clerk: Employee? = null
	 @JvmField var customer: Customer? = null

	 companion object {

		  lateinit var app: Pos
	 }

	 init {
		  
		  app = this
		  activity = this
		  db = DB (this)
	 }

    override fun onCreate (savedInstanceState: Bundle?)  {

        super.onCreate (savedInstanceState)

		  if (allPermissionsGranted ()) {
				
				requestWindowFeature (Window.FEATURE_NO_TITLE)
				getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
				inflater = LayoutInflater.from (activity)
				copyAssets (this)

				if (!db.ready ()) {

					 db.close ()
					 db = DB (this)
					 db.open ()
				}

				employee = Employee (Jar ())
				clerk = Employee (Jar ())
				
				handler = PosHandler ()
				config = Config ()
				input = Input ()
				local = Local ()
		  }
		  else {
				
				ActivityCompat.requestPermissions (this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
		  }
	 }

    override fun onStart () {

        super.onStart ()
	 }
	 
    override fun onResume () {
		  
        super.onResume ()

		  if (!db.ready ()) {

				db.open ()
		  }
		  
		  // start a download thread
		  
		  BackOffice.start ()

		  start ()  // start the app
	 }
	 
    override fun onRestart () {
		  
        super.onRestart ()
	 }
	 
	 override fun onPause () {
		  		  
        super.onPause ()
	 }

    override fun onStop () {
		  
        super.onStop ()
	 }

	 override fun onDestroy () {
		  
        super.onDestroy ()
	 }

	 override fun onActivityResult (requestCode: Int, resultCode: Int, intent: Intent?) {

		  when (requestCode) {

		  }
    }

    override fun onBackPressed () {
		  
        super.onBackPressed ()
	 }
	 
	 fun controlLayoutInit (): Boolean {

		  return this::controlLayout.isInitialized
	 }
	 
	 fun start () {
		  		  		  
		  if (config.ready ()) {
				
				// config.initialize ()

				// set the screen orientation
				
				if (config.has ("orientation")) {

					 when (config.getString ("orientation")) {
						  
						  "portrait" -> {
								
								setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						  }
						  
						  "reverse_portrait" -> {
								
								setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
						  }
						  
						  else -> {
								
								setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						  }
					 }	  
				}

				// start services
				
				posSession = PosSession ()
				totalsService = TotalsService.factory ()
		  
				// attach/start devices
		  
				DeviceManager.start ("devices")

				receiptBuilder = DefaultReceiptBuilder ()
				when (config.getString ("receipt_class")) {

					 "laundry_receipt" -> 
						  receiptBuilder = LaundryReceiptBuilder ()
				}
				
				ticket ()  // get a ticket
				setContentView (R.layout.login_main)

				BackOffice.download ()
		  }
		  else {

				setContentView (R.layout.pos_register)
				overlay = findViewById (R.id.register_overlay) as LinearLayout
		  }
	 }

	 fun posInit (result: Jar) {

		  if (result.getInt ("status") == 0) {

				local.put ("merchant_id", result.getInt ("merchant_id"))
				posInit = result
				setContentView (R.layout.pos_init)
				overlay = findViewById (R.id.pos_init_overlay) as LinearLayout
		  }
	 }

	 fun download () {

		  // start download

        setContentView (R.layout.download)
		  BackOffice.download ()
	 }
	 
	 fun stop () {

		  finish ()
		  System.exit (0)
	 }
	 
	 /**
	  * start a new ticket
	  */
	 
	 fun ticket () {

		  ticket = Ticket (0, Ticket.OPEN)
		  clerk = null
		  customer = null
		  selectValues.clear ()
	 }
	 	 
	 fun login () {
		  
		  Themed () // init theme

		  /**
			* Check if this config needs an open cash amount
			*/
		  
		  if (config.getBoolean ("prompt_open_amount")) {
				
		  		var totalsResults = DbResult (db ()
															 .find ("pos_session_totals")
															 .conditions (arrayOf ("pos_session_id = " + config.getInt ("pos_session_id"),
																						  "total_type = " + TotalsService.BANK))
															 .query (),
														db ())
				
				// if float amount change it to open amount
				
		  		if (totalsResults.fetchRow ()) {

					 var total = totalsResults.row ()
					 if (total.getString ("total_type_desc").equals ("float_amount")) {
						  
						  Control.factory ("Bank").action (Jar ()
																			.put ("type", "open_amount")
																			.put ("float_total", total))
					 }
		  		}
				else {
					 
					 Control.factory ("Bank").action (Jar ()
																	  .put ("type", "open_amount"))
				}
		  }

		  Logger.d ("set layout... " + config.getString ("root_layout") + " " + buID ())	  
        setContentView (resourceID (config.getString ("root_layout"), "layout"))
		  Themed.start ()
		  PosDisplays.update ()
		  loggedIn = true
	 }

	 /**
	  * Things to do at logout
	  */
	 
	 fun logout () {
		  
		  loggedIn = false
	 }

	 /*
	  *
	  * reload/update the meanus
	  *
	  */
	 
	 fun updateMenus () {

		  for (menu in posMenus) {

				menu.update ()
		  }
	 }
	 
	 fun homeMenus () {

		  for (menu in posMenus) {

				menu.home ()
		  }
	 }

	 fun getString (id: String): String {
		  
		  var resourceID = resourceID (id, "string")
		  
		  if (resourceID == 0) {
				
				return "*" + id + "*"
		  }
		  else {
				
				return activity.getString (resourceID)
		  }
	 }
	 
	 fun getInt (id: Int): Int {
				
		  return activity.getResources ().getInteger (id)
	 }
	 
	 fun resourceID (id: String, type: String): Int {

		  var resourceID = activity.getResources ().getIdentifier (id, type, activity.getPackageName ())
		  if (resourceID == 0) {

				Logger.stack ("MISSING RESOURCE, BAD JSON CONFIG?... " + id)
		  }
		  
		  return resourceID
	 }
	 
	 fun lowerKeyboard (view: View) {

        val imm = this.getSystemService (Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow (view.getWindowToken (), 0)
    }
	 
    fun rootView (): View { return rootView }
	 fun activity (): Activity { return activity }
	 fun db (): DB { return db }
	 
	 fun dbname (): String { return "m_" + local.getInt ("merchant_id", 0) }
	 fun posNo (): Int { return local.getInt ("id", 0) }
	 fun buID (): Int { return local.getInt ("business_unit_id", 0) }
	 fun posConfigID (): Int { return local.getInt ("pos_config_id", 0) }
	 fun bu (): Jar { return local.get ("business_unit") }

	 fun controls (control: Control) { }
 	 fun receiptBuilder (): ReceiptBuilder { return receiptBuilder }

	 fun warn (text: String) { }
	 fun toast (text: String) { }
	 
	 fun sendMessage (command: Int) {
		  
		  val m = Message.obtain ()
		  m.what = command
		  handler.sendMessage (m)
	 }
	 
	 fun sendMessage (command: Int, obj: Any) {
		  
		  val m = Message.obtain ()
		  m.what = command
		  m.obj = obj
		  handler.sendMessage (m)
	 }

	 override fun dispatchKeyEvent (event: KeyEvent): Boolean {

		  if (event.action == KeyEvent.ACTION_UP) {

				// Logger.d ("key event... " + event)

				DeviceManager.scanner?.input (event)
				
				for (kl in keyboardListeners) {
					 
					 if (event.keyCode == KeyEvent.KEYCODE_DEL) {
						  
						  kl.onBackspace ()
					 }
					 else {
						  
						  kl.onNum (event.getNumber ())
					 }
				}
		  }
		  
        return false
	 }
	 
	 fun copyAssets (context: Context) {
		  
	 	  val ass = context.getAssets ()
		  val files = ass.list ("");
		  val bufferSize = 1024
		  
		  files?.forEach {

				if (it.toString ().indexOf ("ttf") > 0) {
					 
					 val inputStream = ass.open (it)
					 val outputStream = FileOutputStream (File (app.filesDir, it))

					 try {
						  
						  inputStream.copyTo (outputStream, bufferSize)
					 }
					 finally {
						  
						  inputStream.close ()
						  outputStream.flush ()
						  outputStream.close ()
					 }
				}
		  }
	 }

	 	 /**
	  *
	  *
	  *
	  */

	 override fun onRequestPermissionsResult (requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		  
		  if (requestCode == REQUEST_CODE_PERMISSIONS) {
				
				if (allPermissionsGranted ()) {
					 
				}
				else {
					 
					 Logger.w ("Permissions not granted by the user...")
					 finish ()
				}
		  }
	 }

	 /**
	  *
	  *
	  *
	  */

	 private fun allPermissionsGranted (): Boolean {
		  
		  for (permission in REQUIRED_PERMISSIONS) {
				
				if (ContextCompat.checkSelfPermission (this, permission) != PackageManager.PERMISSION_GRANTED) {
					 
					 return false
				}
		  }
		  return true
	 }

}
