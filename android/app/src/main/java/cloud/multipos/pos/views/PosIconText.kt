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
 
package cloud.multipos.pos.views;

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.Logger

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.view.KeyEvent

open class PosIconText (context: Context, attrs: AttributeSet): TextView (context, attrs) {

	 init {
		  
		  setTypeface (Views.icons ());
		  // setTextColor (R.color.night_fg)
		  // setTextColor (ContextCompat.getColor (Pos.app.activity, R.color.night_fg))
	 }

	 // override fun theme (theme: PosTheme.Theme) {
		  
		  // when (theme) {

				// PosTheme.Theme.Day -> {
					 
				// 	 setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.day_bg));				
				// 	 setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.night_fg));				
				// }

				// PosTheme.Theme.Night -> {
					 
				// 	 setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, R.color.night_bg));				
				// 	 setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.night_fg));				
				// }

				// else -> { }
		  // }
	 // }
	 
	 // override fun theme (): PosTheme.Theme { return theme }

	 override fun onKeyUp (keyCode: Int, event: KeyEvent): Boolean {

		  // intercept KeyEvent action=ACTION_UP
		  
		  return false
	 }
}
