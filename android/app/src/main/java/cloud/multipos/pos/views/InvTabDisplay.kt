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
import cloud.multipos.pos.net.Post

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.View
import android.widget.TextView
import android.widget.Button

class InvTabDisplay (context: Context, attrs: AttributeSet, val scanDisplay: HandheldDisplay): ListDisplay (context, attrs), PosTabListener {
	 
	 val inventoryList = mutableListOf <Jar> ()
	 val invItems = mutableMapOf <String, Jar> ()
	 var upButton: Button
	 var downButton: Button
	 var sendButton: Button
	 var curr: String

	 val itemSkus = mutableListOf <String> ()

	 init {
		  
		  Pos.app.inflater.inflate (R.layout.inv_tab_display, this)
		  
		  upButton = findViewById (R.id.inv_up) as Button
		  downButton = findViewById (R.id.inv_down) as Button
		  sendButton = findViewById (R.id.inv_send) as Button
		  curr = ""

		  upButton.setOnClickListener {

				var invItem = invItems.get (curr)
				invItem?.put ("on_hand_count", invItem?.getInt ("on_hand_count") + 1)
				redraw ()
		  }

		  downButton.setOnClickListener {

				
				var invItem = invItems.get (curr)
				if ((invItem != null) && (invItem.getInt ("on_hand_count") > 0)) {
					 
					 invItem?.put ("on_hand_count", invItem?.getInt ("on_hand_count") - 1)
					 redraw ()
				}
		  }
		  
		  sendButton.setOnClickListener {

				val updates = mutableListOf <Jar> ()
				for ((key, value) in invItems) {
					 
					 updates.add (Jar ()
											.put ("item_id", value.getInt ("id"))
											.put ("on_hand_count", value.getInt ("on_hand_count")))
				}

				val jar = Jar ()
					 .put ("updates", updates)
				
				val post = 
				Post ("pos/inventory-updates")
					 .add (jar)
					 .exec (fun (result: Jar): Unit {
									
									itemSkus.clear ()
									invItems.clear ()
									redraw ()
									Pos.app.inventoryList.clear ()
					 })
				
				scanDisplay.clearScan ()
		  }
		  
		  singleSelect = true
	 }

	 /**
	  *
	  * tab Listener
	  *
	  */

	 override fun view (): View { return this }
	 
	 /**
	  *
	  * Scan Listener
	  *
	  */
	 	 
	 override fun onScan (scan: String) {
		  
		  if (!invItems.contains (scan)) {
				
				val p = Jar ()
					 .put ("sku", scan)
				
				val im = Item (p)
				
				if (im.exists ()) {
					 					 				 
					 itemSkus.add (scan)  // stop double scans
					 curr = scan
					 
					 Post ("pos/inventory/${im.item.getString ("id")}")
						  .exec (fun (p: Jar): Unit {
										 
										 if (p.getInt ("status") == 0) {
											  
											  im.item.put ("on_hand_quantity", p.getInt ("inv_count"))
											  im.item.put ("on_hand_count", p.getInt ("inv_count"))
											  im.item.put ("inv_item_id", p.getInt ("id"))
										 }
										 
										 list ().add (im.item)
										 redraw ()
										 invItems.put (scan, im.item);
						  })
				}
				else {

					 
				}
		  }
	 }

	 override fun select (position: Int) {

		  curr = itemSkus.get (position)
		  
		  var invItem = invItems.get (curr)
	 }

	 /**
	  *
	  * ListDislay implementation
	  *
	  */
	 
	 override fun list (): MutableList <Jar> {
		  
		  return Pos.app.inventoryList as MutableList <Jar>
	 }
	 
	 override fun layout (): String {

		  return "inv_tab_display"
	 }

	 override fun listEntry (pos: Int, view: View): View {
		  
		  if (list.get (pos) != null) {
				
				if (list.get (pos) is Jar) {

					 var p = list.get (pos)

					 return InventoryLine (context, p, pos, this)

				}
		  }
		  return view
	 }
}
