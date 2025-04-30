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
 
package cloud.multipos.pos.pricing

import java.util.ArrayList

import cloud.multipos.pos.Pos
import cloud.multipos.pos.R
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.views.ScalesView

class Metric (): Pricing (), InputListener {

	 lateinit var item: DefaultItem
	 lateinit var sizes: ArrayList <Jar>
	 
 	 override fun apply (i: DefaultItem): Boolean {

		  item = i

		  ScalesView (this,
						 item.item.getString ("item_desc"),
						 InputListener.DECIMAL,
						 item.item.get ("pricing").getInt ("decimal_places"))
		  
		  return true
	 }
	 
	 override fun accept (metric: Jar) {

		  var weight = 0.0
		  var amount = 0.0
		  var price = item.item.get ("pricing").getDouble ("price")

		  if (DeviceManager.scales != null) {

				val scales = DeviceManager.scales as Scales
				weight = scales.weight ()
		  }
		  else {
								
				var decimalPlaces = item.item.get ("pricing").getInt ("decimal_places")
				
				// get the weight from the operator
				
				weight = Pos.app.input.getDouble ()
				
				for (i in 0..<decimalPlaces) {
					 
					 weight /= 10
				}
		  }

		  amount = item.item.get ("pricing").getDouble ("price") * weight
		  
		  item.jar ().put ("merge_like_items", false)

		  if (weight > 0.0) {
				
				item.ticketItem ()
					 .put ("amount", amount)
					 .put ("metric", metric.getDouble ("value"))
				
				item.complete ()
		  }
	 }
}
