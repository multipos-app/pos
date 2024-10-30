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
import cloud.multipos.pos.views.SizeView

class Size (): Pricing (), InputListener {

	 lateinit var item: DefaultItem
	 lateinit var sizes: ArrayList <Jar>
	 
 	 override fun apply (i: DefaultItem): Boolean {

		  item = i

		  Logger.d ("size pricing... " + item)
		  
		  sizes = item.pricing ().getList ("sizes")
		  SizeView (this, sizes, Pos.app.getString (R.string.select_size))
		  return true
	 }
	 
	 override fun accept (select: Jar) {

		  if (Pos.app.controls.empty ()) {

				Logger.w ("empty stack in size pricing")
				return
		  }
		  
		  val item = Pos.app.controls.pop () as DefaultItem

		  item.jar ().put ("merge_like_items", false)
		  
		  item.ticketItem ()
				.put ("amount", sizes.get (select.getInt ("position")).getDouble ("price") * Pos.app.ticket!!.itemMultiplier ())
				.put ("item_desc", sizes.get (select.getInt ("position")).getString ("desc").toUpperCase () + " " + item.ticketItem ().getString ("item_desc"))

		  item.complete ()
	 }
}
