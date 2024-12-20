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
import cloud.multipos.pos.util.extensions.*

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.view.KeyEvent

open class PosCurrencyText (context: Context, attrs: AttributeSet): EditText (context, attrs) {

	 init { }

	 val sb = StringBuffer ()
	 
	 override fun onKeyUp (keyCode: Int, event: KeyEvent): Boolean {

		  when (event.keyCode) {

				KeyEvent.KEYCODE_0,
				KeyEvent.KEYCODE_1,
				KeyEvent.KEYCODE_2,
				KeyEvent.KEYCODE_3,
				KeyEvent.KEYCODE_4,
				KeyEvent.KEYCODE_5,
				KeyEvent.KEYCODE_6,
				KeyEvent.KEYCODE_7,
				KeyEvent.KEYCODE_8,
				KeyEvent.KEYCODE_9 -> {
				
					 sb.append (event.getNumber ())
				}
				KeyEvent.KEYCODE_DEL -> {

					 if (sb.length > 0) {
						  
						  sb.setLength (sb.length - 1)
					 }
				}
		  }
		  
		  Logger.d ("pos edit text... ${event}")

		  if (sb.length > 0) {
				
				var value = sb.toString ().toDouble ()
		  
		  
				setText ((value / 100.0).currency ())
		  }
		  
        return false;
    }

	 fun clear () {

		  sb.setLength (0)
		  setText ("")
	 }
}
