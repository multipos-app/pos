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
import cloud.multipos.pos.models.TicketTender
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.view.View
import android.graphics.Color
import android.graphics.Typeface;

class TenderLine (context: Context, tt: TicketTender): LinearLayout (context) {

	 init {

		  Pos.app.inflater.inflate (R.layout.ticket_tender_line, this);

		  var tender = this.findViewById (R.id.ticket_tender_line) as PosText
		  tender.setTypeface(null, Typeface.BOLD or Typeface.ITALIC)

		  var t = "\n" + Pos.app.getString ("total").uppercase () + " " +
		  Strings.currency (tt.getDouble ("total"), false) +
		  "\n" + Pos.app.getString ("paid").uppercase () + " " +
		  tt.getString ("tender_type").uppercase () + " " +
		  Strings.currency (tt.getDouble ("tendered_amount"), false)

		  if (tt.getDouble ("returned_amount") != 0.0) {

				t += "\n" + Pos.app.getString ("change").uppercase () + " " +
				Strings.currency (tt.getDouble ("returned_amount"), false)
		  }
	 
		  tender.setText (t)

		  when (Themed.theme) {
				
				Themes.Light -> {
					 
					 tender.setTextColor (Color.BLACK)
				}
				
				Themes.Dark -> {
					 
					 tender.setTextColor (Color.WHITE)
				}
		  }
	 }
}
