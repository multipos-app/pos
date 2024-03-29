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

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.devices.ScanListener
import cloud.multipos.pos.models.Item
import cloud.multipos.pos.devices.CameraScanner

import android.content.Context
import android.util.AttributeSet
import android.content.pm.ActivityInfo
import android.widget.LinearLayout
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import java.util.Date

class HandheldDisplay (context: Context, attrs: AttributeSet): PosLayout (context, attrs), ScanListener {
	 
	 var scanner: CameraScanner
	 var invTab: MaterialButton
	 var editTab: MaterialButton
	 var posTab: MaterialButton
	 var layout: LinearLayout
	 var itemDesc: TextView
	 var sku: TextView
	 var posTabListeners = mutableListOf <PosTabListener> ()
	 var tab = 0
	 var lastScan = Date ().getTime ()

	 init {
		  		  
		  Pos.app.inflater.inflate (R.layout.handheld_display, this)
		  
		  /**
			*
			* check camera permissions
			*
			*/
		  
		  scanner = findViewById (R.id.camera_scanner) as CameraScanner
		  scanner.scanListener (this)

		  sku = findViewById (R.id.scan_sku) as TextView
		  itemDesc = findViewById (R.id.scan_desc) as TextView

		  layout = findViewById (R.id.handheld_tabs) as LinearLayout

		  posTab = findViewById (R.id.pos_tab) as MaterialButton
		  invTab = findViewById (R.id.inv_tab) as MaterialButton
		  editTab = findViewById (R.id.item_edit_tab) as MaterialButton

		  posTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.white));				
		  invTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));				
		  editTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));				

		  posTab.setOnClickListener {

				clearScan ()
				tab = 0
				layout.removeAllViews ()
				layout.addView (posTabListeners.get (tab).view ())
				invTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));
				editTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));
				posTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.white));
		  }

		  invTab.setOnClickListener {

				clearScan ()
				tab = 1
				layout.removeAllViews ()
				layout.addView (posTabListeners.get (tab).view ())
				invTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.white));				
				editTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));				
				posTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));				
		  }

		  editTab.setOnClickListener {

				clearScan ()
				tab = 2
				layout.removeAllViews ()
				layout.addView (posTabListeners.get (tab).view ())
				invTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));
				editTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.white));
				posTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));
		  }

		  posTabListeners.add (PosTabDisplay (context, attrs))
		  posTabListeners.add (InvTabDisplay (context, attrs, this))
		  posTabListeners.add (ItemTabDisplay (context, attrs))
		  
		  // start with inventory
		  
		  layout.removeAllViews ()
		  layout.addView (posTabListeners.get (0).view ())
	 }

	 /**
	  *
	  * Scan listener
	  *
	  */
	 
	 override fun onScan (scan: String) {

		  sku.text = scan

		  var currTime = Date ().getTime ()

		  if (((currTime / 1000) - (lastScan / 1000)) < 2) {

				return;
		  }
		  
		  lastScan = Date ().getTime ()
  		  
		  val im = Item (Jar ().put ("sku", scan))
		  
		  if (im.exists ()) {
		  		
				itemDesc.text = im.item.getString ("item_desc")
		  }
		  else {

				clearScan ()
				
				// itemDesc.text = Pos.app.getString ("item_not_found")
				// layout.removeAllViews ()
				// tab = 1
				// layout.removeAllViews ()
				// layout.addView (posTabListeners.get (tab).view ())
				// invTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));
				// editTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.white));
				// posTab.setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.lt_gray));
		  }
		  
		  posTabListeners.get (tab).onScan (scan)
 		  scanner.clear ()
	 }

	 fun clearScan () {

		  sku.text = ""
		  itemDesc.text = ""
 		  scanner.clear ()
	 }
}
