/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 *
 * https://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation
 *
 */
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.view.animation.TranslateAnimation

class DialogContainer (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {
	 
	 init {
		  
		  setVisibility (View.INVISIBLE)
	 }
}
