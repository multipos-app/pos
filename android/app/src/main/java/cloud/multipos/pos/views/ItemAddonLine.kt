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
import cloud.multipos.pos.models.TicketItemAddon
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.view.View
import android.graphics.Color

class ItemAddonLine (context: Context, tia: Jar): LinearLayout (context) {

	 init {

		  Pos.app.inflater.inflate (R.layout.ticket_item_addon_line, this);

		  var desc = this.findViewById (R.id.ticket_item_addon_line_desc) as PosText
		  var amount = this.findViewById (R.id.ticket_item_addon_line_amount) as PosText
		  
		  desc.setText (tia.getString ("addon_description"))
		  amount.setText (Strings.currency (tia.getDouble ("addon_amount"), false))

		  when (Themed.theme) {
				
				Themes.Light -> {
					 
					 desc.setTextColor (Color.BLACK)
					 amount.setTextColor (Color.BLACK)
				}
				
				Themes.Dark -> {
					 
					 desc.setTextColor (Color.WHITE)
					 amount.setTextColor (Color.WHITE)
				}
		  }
	 }
}
