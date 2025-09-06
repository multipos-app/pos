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
 
package cloud.multipos.pos.addons

import java.util.ArrayList

import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.models.*

class MixAndMatchMarkdown (): Addon () {

	 
	 override fun apply () {

		  apply (ticketItem, addon)
	 }
	 
	 override fun apply (ticketItem: TicketItem, ia: Jar) {

		  addon = ia
		  
		  val tia: TicketItemAddon
		  
		  if (ticketItem.hasAddons ()) {

				tia = ticketItem.addons.get (0)
		  }
		  else {
				
				tia = TicketItemAddon ().copy (Jar ()
															  .put ("ticket_item_id", ticketItem.getInt ("id"))
															  .put ("addon_id", addon.getInt ("id"))
															  .put ("addon_amount", 0)
															  .put ("addon_quantity", 1)
															  .put ("addon_description", addon.getString ("description"))) as TicketItemAddon
				tia.update ()
				
				ticketItem.addons.add (tia)
				ticketItem.put ("addon_id", addon.getInt ("id"))
				
				tia.put ("addon", this);
				tia.put ("addon_jar", Jar (addon.getString ("jar")))
				
		  }
		  
		  var jar: Jar

		  if (addon.has ("jar")) {
				
				jar = Jar (addon.getString ("jar"))
		  }
		  else {
				
				return
		  }
		  
		  var count: Double = 0.0
		  var appliedMarkdown: Double = 0.0
		  var list = mutableListOf <TicketItemAddon> ()
		  
		  for (ti in Pos.app.ticket.items) {

				if (ti.has ("addon_id") && (ti.getInt ("addon_id") == addon.getInt ("id"))) {

					 when (ti.getInt ("state")) {
						  
						  TicketItem.RETURN_ITEM, TicketItem.STANDARD -> {
								
								count += ti.getDouble ("quantity")
								
								if (!ti.equals (ticketItem)) {
									 
									 appliedMarkdown += ti.addons.get (0).getDouble ("addon_amount")
								}

								var t = ti.addons.get (0)
								t.put ("item_quantity", ti.getInt ("quantity"))
								list.add (t)
						  }
					 }
				}
				
				if (ti.equals (ticketItem)) {

					 break;
				}
		  }
		  
		  if (count >= jar.getInt ("count")) {

				var markdown: Double
				
				when (jar.getString ("markdown_type")) {
					 
					 "amount" -> {

						  markdown =
								((count / jar.getDouble ("count")) * // # of times to apply markdown  
								  jar.getDouble ("amount") * -1f) -  // markdown amount
						  appliedMarkdown                       // previous markdowns
						  
		  				  tia.put ("addon_amount", markdown)
						  tia.update ()
					 }
					 "percent" -> {

						  markdown =
								((count / jar.getDouble ("count")) *                                           // # of times to apply markdown
						        ticketItem.getDouble ("amount") * (jar.getDouble ("percent") / 100f) * -1f) -  // percent of item to markdown
						  appliedMarkdown                                                                   // previous markdowns

		  				  tia.put ("addon_amount", markdown)
						  tia.update ()
					 }	  
					 "amount_all" -> {

						  for (tmp in list) {

								markdown = (jar.getDouble ("amount") * -1f) * tmp.getDouble ("item_quantity")
								tmp.put ("addon_amount", markdown)
								tmp.update ()
						  }
					 }
				}
		  }
	 }
}
