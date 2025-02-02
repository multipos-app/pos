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

class KeyboardView (context: Context, attrs: AttributeSet): PosSwipeLayout (context, attrs), OnClickListener {

	 lateinit var inputView: LinearLayout
	 lateinit var inputConnection: InputConnection
	 
	 var keycase = KeyCase.Lower
	 val  keyValues = SparseArray <String> ()

	 companion object {
		  
		  lateinit var instance: KeyboardView
	 }

	 init {

		  instance = this
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.keyboard_layout, this)
		  inputView = findViewById (R.id.keyboard_view) as LinearLayout
		  loadKeys ()
	 }
	 
	 fun inputView (): LinearLayout { return inputView }
	 
	 fun push (layout: LinearLayout) {
		  
		  Pos.app.keyboardView.setVisibility (View.VISIBLE)
		  Pos.app.rootView.setVisibility (View.INVISIBLE)
	 }

	 override fun swipeLeft () {

		  Pos.app.keyboardView.setVisibility (View.INVISIBLE)
		  Pos.app.keyboardView.inputView ().removeAllViews ()
		  Pos.app.rootView.setVisibility (View.VISIBLE)
	 }
	 
	 fun loadKeys () {
		  
		  val keys = listOf (R.id.keyboard_1,
									R.id.keyboard_2,
									R.id.keyboard_3,
									R.id.keyboard_4,
									R.id.keyboard_5,
									R.id.keyboard_6,
									R.id.keyboard_7,
									R.id.keyboard_8,
									R.id.keyboard_9,
									R.id.keyboard_0,
									R.id.keyboard_a,
									R.id.keyboard_b,
									R.id.keyboard_c,
									R.id.keyboard_d,
									R.id.keyboard_e,
									R.id.keyboard_f,
									R.id.keyboard_g,
									R.id.keyboard_h,
									R.id.keyboard_i,
									R.id.keyboard_j,
									R.id.keyboard_k,
									R.id.keyboard_l,
									R.id.keyboard_m,
									R.id.keyboard_n,
									R.id.keyboard_o,
									R.id.keyboard_p,
									R.id.keyboard_q,
									R.id.keyboard_r,
									R.id.keyboard_s,
									R.id.keyboard_t,
									R.id.keyboard_u,
									R.id.keyboard_v,
									R.id.keyboard_w,
									R.id.keyboard_x,
									R.id.keyboard_y,
									R.id.keyboard_z,
									R.id.keyboard_00,
									R.id.keyboard_at,
									R.id.keyboard_period,
									R.id.keyboard_dot_com,
									R.id.keyboard_plus,
									R.id.keyboard_minus)

		  for (k in keys) {
				
				var button = findViewById (k) as PosButton
				button.setOnClickListener (this)
				keyValues.put (k, button.getText ().toString ())
		  }

		  var space = findViewById (R.id.keyboard_space) as PosButton?
		  space?.setOnClickListener {
				
				EditView.instance.space ()
		  }
		  
		  var shift = findViewById (R.id.keyboard_shift) as PosButton
		  shift.setOnClickListener {

				if (keycase == KeyCase.Lower) {
					 
					 keycase = KeyCase.Upper
				}
				else {

					 keycase = KeyCase.Lower
				}
				
				for (id in listOf (R.id.keyboard_a,
										 R.id.keyboard_b,
										 R.id.keyboard_c,
										 R.id.keyboard_d,
										 R.id.keyboard_e,
										 R.id.keyboard_f,
										 R.id.keyboard_g,
										 R.id.keyboard_h,
										 R.id.keyboard_i,
										 R.id.keyboard_j,
										 R.id.keyboard_k,
										 R.id.keyboard_l,
										 R.id.keyboard_m,
										 R.id.keyboard_n,
										 R.id.keyboard_o,
										 R.id.keyboard_p,
										 R.id.keyboard_q,
										 R.id.keyboard_r,
										 R.id.keyboard_s,
										 R.id.keyboard_t,
										 R.id.keyboard_u,
										 R.id.keyboard_v,
										 R.id.keyboard_w,
										 R.id.keyboard_x,
										 R.id.keyboard_y,
										 R.id.keyboard_z)) {
					 
			  		 var button = findViewById (id) as PosButton
					 button.setText (toggleAlpha (button.getText ().toString ()))
				}
		  }
		  
		  var del = findViewById (R.id.keyboard_del) as PosButton
		  del.setOnClickListener {
					 
				EditView.instance.del ()
		  }
		  
		  var next = findViewById (R.id.keyboard_enter) as PosButton
		  next.setOnClickListener {

				EditView.instance.next ()
		  }
	 }

	 override fun onClick (view: View) {
	 				
				var value = keyValues.get (view.getId ())
				inputConnection.commitText (value, 1)
	 }
	 
	 fun toggleAlpha (s: String): String {
		  
		  if (keycase == KeyCase.Lower) {

				return s.uppercase ()
		  }
		  else {

				return s.lowercase ()
		  }
	 }
	 
	 enum class KeyCase {
		  
		  Upper,
		  Lower
	 }
}
