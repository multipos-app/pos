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
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.net.Post
import cloud.multipos.pos.devices.ScanListener

import android.view.Gravity
import android.widget.Button
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.LinearLayout
import java.util.Date

class InventoryView (jar: Jar): DialogView (""), ScanListener, SwipeListener {
	 
	 var scanner: CameraScanner
	 var scanItemDesc: TextView
	 var scanSku: TextView
	 var sku: TextView
	 var itemDesc: TextView
	 var serverCount: TextView
	 var invCount: TextView
	 var countUp: Button
	 var countDown: Button
	 var upload: Button
	 var count = 0
	 var itemID = 0
	 var scanCount = 0
	 var lastScan = Date ().getTime ()

	 init {
	 
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.inventory_layout, this)

		  scanner = findViewById (R.id.camera_scanner) as CameraScanner
		  scanner.scanListener (this)
		  
		  scanSku = findViewById (R.id.scan_sku) as TextView
		  scanItemDesc = findViewById (R.id.scan_item_desc) as TextView
		  sku = findViewById (R.id.sku) as TextView
		  itemDesc = findViewById (R.id.item_desc) as TextView
		  serverCount = findViewById (R.id.server_count) as TextView
		  invCount = findViewById (R.id.inv_count) as TextView

		  countUp = findViewById (R.id.count_up) as Button
		  countUp.setOnClickListener {

				count ++
				invCount.text = count.toString ()
		  }
		  
		  countDown = findViewById (R.id.count_down) as Button
		  countDown.setOnClickListener {

				if (count > 0) {
					 
					 count --
					 invCount.text = count.toString ()
				}
		  }
		  
		  upload = findViewById (R.id.inv_upload) as Button
		  upload.setOnClickListener {

				if (count > 0) {
					 
					 val updates = mutableListOf <Jar> ()
					 
					 updates.add (Jar ()
											.put ("item_id", itemID)
											.put ("business_unit_id", Pos.app.buID ())
											.put ("on_hand_count", count))
					 val jar = Jar ()
						  .put ("updates", updates)
					 
					 Post ("pos/inventory-updates")
						  .add (jar)
						  .exec (fun (result: Jar): Unit {
						  })
				}
		  }
		  
		  DialogControl.addView (this)
	 }

	 	 /**
	  *
	  * Scan listener
	  *
	  */
	 
	 override fun onScan (scan: String) {
		  
		  scanSku.text = scan
		  sku.text = scan

		  // stop multiple scans

		  if (scanCount < 10) {

				scanCount ++
				return
		  }
		  scanCount = 0
		    		  
		  val im = Item (Jar ().put ("sku", scan))
		  
		  if (im.exists ()) {
				
				itemID = im.getInt ("id")
				scanItemDesc.text = im.item.getString ("item_desc")
				itemDesc.text = im.item.getString ("item_desc")
				
				Post ("pos/inventory-count")
					 .add (Jar ()
								  .put ("dbname", Pos.app.dbname ())
								  .put ("business_unit_id", Pos.app.buID ())
							 	  .put ("item_id", itemID))
					 .exec (fun (result: Jar): Unit {
									
									if (result.getInt ("status") == 0) {
										 
										 serverCount.text = result.getInt ("inv_count").toString ()
									}
					 })
		  }
		  else {

				itemDesc.text = Pos.app.getString (R.string.item_not_found)
				clearScan ()
		  }
		  
 		  scanner.clear ()
	 }

	 fun clearScan () {

		  scanSku.text = ""
		  scanItemDesc.text = ""
 		  scanner.clear ()
	 }

	 override fun onSwipe (dir: SwipeDir) {
		  
		  when (dir) {

				SwipeDir.Right -> {
					 
					 scanner.stop ()
				}

				else -> { }
		  }
	 }
}
