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

open class InventoryLine (context: Context, item: Jar, pos: Int, val listDisplay: ListDisplay?): LinearLayout (context), PosTheme {

	 var theme: PosTheme.Theme = PosTheme.Theme.Day
	 var position: Int
	 var desc: PosText
	 var quantity: PosText
	 var count: PosText
	 
	 init {
		  
		  position = pos
				
		  Pos.app.inflater.inflate (R.layout.inventory_item_line, this);
				
		  desc = this.findViewById (R.id.inventory_item_line_desc) as PosText
		  quantity = this.findViewById (R.id.inventory_item_line_quantity) as PosText
		  count = this.findViewById (R.id.inventory_item_line_count) as PosText

		  desc.setText (item.getString ("item_desc"))
		  quantity.setText (item.getInt ("on_hand_quantity").toString ())
		  count.setText (item.getInt ("on_hand_count").toString ())

		  if (listDisplay != null) {
					 
				val layout = this.findViewById (R.id.inventory_item_layout) as LinearLayout
				layout.setOnClickListener {
						  
					 if (Pos.app.selectValues.indexOf (pos) >= 0) {
								
						  Pos.app.selectValues.remove (pos);
					 }
					 else {
								
	 					  listDisplay.select (position);
					 }
						  
					 listDisplay.redraw ();
				}

				if (pos % 2 == 1) {
					 
		  			 setBackgroundResource (R.color.odd_bg)
				}
				else {
					 
		  			 setBackgroundResource (R.color.even_bg)
				}
				
				quantity.normal ()
				desc.normal ()

				// set select background

				if (Pos.app.selectValues.size > 0) {

					 listOf (Pos.app.selectValues).forEach () {
						  
						  for (p in it) {
								
								if (p == pos) {
									 
					 				 setBackgroundResource (R.color.select_bg)
									 // quantity.bold ()
									 // desc.bold ()
								}
						  }
					 }
				}
		  }
	 }

	 override fun theme (theme: PosTheme.Theme) { }
	 override fun theme (): PosTheme.Theme { return theme }
}
