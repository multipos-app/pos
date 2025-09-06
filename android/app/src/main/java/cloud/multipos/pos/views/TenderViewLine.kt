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

class TenderViewLine (desc: String, amount: Double, layout: Int): LinearLayout (Pos.app) {

	 init {
				
		  Pos.app.inflater.inflate (layout, this)
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
		  
		  val d = findViewById (R.id.tender_detail_desc) as TextView
		  d.setText (desc)
		  
		  val a = findViewById (R.id.tender_detail_amount) as TextView
		  a.setText (amount.currency ())
		  
		  when (Themed.theme) {
				
				Themes.Light -> {
					 
					 a.setTextColor (Color.BLACK)
					 d.setTextColor (Color.BLACK)
				}
				
				Themes.Dark -> {
					 
					 a.setTextColor (Color.WHITE)
					 d.setTextColor (Color.WHITE)
				}
		  }
	 }
}
