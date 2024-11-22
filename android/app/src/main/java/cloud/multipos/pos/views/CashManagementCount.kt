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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.SessionManager
import cloud.multipos.pos.devices.*;

import android.content.Context
import android.widget.LinearLayout
import android.widget.ScrollView
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridLayout.*
import android.widget.TextView
import android.view.MotionEvent
import android.widget.Button

import java.util.ArrayList

class CashManagementCount (var sessionManager: SessionManager, var cashManagementView: CashManagementView): LinearLayout (Pos.app.activity), PosDisplay {

	 var denom: Int = 0
	 var denoms: ArrayList <CountLine> = ArrayList <CountLine> ()
	 var total: TextView
	 
	 var drawerStart: TextView
	 var drawerSales: TextView
	 var drawerTotal: TextView
	 var drawerCount: TextView
	 var drawerDiff: TextView
	 
	 var input: Int = 0
	 var control: Int = 0
	 
	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
	 	  Pos.app.inflater.inflate (R.layout.cash_management_count, this)
		  
		  var scroller = findViewById (R.id.drawer_count_list) as ScrollView
		  var grid = GridLayout (Pos.app.activity)
		  
	 	  grid.setColumnCount (1)
	 	  grid.setRowCount (sessionManager.totals.size)
		  grid.setLayoutParams (ViewGroup.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

		  var pos = 0
		  for (count in Pos.app.drawerCounts) {

		  		var c = CountLine (count, pos ++)
		  		grid.addView (c)
		  		denoms.add (c)
		  }
		  
		  drawerStart = findViewById (R.id.drawer_start) as TextView
		  drawerSales = findViewById (R.id.drawer_sales) as TextView
		  drawerTotal = findViewById (R.id.drawer_total) as TextView
		  drawerCount = findViewById (R.id.drawer_count) as TextView
		  drawerDiff = findViewById (R.id.drawer_diff) as TextView
		  
		  total = findViewById (R.id.drawer_count_total) as TextView

		  drawerStart.text = sessionManager.openAmount.currency ()
		  drawerSales.text = sessionManager.cashSales.currency ()
		  drawerTotal.text = (sessionManager.openAmount + sessionManager.cashSales).currency ()
		  
		  val next = findViewById (R.id.cm_next_denom) as Button
		  next.setOnClickListener {

				denoms.get (denom).deselect ()
				
				if (denom < (denoms.size - 1)) {

					 denom ++
				}
				else {

					 denom = 0
				}


				denoms.get (denom).select ()

				val view = denoms.get (denom)
				val vTop = view.getTop ()
				val vBottom = view.getBottom ()
				val sHeight = scroller.getBottom ()
				scroller.smoothScrollTo (0, ((vTop + vBottom - sHeight) / 2))
				Pos.app.input.clear ()
		  }

		  val reset = findViewById (R.id.cm_reset) as Button
		  reset.setOnClickListener {

				for (denom in denoms) {

					 denom.value = 0;
					 denom.count.put ("denom_count", 0)
					 denom.quantity.setText ("0")
					 denom.amount.setText ("0.00");
				}

				updateCounts ()
		  }
		  
		  val cont = findViewById (R.id.cm_continue) as Button
		  cont.setOnClickListener {
				
				sessionManager.count = true
				sessionManager.action (Jar ())  // re-create the report details
				PosDisplays.remove (this)
				cashManagementView.replaceView (CashManagementComplete (sessionManager, cashManagementView))
		  }

		  updateCounts ()
		  denoms.get (0).select ()
		  scroller.addView (grid)
		  
		  DeviceManager.printer.drawer ();
		  PosDisplays.add (this)
	 }

	 fun updateCounts () {

		  sessionManager.drawerCount = 0.0
		  
		  for (line in denoms) {

				sessionManager.drawerCount += line.count.get ("denom").getInt ("denom") * line.count.getInt ("denom_count")
		  }
		  
		  sessionManager.drawerCount = Currency.round (sessionManager.drawerCount / 100.0)
		  sessionManager.overShort = Currency.round ((sessionManager.cashSales + sessionManager.openAmount - sessionManager.drawerCount ()) * -1)

		  drawerCount.text = sessionManager.drawerCount ().currency ()
		  total.text = sessionManager.drawerCount ().currency ()
		  drawerDiff.text = sessionManager.overShort.currency ()
	 }
	 
	 inner class CountLine (var count: Jar, var pos: Int): LinearLayout (Pos.app.activity) {

		  public var quantity: TextView
		  public var amount: TextView
		  public var value: Int
		  
		  init {
				
				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
				setClickable (true)

				Pos.app.inflater.inflate (R.layout.drawer_denom, this)
				
				var desc = findViewById (R.id.drawer_denom_desc) as TextView
				
				quantity = findViewById (R.id.drawer_denom_quantity) as TextView
				amount = findViewById (R.id.drawer_denom_amount) as TextView

				var q = count.getInt ("denom_count")
				var a = count.get ("denom").getDouble ("denom")

				value = q
				
				desc.setText (count.get ("denom").getString ("denom_name"))
				quantity.setText (q.toString ())
				amount.setText (((a * q) / 100.0).currency ())
				value = 0
				bg ()
		  }

		  fun add (v: Int) {
				
				value = v

				count.put ("denom_count", value)
				
				quantity.text = value.toString ()
				amount.text = ((count.get ("denom").getDouble ("denom") * value) / 100f).currency ()
				
				updateCounts ()
		  }

		  fun del () {

				if (value > 0) {
					 
					 value /= 10
					 count.put ("denom_count", value)
					 
					 quantity.text = value.toString ()
					 amount.text = ((count.get ("denom").getDouble ("denom") * value) / 100f).currency ()
				}
				
				updateCounts ()
		  }
		  
		  fun bg () {
								
				if (pos % 2 == 0) {
					 
					 setBackgroundResource (R.color.lt_gray)
				}
				else {

					 setBackgroundResource (R.color.transparent)
				}
		  }
		  
		  fun select () {
				
				setBackgroundResource (R.drawable.count_select)
				elevation = 5f
				denom = pos
				value =  count.getInt ("denom_count")
		  }
		  
		  fun deselect () {
				
				bg ()
				elevation = 0f
		  }
		  
		  override fun onInterceptTouchEvent (ev: MotionEvent): Boolean {
				
				return when (ev.actionMasked) {
					 
					 MotionEvent.ACTION_DOWN -> {
						  
						  denoms.get (denom).deselect ()  // reset the old one
						  select ()
						  Pos.app.input.clear ()
						  true
					 }
					 else -> {
						  
						  true
					 }
				}
		  }
	 }

	 override fun update () {

		  denoms.get (denom).add (Pos.app.input.getInt ())
	 }
	 
	 override fun clear () { }
	 override fun message (message: String) { }
	 override fun message (message: Jar) { }
	 
}
