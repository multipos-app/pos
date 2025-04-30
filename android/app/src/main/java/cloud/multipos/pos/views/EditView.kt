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
 
package cloud.multipos.pos.views

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*

import android.widget.Button
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.EditText
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
import android.text.Editable
import android.graphics.Color

abstract class EditView (): PosLayout (Pos.app, null) {

	 companion object {

		  lateinit var instance: EditView
	 }
	 
	 var layout: LinearLayout
	 var editLayout: LinearLayout
	 var actionsLayout: LinearLayout
	 var fields = mutableListOf <PosEditText> ()
	 var curr = 0
	 var pos = 0

	 init {

		  instance = this
		  layout = Pos.app.inflater.inflate (R.layout.edit_layout, Pos.app.keyboardView.inputView ()) as LinearLayout
		  editLayout = layout.findViewById (R.id.edit_fields) as LinearLayout
		  actionsLayout = layout.findViewById (R.id.edit_actions_layout) as LinearLayout
		  actions ()
	 }
	 
	 open fun onChange () { }

	 open fun actionsLayoutID (): Int { return R.layout.edit_actions_layout }

	 open fun actions () {
		  
		  var layout = Pos.app.inflater.inflate (actionsLayoutID (), actionsLayout) as LinearLayout
		  
		  var editComplete = layout.findViewById (R.id.edit_complete) as Button
		  editComplete?.setOnClickListener {

				complete ()
		  }

		  var editReset = layout.findViewById (R.id.edit_reset) as Button
		  editReset?.setOnClickListener {

				reset ()
		  }

		  var editCancel = layout.findViewById (R.id.edit_cancel) as Button
		  editCancel?.setOnClickListener {

				cancel ()
		  }
	 }
	 
	 fun posEditField (id: Int, layout: View, text: String): PosEditText {
		  
		  var field = layout.findViewById (id) as PosEditText
		  field.setText (text)
		  fields.add (field)
		  field.pos = pos ++
		  
		  field?.setOnClickListener {

				curr = field.pos
				for (f in fields) {
					 
					 f?.setBackgroundResource (R.drawable.gray_border)
				}
				fields [curr]?.setBackgroundResource (R.drawable.black_border)
				fields [curr]?.requestFocus ()
		  }
		  
		  return field
	 }

	 fun home () {
		  
		  curr = 0
		  if (fields.size > 0) {
				
				for (f in fields) {
					 
					 f?.setBackgroundResource (R.drawable.gray_border)
				}
				fields [curr]?.setBackgroundResource (R.drawable.black_border)
				fields [curr]?.requestFocus ()
		  }
	 }
	 
	 fun del () {

		  fields [curr]?.del ()
		  
		  // var pos = fields [curr]?.getSelectionStart ()!!
		  // if (pos > 0) {
				
		  // 		var text = fields [curr]?.getText ()?.delete (pos - 1, pos)?.toString ()
		  // 		fields [curr]?.setText (text)
		  //  	fields [curr]?.setSelection (pos - 1)
		  // }
	 }

	 fun space () {
		  
		  fields [curr]?.getText ()?.insert (fields [curr]?.getSelectionStart ()!!, " ")
	 }
	 
	 fun up () {

		  val valid = fields [curr]!!.isValid ()

		  if (!valid) {

				fields [curr]?.setTextColor (Color.RED)
				return
		  }
		  
		  if (fields.size > 0) {
				
				for (f in fields) {

					 f?.setBackgroundResource (R.drawable.gray_border)
					 f?.setTextColor (Color.BLACK)
				}
		  }

		  if (curr > 0) {
				
				curr --
		  }
		  else {

				curr = fields.size - 1
		  }
		  
		  fields [curr]?.setBackgroundResource (R.drawable.black_border)
		  fields [curr]?.requestFocus ()
	 }

	 fun down () {

		  val valid = fields [curr]!!.isValid ()

		  if (!valid) {

				fields [curr]?.setTextColor (Color.RED)
				return
		  }
		  
		  if (fields.size > 0) {
				
				for (f in fields) {

					 f?.setBackgroundResource (R.drawable.gray_border)
					 f?.setTextColor (Color.BLACK)
				}
		  }

		  if (curr == fields.size - 1) {
				
				curr = 0
		  }
		  else {

				curr ++
		  }
		  
		  fields [curr]?.setBackgroundResource (R.drawable.black_border)
		  fields [curr]?.requestFocus ()
	 }
	 
	 inner class SpinnerItem (val jar: Jar, val desc: String) {

		  override fun toString (): String {

				return jar.getString (desc).uppercase ()
		  }
	 }

	 // control methods
	 
	 open fun complete () {

		  complete ()
	 }

	 open fun reset () {

		  reset ()
	 }

	 open fun cancel () {

		  Pos.app.keyboardView.swipeLeft ()
		  Pos.app.input.clear ()
		  PosDisplays.clear () 
	 }
}
