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
import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.View
import java.util.List
import android.view.Gravity
import android.widget.TextView
import android.graphics.Color

class TicketFooter (context: Context, attrs: AttributeSet): LinearLayout (context, attrs), PosDisplay, ThemeListener {
	 
	 var footerSubtotalDesc: TextView
	 var footerSubtotal: TextView
	 var footerTaxDesc: TextView
	 var footerTax: TextView
	 var footerTotalDesc: TextView
	 var footerTotal: TextView
	 
	 val textViews = mutableListOf <TextView> ()

	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.ticket_footer_layout, this)
	  
		  footerSubtotalDesc = findViewById (R.id.footer_subtotal_desc) as TextView	  
		  textViews.add (footerSubtotalDesc)
		  footerSubtotal = findViewById (R.id.footer_subtotal) as TextView
		  textViews.add (footerSubtotal)
		  footerTaxDesc = findViewById (R.id.footer_tax_desc) as TextView
		  textViews.add (footerTaxDesc)
		  footerTax = findViewById (R.id.footer_tax) as TextView
		  textViews.add (footerTax)
		  footerTotalDesc = findViewById (R.id.footer_total_desc) as TextView
		  textViews.add (footerTotalDesc)
		  footerTotal = findViewById (R.id.footer_total) as TextView
		  textViews.add (footerTotal)

		  Themed.add (this)
		  PosDisplays.add (this)
		  update ()
	 }

	 /**
	  *
	  * PosDisplay impl
	  *
	  */
	 
	 override fun update () {
		  
		  if (getVisibility () == View.INVISIBLE) {
				
				return
		  }

		  footerSubtotal?.setText (Strings.currency (Pos.app.ticket.getDouble ("sub_total"), false))
		  if (Pos.app.ticket.getDouble ("tax_total_inc") != 0.0) {
				
				footerTax?.setText (Strings.currency (Pos.app.ticket.getDouble ("tax_total_inc"), false))
		  }
		  else {
					 
				footerTax?.setText (Strings.currency (Pos.app.ticket.getDouble ("tax_total"), false))
		  }
		  				
		  footerTotal?.setText (Strings.currency (Pos.app.ticket.getDouble ("total"), false))
	 }
	 
	 override fun clear () {

		  if (!Pos.app.ticket.hasItems ()) {
				
				footerSubtotal?.setText (Strings.currency (0.0, false))
				footerTax?.setText (Strings.currency (0.0, false))
				footerTotal?.setText (Strings.currency (0.0, false))
		  }
	 }

	 // Theme impl
	 
	 override fun update (theme: Themes) {

		  Logger.d ("ticket footer theme update... ${theme}")
		  var color = Color.BLACK
		  if (theme == Themes.Dark) {
				
				color = Color.WHITE
		  }
		  
		  for (tv in textViews) {
				
				tv.setTextColor (color)
		  }
	 }
}
