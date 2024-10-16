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

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

class PosIconButton (context: Context,  attrs: AttributeSet): PosButton (context, attrs), PosTheme {

	 init {

		  theme = PosTheme.Theme.Day
		  setTypeface (Views.icons ());
		  // setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.day_fg));				
		  // setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.transparent));				
	 }
	 
	 override fun theme (theme: PosTheme.Theme) {
		  
		  when (theme) {

				PosTheme.Theme.Day -> {
					 
					 setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.transparent));				
					 setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.day_fg));				
				}

				PosTheme.Theme.Night -> {
					 
					 setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.transparent));				
					 setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.night_fg));				
				}
				else -> { }
		  }
	 }
}
