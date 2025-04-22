/**
 * Copyright (C) 2025 multiPOS, LLC
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
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.util.SparseArray
import android.view.inputmethod.InputConnection

class PullTabsWeighView (context: Context, attrs: AttributeSet): PosSwipeLayout (context, attrs), OnClickListener {
	 
	 companion object {
		  
		  lateinit var instance: PullTabsWeighView
	 }
	 
	 init {
		  
		  instance = this
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.pull_tabs_weigh_layout, this)
	 }
	 
	 override fun onClick (view: View) {
		  
	 }
}
