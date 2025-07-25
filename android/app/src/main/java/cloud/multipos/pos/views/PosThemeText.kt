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
import android.graphics.Typeface
import android.graphics.Color

open class PosThemeText (context: Context, attrs: AttributeSet): PosText (context, attrs) {

	 init {
		  
		  setTextColor (if (Themed.theme == Themes.Light) Color.BLACK else Color.WHITE)
	 }
}

