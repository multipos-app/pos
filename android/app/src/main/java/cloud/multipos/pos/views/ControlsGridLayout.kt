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
		  grid.setLayoutParams (ViewGroup.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

		  if (tabs.size > 0) {
								
				var index = 0
				for (tab in tabs) {
					 
					 val t = Jar ()
						  .put ("text", tab.getString ("name"))
						  .put ("color", tab.getString ("color"))
						  .put ("params", Jar ()
										.put ("menu_index", index ++))
					 
		  			 grid.addView (NavigateButton (t))
				}

				while (index < menu.getInt ("width")) {

					 // fill out the line

		  			 grid.addView (EmptyButton ())
					 index ++
				}
		  }

	 	  for (b in buttons) {

				if (b.has ("class")) {
					 
					 when (b.getString ("class")) {
					 
						  "Null" -> {

		  						grid.addView (EmptyButton ())
						  }
						  
						  "Separator" -> {
								
		  						// grid.addView (Separator (b))
						  }
						  else -> {
								
		  						grid.addView (GridControlButton (b))
						  }
					 }
				}
		  }
		  
		  addView (grid)
	 }
	 
	 inner class GridControlButton (jar: Jar): GridButton (jar) {
		  
		  init {
				
				button.setText (jar.getString ("text"))
				button.setTypeface (Views.buttonFont ())

				var control: Control?  = controls.get (jar.getString ("class"))
				
				if (control == null) {

					 var controlClass = jar.getString ("class")

					 if (controlClass == "Item") {

						  controlClass = "DefaultItem"
					 }
					 
					 control = Control.factory (controlClass)
					 controls.put (jar.getString ("class"), Control.factory (controlClass))
				}
					 
				button.setOnClickListener {
						  
					 var c = controls.get (jar.getString ("class"))

					 if (c != null) {

						  c.action (jar.get ("params").put ("entry_mode", "control"))						  
					 }
				}
		  }
	 }
	 
	 inner class NavigateButton (jar: Jar): GridButton (jar) {

		  init {
				
				button.setText (jar.getString ("text"))				
				button.setTypeface (Views.buttonFont ())
				button.setOnClickListener {
					 					 
					 posMenuControl.menu (jar.get ("params").getInt ("menu_index"))
				}
		  }
	 }
	 	 
	 inner open class GridButton (jar: Jar): LinearLayout (Pos.app.activity) {

		  var layoutParams: GridLayout.LayoutParams
		  var button: MaterialButton
		  
		  init {

				Pos.app.inflater.inflate (R.layout.pos_control_button, this)  //  
				
				button = findViewById (R.id.control_button) as MaterialButton
				
				layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																	 GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.height = 0
				layoutParams.width = 0

				setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
				setLayoutParams (layoutParams)
				setPadding (0, 0, 0, 0)
								
				var color = "#eeeeee"
				if (jar.has ("color")) {

					 color = jar.getString ("color")
					 if (color.length == 0) {

						  Logger.w ("invalid button color... ${jar}")
						  color = "#eeeeee"
					 }
				}
				
				if (Themed.theme == Themes.Light) {

					 // lighten the color
					 
					 color = color.replace ("#", "#30")
				}

				var tmp = Color.WHITE
				
				try {
					 
					 tmp = Color.parseColor (color)
				}
				catch (e: Exception) {

					 Logger.w ("error parsing color... ${jar}")
					 color = "#eeeeee"
				}
				
				button.setTextColor (if (Themed.theme == Themes.Light) Color.BLACK else Color.WHITE)
					  						  
				val colorList = ColorStateList (arrayOf (intArrayOf(-android.R.attr.state_enabled),
																	  intArrayOf(android.R.attr.state_enabled)),
														  intArrayOf (Color.DKGRAY,
																		  Color.parseColor (color)))
		 		button.setBackgroundTintList (colorList)

				// add the stroke
						  
				val strokeList = ColorStateList (arrayOf (intArrayOf(-android.R.attr.state_enabled),
																		intArrayOf(android.R.attr.state_enabled)),
															intArrayOf (Color.BLACK,
																			Color.parseColor (Pos.app.getString (R.color.dk_gray))))

				button.strokeWidth = 3
				button.strokeColor = strokeList
				setClickable (true)
		  }
	 }

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
	 
	 inner open class Separator (jar: Jar): PosText (Pos.app.activity, attrs) {

		  init {
				
				var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, menu.getInt ("width").toFloat ()),
																		  GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
				layoutParams.height = 0
				layoutParams.width = 0
				setText (jar.getString ("text"))
				setTextColor (R.color.black)
				setLayoutParams (layoutParams)

		  }
	 }
}
