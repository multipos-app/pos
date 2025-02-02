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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.controls.Tender

import cloud.multipos.pos.R
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import android.graphics.Color

class CreditTenderView (val tender: Tender): DialogView (Pos.app.getString (tender.tenderType ())) {

	 var progress: ProgressBar
	 var statusText: TextView
	 var inProgress: Boolean = false

	 init {
		  
		  Pos.app.inflater.inflate (R.layout.credit_tender_layout, dialogLayout)

		  progress = findViewById (R.id.credit_dialog_progress) as ProgressBar
		  statusText = findViewById (R.id.credit_dialog_status) as TextView

		  var grid = findViewById (R.id.credit_dialog_detail) as GridLayout

		  var total = tender.total ()

		  grid.addView (TenderLine (Pos.app.getString ("sale_total"), total, R.layout.tender_dialog_detail))

		  grid.addView (TenderLine (Pos.app.getString ("tendered"), tender.tendered, R.layout.tender_dialog_detail))

		  if (tender.fees > 0.0) {

		  		grid.addView (TenderLine (Pos.app.getString ("service_fee"), tender.fees (), R.layout.tender_dialog_detail))
		  }

		  if (tender.paid > 0.0) {
				
				grid.addView (TenderLine (Pos.app.getString ("total"), tender.total, R.layout.tender_dialog_detail))
		  		grid.addView (TenderLine (Pos.app.getString ("paid"), tender.paid, R.layout.tender_dialog_detail))
				total -= tender.paid
		  }

		  if (Math.abs (tender.balance) > 0.0) {
				
		  		grid.addView (TenderLine (Pos.app.getString ("balance_due"), Math.abs (tender.balance), R.layout.tender_dialog_detail))
		  }
		  
		  if (Math.abs (tender.balance) > 0.0) {
				
				grid.addView (TenderLine (Pos.app.getString ("change_due"), tender.returned, R.layout.tender_dialog_detail))
		  }
				
		  grid.addView (TenderLine (Pos.app.getString ("auth_amount"), tender.tendered + tender.fees (), R.layout.tender_dialog_detail_lg))
		  
		  Pos.app.controlLayout.push (this)
	 }

	 override fun accept () {
		  
		  tender
				.confirmed (true)
				.action (tender.jar)
		  
		  if (Math.abs (tender.balance) == 0.0) {
				
				// PosDisplays.home ()
				// PosMenus.home ()
		  }
		  
		  inProgress = true
		  auth ()
	 }
	 
	 override fun onSwipe (dir: SwipeDir) {
		  
		  // when (dir) {

		  // 		SwipeDir.Right -> {		  
		  // 			 tender.cancel ()
		  // 		}
		  // }
	 }

	 fun auth () {
		  
		  progress.setVisibility (View.VISIBLE)
		  statusText.setText (Pos.app.getString ("authorizing"))
	 }
	 
	 fun cancel (text: String) {
		  
		  // accept.text = Pos.app.getString ("cancel")
		  // statusText.text = text
		  // progress.visibility = View.INVISIBLE
		  // accept.setText (Pos.app.getString ("cancel"))

		  // accept.setOnClickListener () {
				
		  // 		Pos.app.controlLayout.swipeRight ()
		  // }
	 }

	 fun updateStatus (text: String) {

		  statusText.setText (text)
	 }

	 fun stop () {

		  tender.cancel ()
		  Pos.app.controlLayout.swipeLeft ()
	 }

	 inner class TenderLine (desc: String, amount: Double, layout: Int): LinearLayout (Pos.app.activity) {

		  init {
				
				Pos.app.inflater.inflate (layout, this)
				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

				var d = findViewById (R.id.tender_dialog_detail_desc) as PosText
				d.setText (desc)
				var a = findViewById (R.id.tender_dialog_detail_amount) as PosText
				a.setText (amount.currency ())
		  }
	 }
}
