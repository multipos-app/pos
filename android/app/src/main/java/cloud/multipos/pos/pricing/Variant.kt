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
import cloud.multipos.pos.views.VariantView

class Variant (): Pricing (), InputListener {

	 lateinit var item: DefaultItem
	 lateinit var variants: ArrayList <Jar>
	 
 	 override fun apply (i: DefaultItem): Boolean {

		  item = i		  
		  variants = item.pricing ().getList ("variants")
		  VariantView (this, variants, Pos.app.getString (R.string.select_variant))
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
				.put ("amount", variants.get (select.getInt ("position")).getDouble ("price") * Pos.app.ticket!!.multiplier ())
				.put ("item_desc", variants.get (select.getInt ("position")).getString ("desc").uppercase () + " " + item.ticketItem ().getString ("item_desc"))

		  item.complete ()
	 }
}
