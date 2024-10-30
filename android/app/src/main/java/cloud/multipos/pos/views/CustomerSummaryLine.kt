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
import cloud.multipos.pos.models.TicketItem
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import java.util.Optional
import android.view.View
import android.view.MotionEvent

class CustomerSummaryLine (context: Context, desc: String, amount: Double, pos: Int): LinearLayout (context) {

	 init {
		  
		  Pos.app.inflater.inflate (R.layout.customer_ticket_summary_line, this);
		  
		  var d = this.findViewById (R.id.customer_ticket_summary_line_desc) as PosText
		  var a = this.findViewById (R.id.customer_ticket_summary_line_amount) as PosText
		  
		  d.setText (desc)
		  a.setText (Strings.currency (amount, false))

		  if (pos % 2 == 1) {
				
		  		setBackgroundResource (R.color.light_odd_bg)
		  }
		  else {
				
		  		setBackgroundResource (R.color.light_even_bg)
		  }
	 }
}
