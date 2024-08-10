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
import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout;
import android.widget.Button
import android.view.View;

class PosKeypad (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {
	 
	 val enter = Enter ()
	 val quantity = Quantity ()
	 val clear = Clear ()
	 
	 companion object {

		  lateinit var instance: PosKeypad
	 }

	 init {

		  instance = this
		  
		  var keypadLayout = "pos_keypad_2"
		  
		  if (Pos.app.config.has ("keypad_layout")) {
				
		  		keypadLayout = Pos.app.config.getString ("keypad_layout")
		  }

		  Logger.d ("loading keypad... ${keypadLayout}")

		  Pos.app.inflater.inflate (Pos.app.resourceID (keypadLayout, "layout"), this)
		  
		  val e = findViewById (R.id.keypad_enter) as Button?
		  if (e != null) {
				
				e.setOnClickListener {
					 
					 if (Pos.app.input.sb ().length > 0) {
						  
						  val value = (Pos.app.input.getString ())
					 	  val jar = Jar ().put ("value", value)

						  if (Pos.app.controls.size > 0) {
								
								if (Pos.app.controls.peek () is InputListener) {

									 val control = Pos.app.controls.pop () as InputListener
									 control.accept (Jar ())
								}
						  }
						  else {

								enter.action (jar as Jar)
						  }

						  PosDisplays.enter ()
						  Pos.app.input.clear ()
					 }
				}
		  }
		  
		  val qty = findViewById (R.id.keypad_qty) as Button
		  if (qty != null) {
				
				qty.setOnClickListener {
					 
					 quantity.action (Jar ())
					 Pos.app.input.clear ()
				}
		  }

		  val qtyUp = findViewById (R.id.keypad_qty_up) as Button
		  if (qtyUp != null) {
				
				qtyUp.setOnClickListener {
					 
					 quantity.action (Jar ().put ("multiplier", 1))
					 Pos.app.input.clear ()
				}
		  }

		  val qtyDown = findViewById (R.id.keypad_qty_down) as Button
		  if (qtyDown != null) {
				
				qtyDown.setOnClickListener {
					 
					 quantity.action (Jar ().put ("multiplier", -1))
					 Pos.app.input.clear ()
				}
		  }

		  val buttons = arrayOf (R.id.keypad_button_0,
										 R.id.keypad_button_1,
										 R.id.keypad_button_2,
										 R.id.keypad_button_3,
										 R.id.keypad_button_4,
										 R.id.keypad_button_5,
										 R.id.keypad_button_6,
										 R.id.keypad_button_7,
										 R.id.keypad_button_8,
										 R.id.keypad_button_9)

		  var index = 0
		  for (button in buttons) {
				
		  		val digit = findViewById (button) as Button
				val value = index
				
		  		digit.setOnClickListener {
					 
					 if (Pos.app.input.sb ().length < Pos.app.input.MAX) {

						  Pos.app.input.append (value.toString ())
					 }
		 		}
				index ++
		  }

		  val del = findViewById (R.id.keypad_del) as Button?
		  if (del != null) {

				del.setTypeface (Views.icons ())
		  		del.setOnClickListener {
					 
					 Pos.app.input.del ()
					 if (Pos.app.input.length () == 0) {

						  PosDisplays.clear ()
					 }
				}
		  }
		  
		  val zeroZero = findViewById (R.id.keypad_zero_zero) as Button?
		  if (zeroZero != null) {

				zeroZero.setTypeface (Views.icons ())
		  		zeroZero.setOnClickListener {
					 
					 Pos.app.input.append ("00")
				}
		  }
	 }

	 fun clear () {

		  Pos.app.input.clear ()

		  // clear the app stack...

		  while (Pos.app.controls.size > 0) {

				Pos.app.controls.removeFirst ()
		  }
		  
		  clear.action (Jar ())
	 }
}

