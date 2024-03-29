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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.GridLayout
import android.widget.GridLayout.*
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.ContextThemeWrapper 
import android.graphics.Color

class PosPageMenu (context: Context,
						 attrs: AttributeSet,
						 menu: Jar): LinearLayout (context, attrs) {

	 var theme = "solid"
	 var themeWrapper = ContextThemeWrapper (Pos.app.activity, R.style.SolidButton)
	 val title = menu.getString ("name")
	 
	 init {

		  setBackgroundResource (R.color.transparent)

		  setLayoutParams (LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
		  // setLayoutParams (LinearLayout.LayoutParams (500, 500))
		  
		  when (Attrs.getString (attrs, "button_style", "solid")) {
						
				"solid" -> {
							 
					 themeWrapper = ContextThemeWrapper (Pos.app.activity, R.style.SolidButton)
				}
		  }
		  
		  val textSize = Attrs.getInt (attrs, "text_size", 14)

		  var grid = GridLayout (context, attrs)
		  val buttons = menu.getList ("buttons") as ArrayList <Jar>
		  
	 	  grid.setColumnCount (menu.getInt ("width"))
	 	  grid.setRowCount (menu.getInt ("height"))
		  grid.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
	
	 	  for (b in buttons) {
				
				when (b.getString ("class")) {

							  
					 "Null" -> {

		  				  grid.addView (EmptyButton (context))
					 }
					 
					 else -> {
						  
		  				  grid.addView (GridControlButton (context, b, textSize.toFloat (), attrs))
					 }
	 			}
		  }
		  
		  addView (grid)
	 }

	 inner class GridControlButton (context: Context, button: Jar, textSize: Float, attrs: AttributeSet): GridButton (context, button, textSize, attrs) {

		  var control: Control

		  init {

				if (button.getString ("color") == "btn-empty") {

					 button.put ("color", "empty");
				}
				
				setText (button.getString ("text"))
				
				control = Control.factory (button.getString ("class"))
				if (control != null) {
					 
					 setOnClickListener {

						  control.action (button.get ("jar"))
					 }
				}
		  }
	 }
	 

	 inner open class GridButton (context: Context, button: Jar, textSize: Float, attrs: AttributeSet): MaterialButton (themeWrapper) {
		  
		  init {
				
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				
				layoutParams.height = 0
				layoutParams.width = 0
				layoutParams.setMargins (3, -3, 3, -3)

				setTextSize (textSize)
	 			setGravity (Gravity.LEFT or Gravity.TOP)
				setPadding (Attrs.getInt (attrs, "text_padding_left", 5),
								Attrs.getInt (attrs, "text_padding_right", 10),
								0,
								0)
				
				when (theme) {

					 "solid" -> {

		 				  setBackgroundTintList (ContextCompat.getColorStateList (Pos.app.activity, Pos.app.resourceID (button.getString ("color"), "color")))
					 }
				}

				setLayoutParams (layoutParams)

		  }
	 }


	 inner class EmptyButton (context: Context): LinearLayout (context) {
		  
		  init {
				
	 			setBackgroundResource (R.color.transparent)
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.height = 0
				layoutParams.width = 0
				setLayoutParams (layoutParams)
		  }
	 }
}
