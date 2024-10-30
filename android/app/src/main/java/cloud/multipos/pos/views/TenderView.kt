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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.Tender
import cloud.multipos.pos.models.Ticket

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import android.widget.GridLayout
import android.widget.LinearLayout
import android.view.View;

class TenderView (val tender: Tender): DialogView (Pos.app.getString ("pay") + " " + tender.tenderType) {
	 
	 init {
		  
		  Pos.app.inflater.inflate (R.layout.tender_layout, dialogLayout)

		  val grid = findViewById (R.id.tender_detail) as GridLayout
		  
		  grid.addView (TenderLine (Pos.app.getString ("total"), tender.total, R.layout.tender_detail))

		  if (tender.paid > 0) {
				
		  		grid.addView (TenderLine (Pos.app.getString ("paid"), tender.paid, R.layout.tender_detail))
		  }
		  
		  grid.addView (TenderLine (Pos.app.getString ("tendered"), tender.tendered, R.layout.tender_detail_lg))

		  if (tender.returned > 0) {
				
				grid.addView (TenderLine (Pos.app.getString ("change_due"), tender.returned, R.layout.tender_detail))
		  }
		  
		  if (tender.balance > 0) {
				
		  		grid.addView (TenderLine (Pos.app.getString ("balance_due"), tender.balance, R.layout.tender_detail_lg))
		  } 
		  else if (tender.returned > 0) {
				
		  		grid.addView (TenderLine (Pos.app.getString ("change"), tender.returned, R.layout.tender_detail_lg))
		  }

		  Pos.app.controlLayout.push (this)
	 }

	 
	 override fun accept () {
		  
		  // if (tender.balance <= 0) {

		  // 		Pos.app.controlLayout.pop (this)
		  // }

		  Pos.app.controlLayout.pop (this)
		  
		  tender
				.confirmed (true)
				.action (tender.jar ())
	 }

	 class TenderLine (desc: String, amount: Double, layout: Int): LinearLayout (Pos.app) {

		  init {
				
				Pos.app.inflater.inflate (layout, this)
				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
		  
				val d = findViewById (R.id.tender_detail_desc) as TextView
				d.setText (desc)
		  
				val a = findViewById (R.id.tender_detail_amount) as TextView
				a.setText (Strings.currency (amount, false))
		  }
	 }
}
