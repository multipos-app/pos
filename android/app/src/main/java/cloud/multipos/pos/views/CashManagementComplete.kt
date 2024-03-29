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
import cloud.multipos.pos.controls.SessionManager
import cloud.multipos.pos.models.Ticket

import android.content.Context
import android.widget.LinearLayout
import android.widget.ScrollView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button

class CashManagementComplete (var sessionManager: SessionManager, var cashManagementView: CashManagementView): LinearLayout (Pos.app.activity), PosDisplay {
	 
	 var drawerStart: TextView
	 var drawerSales: TextView
	 var drawerTotal: TextView
	 var drawerCount: TextView
	 var drawerDiff: TextView
	 var floatInput: TextView

	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
	 	  Pos.app.inflater.inflate (R.layout.cash_management_complete, this)

		  drawerStart = findViewById (R.id.drawer_start) as TextView
		  drawerSales = findViewById (R.id.drawer_sales) as TextView
		  drawerTotal = findViewById (R.id.drawer_total) as TextView
		  drawerCount = findViewById (R.id.drawer_count) as TextView
		  drawerDiff = findViewById (R.id.drawer_diff) as TextView
		  floatInput = findViewById (R.id.float_input) as TextView
		  		  
		  drawerStart.text = Strings.currency (sessionManager.openAmount, false)
		  drawerSales.text = Strings.currency (sessionManager.cashSales, false)
		  drawerTotal.text = Strings.currency (sessionManager.openAmount + sessionManager.cashSales, false)
		  drawerCount.text = Strings.currency (sessionManager.drawerCount (), false)

		  Pos.app.input.clear ()
		  
		  var diff = sessionManager.cashSales + sessionManager.openAmount - sessionManager.drawerCount ()

		  if (diff > 0f) {

				diff *= -1
		  }

		  if (diff < 0) {
				
		  }
		  
		  drawerDiff.text = Strings.currency (diff, false)
		  
		  val prev = findViewById (R.id.cm_previous) as Button
		  prev.setOnClickListener {
				
				PosDisplays.remove (this)
				cashManagementView.replaceView (CashManagementCount (sessionManager, cashManagementView))

		  }
		  
		  val cont = findViewById (R.id.cm_continue) as Button
		  cont.setOnClickListener {

				sessionManager.action (Jar ())  // re-create the report details
				sessionManager.complete (Ticket.DRAWER_COUNT)
		  }
		  
		  PosDisplays.add (this)
	 }
	 
	 override fun update () {
		  
		  sessionManager.sessionFloat = Pos.app.input.getDouble () / 100.0
		  floatInput.setText (Strings.currency (sessionManager.sessionFloat, true))
	 }
	 
	 override fun view (): View { return this }
	 override fun clear () { }
	 override fun message (message: String) { }
	 override fun message (message: Jar) { }
}
