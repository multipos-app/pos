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
import android.content.ComponentName
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
import android.os.Environment
import android.animation.ObjectAnimator

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf (Manifest.permission.CAMERA,
														  Manifest.permission.WRITE_EXTERNAL_STORAGE,
														  Manifest.permission.READ_EXTERNAL_STORAGE)

class Pos (): AppCompatActivity () {

	 lateinit var activity: Activity
	 lateinit var lastTicket: Ticket
	 
	 @JvmField var db: DB
	 
	 lateinit var rootView: RootView
	 lateinit var keyboardView: KeyboardView
	 lateinit var auxView: AuxView
	 
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
	 lateinit var posInit: Jar
	 lateinit var overlay: LinearLayout

	 // default locale
	 
	 var locale = Locale.US
	 var permissions = false
	 var serverLog = "quiet" // send logs to server?
	 var imageDir = ""

	 val inventoryList = mutableListOf <Device> ()
	 val posMenus = mutableListOf <PosMenus> ()

	 @JvmField val devices = mutableListOf <Device> ()

	 var receiptBuilder = ReceiptBuilder ()
	 var authInProgress = false;
	 var paused = false;

	 @JvmField var controls = Stack <Control> ()
	 @JvmField var businessUnits: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var configs: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var drawerCounts: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var selectValues: ArrayList <Int> = ArrayList <Int> ()
	 @JvmField var messages: ArrayList <Jar> = ArrayList <Jar> ()
	 @JvmField var loggedIn: Boolean = false
	 @JvmField var quantity = 1
	 
	 @JvmField var employee: Employee? = null
	 @JvmField var clerk: Employee? = null

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
				
				local = Local ()
				
				Logger.i ("pos create... ${local.getBoolean ("paused")}");

