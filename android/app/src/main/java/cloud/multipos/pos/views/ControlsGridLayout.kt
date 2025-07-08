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
import android.widget.Button
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.graphics.Color
import android.os.Build
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import java.io.File
import android.view.View.OnLongClickListener

/**
 *
 * See PosButton style for setup and padding
 *
 */

class ControlsGridLayout (val menu: Jar,
								  val posMenuControl: PosMenuControl,
								  val tabs: List <Jar>, 
								  attrs: AttributeSet): PosLayout (Pos.app.activity, attrs) {

	 var controls = mutableMapOf <String, Control> ()

	 init {
		  
		  val buttons = menu.getList ("buttons") as ArrayList <Jar>
		  
		  var grid = GridLayout (Pos.app.activity, attrs)
		  grid.setAlignmentMode (GridLayout.ALIGN_BOUNDS)		  
	 	  grid.setColumnCount (menu.getInt ("width"))
		  grid.setLayoutParams (ViewGroup.LayoutParams (LayoutParams.MATCH_PARENT,
																		LayoutParams.MATCH_PARENT))

		  if (tabs.size > 0) {
								
				var index = 0
				for (tab in tabs) {
					 
					 val t = Jar ()
						  .put ("text", tab.getString ("name"))
						  .put ("color", tab.getString ("color"))
						  .put ("params", Jar ()
										.put ("menu_index", index ++))
					 
		  			 grid.addView (ControlNavButton (t, posMenuControl))
				}

				while (index < menu.getInt ("width")) {

					 // fill out the line

		  			 grid.addView (EmptyButton ())
					 index ++
				}
		  }

	 	  for (button in buttons) {
				
				if (button.has ("class")) {
					 
					 when (button.getString ("class")) {
					 
						  "Null" -> {

		  						grid.addView (EmptyButton ())
						  }
						  
						  else -> {
								
		  						grid.addView (if (button.getBoolean ("image"))
												  ControlImageButton (button, controls)
												  else
												  ControlTextButton (button, controls))
						  }
					 }
				}
		  }
		  
		  addView (grid)
	 }
	 

	 // inner class NavigateButton (jar: Jar): LinearLayout (Pos.app) {

	 // 	  init {
				
	 // 			button?.setText (jar.getString ("text"))				
	 // 			button?.setTypeface (Views.buttonFont ())
	 // 			button?.setOnClickListener {
					 					 
	 // 				 posMenuControl.menu (jar.get ("params").getInt ("menu_index"))
	 // 			}
	 // 	  }
	 // }
	 	 
	 inner class EmptyButton (): LinearLayout (Pos.app.activity) {
		  
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
