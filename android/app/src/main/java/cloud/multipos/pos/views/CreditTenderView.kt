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

	 init {

		  Logger.x ("credit view... ${tender}")
		  
		  Pos.app.inflater.inflate (R.layout.credit_tender_layout, dialogLayout)

		  progress = findViewById (R.id.credit_dialog_progress) as ProgressBar
		  statusText = findViewById (R.id.credit_dialog_status) as TextView

		  var grid = findViewById (R.id.credit_dialog_detail) as GridLayout

		  var total = tender.total

		  grid.addView (TenderViewLine (Pos.app.getString ("sale_total"), total, R.layout.tender_detail))
		  grid.addView (TenderViewLine (Pos.app.getString ("tendered"), tender.tendered, R.layout.tender_detail))

		  if (tender.fees > 0.0) {

		  		grid.addView (TenderViewLine (Pos.app.getString ("service_fee"), tender.fees, R.layout.tender_detail))
		  }

		  if (tender.paid > 0.0) {
				
				grid.addView (TenderViewLine (Pos.app.getString ("total"), tender.total, R.layout.tender_detail))
		  		grid.addView (TenderViewLine (Pos.app.getString ("paid"), tender.paid, R.layout.tender_detail))
				total -= tender.paid
		  }

		  if (Math.abs (tender.balance) > 0.0) {
				
		  		grid.addView (TenderViewLine (Pos.app.getString ("balance_due"), Math.abs (tender.balance), R.layout.tender_detail))
		  }
		  
		  if (Math.abs (tender.balance) > 0.0) {
				
				grid.addView (TenderViewLine (Pos.app.getString ("change_due"), tender.returned, R.layout.tender_detail))
		  }
				
		  grid.addView (TenderViewLine (Pos.app.getString ("auth_amount"), tender.tendered + tender.fees, R.layout.tender_detail_lg))
		  
		  DialogControl.addView (this)

		  when (Themed.theme) {
				
				Themes.Light -> {
					 
					 statusText.setTextColor (Color.BLACK)
				}
				
				Themes.Dark -> {
					 
					 statusText.setTextColor (Color.WHITE)
				}
		  }
	 }

	 override fun accept () {

		  progress.setVisibility (View.VISIBLE)
		  statusText.setText (Pos.app.getString ("authorizing"))
		  
		  accept.setBackgroundColor (ContextCompat.getColor (Pos.app, R.color.pos_warn))
		  accept.setText (Pos.app.getString ("cancel"))

		  tender
				.payment ()
	 }

	 fun cancel (text: String) {

		  Logger.d ("tender view cancel... ${text}")
		  
		  accept.text = Pos.app.getString ("cancel")
		  statusText.text = text
		  progress.visibility = View.INVISIBLE
		  accept.setBackgroundColor (ContextCompat.getColor (Pos.app, R.color.pos_danger))

		  accept.setOnClickListener () {
				
				DialogControl.close ()
		  }
	 }

	 fun updateStatus (text: String) {

		  statusText.setText (text)
	 }

	 fun stop () {

		  tender.cancel ()
		  // Pos.app.controlLayout.swipeLeft ()
	 }
}
