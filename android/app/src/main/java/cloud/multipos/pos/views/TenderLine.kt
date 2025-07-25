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
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.view.View
import android.graphics.Color
import android.graphics.Typeface;

class TenderLine (context: Context, tt: TicketTender, ticket: Ticket): LinearLayout (context) {

	 init {

		  var t: String
		  var color = Color.RED
		  	  
		  if (tt.getDouble ("balance") > 0.0) {
					 
				Pos.app.inflater.inflate (R.layout.ticket_tender_line_balance_due, this);
				
				t = Pos.app.getString ("total").uppercase () + " " +
				Pos.app.ticket.getDouble ("total").currency () +
				"\n" + Pos.app.getString ("paid").uppercase () + " " +
				tt.getString ("tender_type").uppercase () + " " +
				tt.getDouble ("tendered_amount").currency () +
				"\n" + Pos.app.getString ("balance_due").uppercase () + " " +
				tt.getDouble ("balance").currency ()
		  }
		  else {
				
				Pos.app.inflater.inflate (R.layout.ticket_tender_line, this);
				
				t = Pos.app.getString ("total").uppercase () + " " + tt.getDouble ("amount").currency ()

				when (tt.getString ("sub_tender_type")) {

					 "cash_drop" -> {

						  t += "\n" + Pos.app.getString ("bank").uppercase () + " " +
						  tt.getString ("tender_type").uppercase () + " " +
						  tt.getDouble ("tendered_amount").currency ()
					 }
					 else -> {
						  
						  t += "\n" + Pos.app.getString ("paid").uppercase () + " " +
						  tt.getString ("tender_type").uppercase () + " " +
						  tt.getDouble ("tendered_amount").currency ()
					 }
				}
				
				if (tt.getDouble ("returned_amount") != 0.0) {
					 
					 t += "\n" + Pos.app.getString ("change").uppercase () + " " +
					 tt.getDouble ("returned_amount").currency ()
				}

				when (Themed.theme) {
					 
					 Themes.Light -> {
						  
						  color = Color.BLACK
					 }
					 
					 Themes.Dark -> {
						  
						  color = Color.WHITE
					 }
				}
		  }
		  
		  var tender = this.findViewById (R.id.ticket_tender_line) as PosText
		  tender.setTypeface (null, Typeface.BOLD or Typeface.ITALIC)
		  tender.setTextColor (color)
		  tender.setText (t)
	 }
}
