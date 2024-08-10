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

open class PosControlSwipeLayout (context: Context, attrs: AttributeSet): PosSwipeLayout (context, attrs) {

	 val listeners = mutableListOf <SwipeListener> ()
	 val dialogs = mutableListOf <DialogView> ()
	 val MAX_DIALOGS = 10
	 
	 init {

		  Pos.app.controlLayout = this
	 }

	 fun push (dialog: DialogView) {
		  
		  dialogs.add (0, dialog)
		  swipeLeft ()

		  if (dialog is SwipeListener) {
				
				listeners.add (dialog as SwipeListener)
		  }

		  if (dialogs.size == MAX_DIALOGS) {

				dialogs.removeAt (MAX_DIALOGS - 1)
		  }
	 }
	 
	 fun pop (dialog: DialogView) {
		  
		  swipeRight ()

		  if (!dialog.sticky ()) {

				dialogs.remove (dialog)
		  }
		  
		  if (listeners.contains (dialog)) {
				
				listeners.remove (dialog)
		  }
	 }
	 
	 override fun swipeLeft () {
		  					 
		  if (dialogs.size > 0) {
				
				swiper = this
				dir = Gravity.END
				edge = Gravity.START
 				swipe (dialogs [0])
				notifyListeners (SwipeDir.Left)
		  }
		  else {

				Logger.w ("wha... no dialgos...")
		  }
	 }

	 override fun swipeRight () {
		  
		  dir = Gravity.START
		  edge = Gravity.END
		  swipe (Pos.app.controlHomeLayout)
		  notifyListeners (SwipeDir.Right)

		  if ((dialogs.size > 0) && !dialogs [0].sticky ()) {

				dialogs.removeAt (0);
		  }
	 }
	 
	 override fun swipeUp () {

		  notifyListeners (SwipeDir.Up)
	 }

	 override fun swipeDown () {

		  notifyListeners (SwipeDir.Down)
	 }

	 fun notifyListeners (dir: SwipeDir) {
		  
		  for (listener in listeners) {

				listener.onSwipe (dir)
		  }
	 }
}
