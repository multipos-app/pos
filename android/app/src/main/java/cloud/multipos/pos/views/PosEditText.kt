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

import android.view.View
import android.view.View.OnFocusChangeListener
import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
import android.text.Editable

open class PosEditText (context: Context, attrs: AttributeSet): EditText (context, attrs), TextWatcher, PosTextValidator {

	 lateinit var changeListener: EditView
	 var pos = 0
	 
	 init {

		  setShowSoftInputOnFocus (false)
		  		  
		  setOnFocusChangeListener { _, hasFocus -> if (hasFocus) {
													
													val ic = onCreateInputConnection (EditorInfo ())
													KeyboardView.instance.inputConnection = onCreateInputConnection (EditorInfo ())
													setSelection (length ())
											  }
											  else { }
		  }
	 }
	 
	 open fun del () {
		  
		  var pos = getSelectionStart ()!!
		  if (pos > 0) {
				
				var text = getText ()?.delete (pos - 1, pos)?.toString ()
				setText (text)
		   	setSelection (pos - 1)
		  }
	 }
	 
	 override fun isValid (): Boolean { return true }
	 
	 fun setOnChange (listener: EditView) { changeListener = listener }
	 
	 override fun onTextChanged (text: CharSequence?, start: Int, before: Int, count: Int) {

		  if (this::changeListener.isInitialized) {

				changeListener.onChange ()
		  }
	 }
	 
	 override fun afterTextChanged (text: Editable?) { }
	 override fun beforeTextChanged (text: CharSequence?, start: Int, count: Int, after: Int) { }
}
