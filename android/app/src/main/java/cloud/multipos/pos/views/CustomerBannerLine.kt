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
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.graphics.Typeface

class CustomerBannerLine (context: Context, p: Jar): LinearLayout (context) {

	 init {
		  		  
		  when (p.getString ("size")) {

				"big" ->  Pos.app.inflater.inflate (R.layout.customer_banner_line_big, this)
				"normal" ->  Pos.app.inflater.inflate (R.layout.customer_banner_line_normal, this)
				"small" ->  Pos.app.inflater.inflate (R.layout.customer_banner_line_small, this)
		  }
				
		  var text = findViewById (R.id.customer_banner_content) as TextView

		  when (p.getString ("justify")) {

				"center" ->  text.gravity = Gravity.CENTER_HORIZONTAL
				"left" -> text.gravity = Gravity.START
				"right" -> text.gravity = Gravity.END
		  }
				
		  when (p.getString ("justify")) {

				"bold" -> text.setTypeface(text.getTypeface(), Typeface.BOLD)
				"bold" -> text.setTypeface(text.getTypeface(), Typeface.BOLD_ITALIC)
		  }
		  
		  text.text = p.getString ("text")
	 }
}
