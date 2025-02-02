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
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.ConfirmView
import cloud.multipos.pos.views.ItemEditView

class ItemNotFound (): ItemUpdate () {

	 override fun controlAction (jar: Jar) {
		  		  
		  if (confirmed ()) {

				if (jar!!.has ("item")) {

					 val item = jar!!.get ("item")
					 
					 // update the item
					 
					 update (jar.get ("item"))
					 
					 // add the new item to the current ticket
					 
					 Control.factory ("DefaultItem").action (Jar ()
																				.put ("sku", jar.get ("item").getString ("sku")))
				}
				else {
					 
					 ItemEditView (this, Jar ()
											 .put ("sku", jar?.getString ("sku"))
											 .put ("item_desc", "bread")
											 .put ("price", 1.99)
											 .put ("cost", .78))
				}
		  }
		  else {
								
				ConfirmView (Pos.app.getString ("item_not_found_confirm"), this, jar!!)
		  }
	 }
}
