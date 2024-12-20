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

import java.util.ArrayList

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.NumberInputView

class ItemMarkdown (): TicketModifier (), InputListener {


	 override fun controlAction (jar: Jar) {

		  jar (jar)
		  
		  if (jar.has ("type") && (jar.getString ("type") == "fixed")) {

				accept (jar)
		  }
		  else {

				var title = ""
				var prompt = ""
				var type = InputListener.DECIMAL

				when (jar.getString ("value")) {

					 "currency" -> {
						  
						  title = Pos.app.getString ("amount_markdown");
						  prompt = Pos.app.getString ("enter_markdown_amount");
						  type = InputListener.CURRENCY
					 }
					 "percent" -> {
						  
						  title = Pos.app.getString ("percent_markdown");
						  prompt = Pos.app.getString ("enter_markdown_percent");
						  type = InputListener.DECIMAL
					 }
				}
				
				NumberInputView (this,
									  title,
									  prompt,
									  type,
									  0)
		  }
	 }
	 
	 override fun accept (result: Jar) {
		  
		  if (jar ().getString ("type") == "prompt") {

				when (jar ().getString ("value")) {

					 "currency" -> {
						  
						 jar ().put ("currency_value", result.getDouble ("value"))
					 }
					 "percent" -> {
						  
						  jar ().put ("percent_value", result.getDouble ("value"))
					 }
				}
		  }
		  
		  for (ti in Pos.app.ticket.selectItems ()) {

				val markdown = getMarkdown (ti, jar ());
						  
				val tia = TicketItemAddon ()

				tia
					 .put ("ticket_item_id", ti.getInt ("id"))
					 .put ("addon_amount", markdown)
					 .put ("addon_quantity", ti.getInt ("quantity"))
					 .put ("addon_description", jar ().getString ("receipt_text"));
				
				val id = Pos.app.db.insert ("ticket_item_addons", tia);
				
				tia
					 .put ("id", id);
		  
				ti.addons.add (tia)
		  }
		  
		  Pos.app.ticket.update ();
		  updateDisplays ();
	 }

	 fun getMarkdown (ti: TicketItem, jar: Jar): Double {
		  
		  var markdown = 0.0
		  
		  when (jar ().getString ("value")) {
		  
				"percent" -> {
				
					 markdown = -1.0 * (ti.getDouble ("amount") * (jar ().getDouble ("percent_value"))) / 100.0;
				}
				
				"currency" -> {
				
					 markdown = -1.0 * jar ().getDouble ("currency_value")
				}
		  }
		  
		  return Currency.round (markdown)
	 }
}
