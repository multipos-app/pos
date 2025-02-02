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

open class PosCurrencyEditText (context: Context, attrs: AttributeSet): PosEditText (context, attrs), TextWatcher {

	 var ignore = false
	 
	 init {
		  
	     setRawInputType (InputType.TYPE_CLASS_TEXT)
        setTextIsSelectable (false)
		  setShowSoftInputOnFocus (false)
	 }
	 
	 override fun del () {

		  setText ("0.00")
	 }

	 override fun onTextChanged (text: CharSequence?, start: Int, before: Int, count: Int) {
													
		  if (ignore) {

				return
		  }
		  else {

				ignore = true

				if (text!!.length > 0) {
					 
					 val cleanString: String = text!!.replace ("""[$,.]""".toRegex (), "")					 
					 val formatted = (cleanString.toDouble () / 100.0).currency ()					 
					 setText (formatted)
					 setSelection (getText ().length)
				}
				
				ignore = false

		  }
	 }
	 
	 override fun afterTextChanged (text: Editable?) {
		  
		  setSelection (getText ().length)
	 }
	 
	 override fun beforeTextChanged (text: CharSequence?, start: Int, count: Int, after: Int) { }
}
