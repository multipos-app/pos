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
import cloud.multipos.pos.Pos
import cloud.multipos.pos.models.TicketItem
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Button
import android.view.View
import android.view.View.OnLongClickListener
import android.graphics.Color

open class ItemLine (context: Context, ti: TicketItem, pos: Int, val listDisplay: ListDisplay?): LinearLayout (context) {

	 var position: Int
	 lateinit var desc: PosText
	 lateinit var amount: PosText
	 lateinit var quantity: PosText
	 	 
	 init {
		  
		  position = pos
				
		  if (ti.getInt ("state") != TicketItem.VOID_ITEM) {

				Pos.app.inflater.inflate (itemLayout (), this);
				
				quantity = this.findViewById (R.id.ticket_item_line_quantity) as PosText
				desc = this.findViewById (R.id.ticket_item_line_desc) as PosText
				amount = this.findViewById (R.id.ticket_item_line_amount) as PosText
				
				quantity.setTypeface (Views.displayFont ())
				desc.setTypeface (Views.displayFont ())
				amount.setTypeface (Views.displayFont ())

				if (ti.extAmount () != 0.0) {
					 
					 quantity.setText (ti.getInt ("quantity").toString ())
				}
				else {

					 quantity.setText ("")
				}
				
				if (ti.has ("weight")) {
					 
					 desc.setText (ti.getString ("item_desc") + " " + String.format ("%.1f" + Pos.app.getString ("pounds"), ti.getDouble ("weight")));
				}
				else {
					 
					 desc.setText (ti.getString ("item_desc"))
				}
				
				amount.setText (Strings.currency (ti.extAmount (), false))
					 
				val layout = this.findViewById (R.id.ticket_item_layout) as LinearLayout
				layout.setOnClickListener {
					 
					 if (Pos.app.selectValues.indexOf (pos) >= 0) {
						  
						  Pos.app.selectValues.remove (pos);
					 }
					 else {
						  
	 					  listDisplay?.select (position);
					 }
					 
					 listDisplay?.redraw ();	 
				}
				
				if (pos % 2 == 1) {
					 
		  			 setBackgroundResource (Themed.oddBg)
				}
				else {
					 
		  			 setBackgroundResource (Themed.evenBg)
				}
				
				quantity.normal ()
				desc.normal ()
				amount.normal ()
				
				when (Themed.theme) {
				
					 Themes.Light -> {
					 
						  quantity.setTextColor (Color.BLACK)
						  desc.setTextColor (Color.BLACK)
						  amount.setTextColor (Color.BLACK)
					 }
				
					 Themes.Dark -> {

						  quantity.setTextColor (Color.WHITE)
						  desc.setTextColor (Color.WHITE)
						  amount.setTextColor (Color.WHITE)
					 }
				}
				
				if (ti.hasAddons ()) {

					 val addonLayout = findViewById (itemAddonLayout ()) as LinearLayout
					 for (tia in ti.addons) {
						  								
						  addonLayout.addView (ItemAddonLine (context, tia))
					 }
				}

				if (ti.hasLinks ()) {

					 val addonLayout = findViewById (itemLinkLayout ()) as LinearLayout
					 for (tia in ti.links) {

						  addonLayout.addView (ItemLinkLine (context, tia))
					 }
				}
				
				if (ti.getString ("data_capture").length > 0) {

					 val addonLayout = findViewById (itemLinkLayout ()) as LinearLayout
					 addonLayout.addView (ItemNoteLine (context, Jar (ti.getString ("data_capture"))))
				}

				// set select background

				if (Pos.app.selectValues.size > 0) {

					 listOf (Pos.app.selectValues).forEach () {
						  
						  for (p in it) {
								
								if (p == pos) {
									 
					 				 setBackgroundResource (Themed.selectBg)
									 quantity.bold ()
									 desc.bold ()
									 amount.bold ()
								}
						  }
					 }
				}
		  }

		  var note = this.findViewById (R.id.ticket_item_line_note) as Button?
		  note?.setOnClickListener {
											  					 
				// ItemAddNoteDialog (Pos.app.ticket.items.get (pos))
		  }
	 }
	 
	 open fun itemLayout (): Int {

		  if (Pos.app.config.has ("item_line_layout")) {

				return Pos.app.resourceID (Pos.app.config.getString ("item_line_layout"), "layout")
		  }
		  else {
				
				return R.layout.ticket_item_line
		  }
	 }
	 
	 open fun itemAddonLayout (): Int { return R.id.ticket_item_addon_layout }
	 open fun itemLinkLayout (): Int { return R.id.ticket_item_link_layout }
}
