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
 *
 */
 
package cloud.multipos.pos.pricing

import java.util.ArrayList

import cloud.multipos.pos.Pos
import cloud.multipos.pos.R
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.views.NumberInputView

//  prompt the cashier for the item amount

class Open (): Pricing (), InputListener {

	 lateinit var item: DefaultItem
	 
 	 override fun apply (i: DefaultItem): Boolean {

		  item = i
		  NumberInputView (this,
								 Pos.app.getString (R.string.enter_price),
								 item.item.getString ("item_desc"),
								 InputListener.CURRENCY,
								 0)
		  return true
	 }
	 
	 override fun accept (select: Jar) {
		  												
		  val amount = select.getDouble ("value") / 100.0
		  
		  item.jar ()
				.put ("merge_like_items", false)
				.put ("amount", select.getDouble ("value") / 100.0)
								
		  Pos.app.input.clear ()
		  item.complete ()
	 }
}
