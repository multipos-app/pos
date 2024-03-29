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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.*
import cloud.multipos.pos.devices.*

class MetricItem (): DefaultItem (), InputListener {
	 
	 override fun controlAction (jar: Jar) {

		  jar (jar)

		  if (jar.getBoolean ("customer_required") && !Pos.app.ticket.has ("customer")) {
				
				CustomerSearchView (this)
				return
		  }
		  
		  val select = "select item_desc from items where sku = '" + jar.getString ("sku") + "'"

		  val itemResult = DbResult (select, Pos.app.db)
		  if (itemResult.fetchRow ()) {
				
		  		val item = itemResult.row ()
				
				if (DeviceManager.scales?.deviceStatus () == DeviceStatus.OnLine) {
					 
					 // ScalesDialog (this, item.getString ("item_desc"))
					 jar ().put ("entry_mode", "device")
				}
				else {

					 // numberDialog = NumberDialog (this, R.string.enter_weight, NumberDialog.DECIMAL, true)
					 // numberDialog.show ()		
					 jar ().put ("entry_mode", "keyed")
				}
		  }
	 }
	 
	 override fun accept (result: Jar) {

		  // if (jar ().getBoolean ("customer_required") && result.getString ("dialog") == "customer") {

		  // 		this.controlAction (jar ())
		  // 		return
		  // }

		  // if (result.getString ("dialog") == "number") {
		  
		  // 		numberDialog.dismiss ()
		  // }
		  
		  val amount = result.getDouble ("value")
		  jar ().put ("metric", Currency.round (amount))
		  super.controlAction (jar ())
		  updateDisplays ()
	 }
	 
	 override fun mergeItems (): Boolean {

		  return false
	 }
}
