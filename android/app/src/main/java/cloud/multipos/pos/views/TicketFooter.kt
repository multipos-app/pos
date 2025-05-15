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
import cloud.multipos.pos.util.extensions.*
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
	 
	 var footerSubtotalDesc: PosText
	 var footerSubtotal: PosText
	 var footerTaxDesc: PosText
	 var footerTax: PosText
	 var footerTotalDesc: PosText
	 var footerTotal: PosText

	 var next: PosIconText
	 var prev: PosIconText
	 var home: PosIconText
	 var suspend: PosIconText
	 var print: PosIconText
	 var customer: PosIconText
	 
	 val textViews = mutableListOf <PosText> ()

	 val navControl = Control.factory ("LoadTicket")
	 val printControl = Control.factory ("PrintReceipt")
	 
	 init {
		  
		  Pos.app.inflater.inflate (R.layout.ticket_footer_nav_layout, this)
	  
		  footerSubtotalDesc = findViewById (R.id.footer_subtotal_desc) as PosText
		  footerSubtotalDesc.italic ()
		  textViews.add (footerSubtotalDesc)
		  footerSubtotal = findViewById (R.id.footer_subtotal) as PosText
		  textViews.add (footerSubtotal)
		  
		  footerTaxDesc = findViewById (R.id.footer_tax_desc) as PosText
		  footerTaxDesc.italic ()
		  textViews.add (footerTaxDesc)
		  footerTax = findViewById (R.id.footer_tax) as PosText
		  textViews.add (footerTax)

		 
		  footerTotalDesc = findViewById (R.id.footer_total_desc) as PosText
		  footerTotalDesc.italic ()
		  textViews.add (footerTotalDesc)
		  footerTotal = findViewById (R.id.footer_total) as PosText
		  textViews.add (footerTotal)
		  
		  home = findViewById (R.id.ticket_nav_home) as PosIconText
		  textViews.add (home)
		  home?.setOnClickListener {

				navControl.action (Jar ().put ("dir", 0))
		  }
		  
		  prev = findViewById (R.id.ticket_nav_prev) as PosIconText
		  textViews.add (prev)
		  prev?.setOnClickListener {
	
				navControl.action (Jar ().put ("dir", -1))
		  }
		  
		  next = findViewById (R.id.ticket_nav_next) as PosIconText
		  textViews.add (next)
		  next?.setOnClickListener {

				navControl.action (Jar ().put ("dir", 1))
		  }
		  
		  suspend = findViewById (R.id.ticket_nav_suspend) as PosIconText
		  textViews.add (suspend)
		  suspend?.setOnClickListener {

				Control.factory ("Suspend").action (Jar ())
		  }
		  
		  print = findViewById (R.id.ticket_nav_print) as PosIconText
		  textViews.add (print)
		  print?.setOnClickListener {
				
				printControl.action (Jar ())
		  }
		  
		  customer = findViewById (R.id.ticket_nav_customer) as PosIconText
		  textViews.add (customer)
		  customer?.setOnClickListener {
				
		  		CustomerSearchView ()
		  }

		  Themed.add (this)
		  PosDisplays.add (this)
		  update ()
	 }
	 
	 // PosDisplay impl

	 override fun update () {
		  
		  if (getVisibility () == View.INVISIBLE) {
				
				return
		  }

		  footerSubtotal?.setText (Pos.app.ticket.getDouble ("sub_total").currency ())
		  if (Pos.app.ticket.getDouble ("tax_total_inc") != 0.0) {
				
				footerTax?.setText (Pos.app.ticket.getDouble ("tax_total_inc").currency ())
		  }
		  else {
					 
				footerTax?.setText (Pos.app.ticket.getDouble ("tax_total").currency ())
		  }
		  				
		  footerTotal?.setText (Pos.app.ticket.getDouble ("total").currency ())
	 }
	 
	 override fun clear () {

		  if (!Pos.app.ticket.hasItems ()) {
				
				footerSubtotal?.setText (0.0.currency ())
				footerTax?.setText (0.0.currency ())
				footerTotal?.setText (0.0.currency ())
		  }
	 }

	 // Theme impl
	 
	 override fun update (theme: Themes) {

		  var color = Color.BLACK
		  if (theme == Themes.Dark) {
				
				color = Color.WHITE
		  }
		  
		  for (tv in textViews) {
				
				tv.setTextColor (color)
		  }
	 }
}
