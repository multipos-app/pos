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
 
package cloud.multipos.pos.models

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.db.*

class TicketItem (): Jar () {
	 
	 @JvmField val links = mutableListOf <TicketItem> ()
	 @JvmField val addons = mutableListOf <TicketItemAddon> ()
	 @JvmField val mods = mutableListOf <Jar> ()

	 fun extAmount (): Double {

		  return Currency.round (getDouble ("amount") * getInt ("quantity"))
	 }

	 fun amountWithoutTax (): Double {

		  if (Pos.app.config.taxMap () != null) {
				
				if (getInt ("tax_incl") == 0)  {

					 return getDouble ("amount")
				}
				else  {
					 
					 return getDouble ("amount") - tax (Pos.app.config.taxMap ().get (Integer.toString (getInt ("tax_group_id"))) as Jar)
				}
		  }
		  else {
				
				return getDouble ("amount")
		  }
	 }
	 
	 fun profit (): Double {
		  		  
		  return Currency.round (extAmount () - getDouble ("cost"))
	 }

	 fun percentProfit (): Double {
		  
		  return Currency.round (profit () / (amountWithoutTax () + addonAmount ())) * 100.0
	 }
	 
	 fun addonAmount (): Double {

		  var addonAmount = 0.0

		  if (hasAddons ()) {
				
				addons.forEach {
					 
					 addonAmount += it.getDouble ("addon_amount")
				}
		  }

		  return Currency.round (addonAmount)
	 }

	 fun tax (taxGroupID: Int): Double {

		  if (taxGroupID > 0) {
				
		  		var taxGroup = Pos.app.config.taxMap ().get (Integer.toString (taxGroupID))
										  
		  		if (taxGroup != null) {
												
		  			 return tax (taxGroup as Jar)
				}
		  }
		  return 0.0
	 }
	 
	 fun tax (taxGroup: Jar): Double {
		  
		  var tax = 0.0

		  if (getInt ("tax_exempt") == 1) {

				return 0.0
		  }
		  else if (getInt ("tax_incl") == 1) {

				tax = Currency.round ((((extAmount () + addonAmount ()) / (100 + taxGroup.getDouble ("rate"))) * 100.0) * taxGroup.getDouble ("rate") / 100.0)
		  }
		  else {

				tax = Currency.round ((extAmount () + addonAmount ()) * (taxGroup.getDouble ("rate") / 100.0))
		  }
		  
		  return tax
	 }

	 fun refund () {
        
		  put ("amount", getDouble ("amount") * -1.0)
		  put ("state", RETURN_ITEM)
		  
		  Pos.app.db ().update ("ticket_items", this)
		  
		  addons.forEach {
				
				Pos.app.db ().update ("ticket_item_addons", it)
		  }
	 }

	 fun note (note: String) {

		  put ("data_capture", Jar ().put ("note", note).toString ())
		  Pos.app.db ().exec ("update ticket_items set data_capture = '" + getString ("data_capture") + "' where id = " + getInt ("id"))
		  Pos.app.ticket.other.add (Jar ().put ("note", note))
	 }
	 
	 fun note (): String {

		  var note = ""
		  val dc = getString ("data_capture")
		  
		  if (dc.length > 0) {

				val dataCapture = Jar (dc)
				if (dataCapture.has ("note")) {

					 note = dataCapture.getString ("note")

				}
		  }
		  return note
	 }

	 fun removeAddons (addonTypes: List <Int>) {

		  val remove = mutableListOf <TicketItemAddon> ()

	 	  for (tia in addons) {

				for (addonType in addonTypes) {
					 
					 if (tia.getInt ("addon_type") == addonType) {

						  remove.add (tia)
					 }
				}
		  }

		  for (tia in remove) {
	
				addons.remove (tia)
		  }
	 }
	 
	 fun hasLinks (): Boolean { return links.size > 0 }
	 fun hasAddons (): Boolean { return addons.size > 0 }
	 fun hasMods (): Boolean { return mods.size > 0 }

	 companion object {
		  
		  const val STANDARD    = 0
		  const val VOID_ITEM   = 1
		  const val COMP_ITEM   = 2
		  const val RETURN_ITEM = 3
		  const val GIFT_CARD   = 4
		  const val WEIGHT_ITEM = 5
		  const val REDEEM      = 6
		  const val PAYOUT      = 7

		  const val DEPOSIT_LINK = 0
		  const val PACKAGE_LINK = 1

		  const val VOLUME = 1
		  const val WEIGHT = 2
		  const val LENGTH = 3
	 }
}

