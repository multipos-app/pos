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
 */
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.Gravity

open class PosControlSwipeLayout (context: Context, attrs: AttributeSet?): PosSwipeLayout (context, attrs) {

	 lateinit var next: PosLayout
	 lateinit var prev: PosLayout
	 var canSwipe = false
	 
	 val listeners = mutableListOf <SwipeListener> ()
	 
	 init {

		  Pos.app.controlLayout = this
	 }

	 fun load (layout: PosLayout) {

		  next = layout
		  prev = next
		  canSwipe = true
		  swipeLeft ()
		  listeners.add (layout as SwipeListener)
	 }
	 
	 fun unload (layout: PosLayout) {
		  
		  if (listeners.contains (layout)) {
				
				PosDisplays.remove (layout as PosDisplay)
				listeners.remove (layout)
		  }
	 }
	 
	 override fun swipeLeft () {
		  
		  if (canSwipe) {
				
				swiper = this
				dir = Gravity.END
				edge = Gravity.START
 				swipe (next)
				notifyListeners (SwipeDir.Left)
		  }
	 }

	 override fun swipeRight () {
		  		  
		  swiper = this
		  dir = Gravity.START
		  edge = Gravity.END
		  swipe (Pos.app.controlHomeLayout)
		  notifyListeners (SwipeDir.Right)
	 }
	 
	 override fun swipeUp () { Logger.d ("pos control swipe up... ") }
	 override fun swipeDown () { Logger.d ("pos control swipe down... ") }

	 fun notifyListeners (dir: SwipeDir) {
		  
		  for (swipe in listeners) {
				
				swipe.onSwipe (dir)
		  }
	 }
	 
}