				if (local.getBoolean ("paused")) {

					 local.clear ("paused")
					 restart ()
				}

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

		  }
		  else {
				
				ActivityCompat.requestPermissions (this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
		  } 
	 }

    override fun onStart () {

        super.onStart ()
		  
		  Logger.i ("pos start...  ${local.getBoolean ("paused")}")
		  
		  if (local.getBoolean ("paused")) {

				local.clear ("paused")
				restart ()
		  }
	 }
	 
    override fun onResume () {

        super.onResume ()
		  
		  Logger.i ("pos resume... ${local.getBoolean ("paused")}");

		  if (authInProgress) {
				
				authInProgress = false
				return;
		  }

		  else {
				
				if (!db.ready ()) {

					 db.open ()
				}
		  
				// start services if a configuration is loaded
		  
				// if (config.getInt ("merchant_id") > 0) {
					 
				// 	 BackOffice.start ()
				// }
				
				BackOffice.start ()
				DeviceManager.start ("devices")

				start ()  // start the app

				if (this::posAppBar.isInitialized) {
					 
					 posAppBar.onResume ()
				}
				
				// BackOffice.download ()  // start downloads
		  }
	 }
	 
    override fun onRestart () {
		  
        super.onRestart ()
		  Logger.i ("pos start... ");
	 }
	 
	 override fun onPause () {

        super.onPause ()
		  
		  Logger.d ("pos pause...")
		  local.put ("paused", true)
		  
		  if (this::posAppBar.isInitialized) {

				posAppBar.onPause ()
				DeviceManager.onPause ()
				BackOffice.onPause ()
		  }
		  
	 }

    override fun onStop () {
		  
        super.onStop ()
		  Logger.d ("pos stop...")
	 }

	 override fun onDestroy () {
		  
        super.onDestroy ()
		  Logger.d ("pos destroy...")
	 }

	 override fun onActivityResult (requestCode: Int, resultCode: Int, intent: Intent?) {

		  when (requestCode) {

		  }
    }

    override fun onBackPressed () {
		  
        super.onBackPressed ()
	 }
	 	 
	 fun configInit (): Boolean {

		  return this::config.isInitialized
	 }
	 
	 override fun dispatchKeyEvent (event: KeyEvent): Boolean {

		  if (this::keyboardView.isInitialized) {
				
				if (keyboardView.showing) {

					 keyboardView.swipeLeft ()
				}
		  }
		  
        if (event.action == KeyEvent.ACTION_UP) {
								
				DeviceManager.scanner?.input (event)
        }

        return true
    }

	 fun start () {
		  
		  if (configInit () && config.ready ()) {
				
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
		  
				receiptBuilder = DefaultReceiptBuilder ()
				when (config.getString ("receipt_class")) {
					 
					 "laundry_receipt" -> 

						  receiptBuilder = LaundryReceiptBuilder ()
				}

				if (config.has ("image_buttons")) {

					 // create the image button dir if it does not exist
					 
					 imageDir = "${getFilesDir ().toString ()}/img"
					 
					 val file = File (imageDir);

					 if (!file.exists ()) {

						  file.mkdir ();
					 }
				}

				ticket ()  // load an initial ticket
				
				setContentView (R.layout.login_main)

				BackOffice.download ()  // start downloads

				if (config.has ("android_customer_display")) {
					 
					 CustomerFacingDisplay ()
				}
		  }
		  else {

				setContentView (R.layout.pos_register)
				overlay = findViewById (R.id.register_overlay) as LinearLayout
		  }
		  
		  Control.factory ("UploadLog").action (Jar ())
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
	 
	 fun ticket (): Pos {

		  ticket = Ticket (0, Ticket.OPEN)
		  clerk = null
		  selectValues.clear ()
		  return this
	 }
	 	 
	 fun login () {
		  		  
		  Themed () // init theme

		  Logger.i ("set layout... " + config.getString ("root_layout") + " buID: " + buID ())
		  
        setContentView (R.layout.root_layout)
		  
		  rootView = findViewById (R.id.root_layout_container) as RootView
		  keyboardView = findViewById (R.id.keyboard_container) as KeyboardView
		  auxView = findViewById (R.id.aux_container) as AuxView

		  Themed.start ()
		  PosDisplays.update ()
		  loggedIn = true
		  
		  // initialize dialogs
		  
		  DialogControl ()
		  
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
	 }

	 /**
	  * Things to do at logout
	  */
	 
	 fun logout () {
		  
		  loggedIn = false
		  DialogControl.clear ()
		  setContentView (R.layout.login_main);
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
	  * Add USE_CAMERA to BuildConfig (app/build.gradle) so initil permission request is optional
	  *
	  * buildConfigField "Boolean", "USE_CAMERA", "false" 
	  *
	  */

	 override fun onRequestPermissionsResult (requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		  		  
		  if (requestCode == REQUEST_CODE_PERMISSIONS) {
				
				if (!allPermissionsGranted ()) {
					 
					 FileUtils.uploadLog ()
					 finish ()
					 System.exit (1)
				}
		  }
	 }

	 /**
	  *
	  * check if all permissions are granted
	  *
	  */

	 private fun allPermissionsGranted (): Boolean {
		  
		  if (BuildConfig.USE_CAMERA) {

				for (permission in REQUIRED_PERMISSIONS) {
					 
					 if (ContextCompat.checkSelfPermission (this, permission) != PackageManager.PERMISSION_GRANTED) {
						  
					 	  return false
					 }
				}
		  }
		  
		  return true
	 }

	 fun restart () {

		  Logger.i ("pos forced restart...")
		  
		  val packageManager = this.getPackageManager ()
		  val intent = packageManager.getLaunchIntentForPackage (this.getPackageName ()) as Intent
		  val componentName = intent.getComponent() as ComponentName
		  val mainIntent = Intent.makeRestartActivityTask (componentName) as Intent
		  
		  mainIntent.setPackage (this.getPackageName ())
		  this.startActivity (mainIntent)
		  Runtime.getRuntime().exit (0)
	 }
}
