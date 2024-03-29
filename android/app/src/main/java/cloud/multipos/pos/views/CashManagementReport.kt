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

import android.content.Context
import android.widget.LinearLayout
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridLayout.*
import android.widget.TextView

class CashManagementReport (var sessionManager: SessionManager): PosLayout (Pos.app.activity) {

	 init {
		  		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  
		  var grid = GridLayout (Pos.app.activity)
	 	  grid.setColumnCount (1)
	 	  grid.setRowCount (sessionManager.totals.size)
		  grid.setLayoutParams (ViewGroup.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

		  for (p in sessionManager.totals) {

				grid.addView (ReportLine (p));
		  }

		  addView (grid)
	 }

	 inner class ReportLine (p: Jar): LinearLayout (Pos.app.activity) {

		  init {
					
				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
				
				when (p.getString ("type")) {

					 "total" -> {

								
								Pos.app.inflater.inflate (R.layout.cm_report_line, this)
								var quantity = findViewById (R.id.cm_report_line_qty) as TextView
								var desc = findViewById (R.id.cm_report_line_desc) as TextView
								var amount = findViewById (R.id.cm_report_line_amount) as TextView
								
								var qty = ""
								if (p.getInt ("quantity") > 0) {

									 qty = p.getInt ("quantity").toString ()
								}
						  		
								quantity.setText (qty)
								desc.setText (p.getString ("desc"))
								amount.setText (Strings.currency (p.getDouble ("amount"), false))
					 }
					 
					 "header" -> {
						  
						  Pos.app.inflater.inflate (R.layout.cm_report_section, this)
						  var desc = findViewById (R.id.cm_report_section_desc) as TextView
						  desc.setText (p.getString ("desc"))
					 }

					 "separator" -> {

						  Pos.app.inflater.inflate (R.layout.cm_report_separator, this)
					 }
				}
		  }
	 }
}
