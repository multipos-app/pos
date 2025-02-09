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
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
import android.text.Editable
import java.text.NumberFormat
import android.view.View.OnFocusChangeListener

open class PosPhoneEditText (context: Context, attrs: AttributeSet): PosEditText (context, attrs), TextWatcher {

	 companion object {
		  
		val PHONE_RAW_LEN = 10
		val PHONE_LEN = 14
		val LAST_4 = 4
	 }
	 
	 var ignore = false
	 var formatted = false
	 var ph = ""
	 
	 init {
		  
	     setRawInputType (InputType.TYPE_CLASS_TEXT)
        setTextIsSelectable (true)
		  setShowSoftInputOnFocus (false)
	 }

	 override fun del () {
		  
		  ph = ""
		  setText (ph)
		  ignore = false
		  formatted = false
		  setSelection (text!!.length)
	 }
	 
	 override fun isValid (): Boolean {

		  return (text!!.length == PHONE_LEN) || (text!!.length == LAST_4) || (text!!.length == 0)
	 }
	 
	 override fun onTextChanged (text: CharSequence?, start: Int, before: Int, count: Int) {
		  
		  if (ignore) {

				return
		  }
		  else {

				ignore = true

				if (text!!.length > PHONE_LEN) {
				
					 setText (ph)
					 ignore = false
					 return
				}
				
				if (text!!.length == PHONE_RAW_LEN) {

					 ph = text!!.toString ().phone ()
					 
					 setText (ph)
					 formatted = true
					 EditView.instance.down ()
				}
				
				setSelection (text!!.length)
				
				ignore = false
		  }
	 }
	 
	 override fun afterTextChanged (text: Editable?) {  }
	 override fun beforeTextChanged (text: CharSequence?, start: Int, count: Int, after: Int) { }
}
