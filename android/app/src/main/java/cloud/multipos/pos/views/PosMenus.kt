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
import cloud.multipos.pos.db.*

import android.content.Context
import android.view.View
import android.util.AttributeSet
import android.view.Gravity
import android.transition.Scene
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager

class PosMenus (context: Context, attrs: AttributeSet): PosLayout (context, attrs), PosMenuControl, SwipeListener  {

	 companion object {
		  
		  val menus = mutableMapOf <String, PosMenus> ()

		  fun set (name: String, index: Int) {

				Logger.d ("set menu... $name $index")
				
				val m = menus.get (name)
				m?.menu (index)
		  }
	 }
	 
	 var layouts = mutableListOf <PosLayout> ()
	 var tabs = mutableListOf <Jar> ()
	 var curr = 0
	 var name: String
	 
	 init {

		  Pos.app.posMenus.add (this)
		  name = getAttr ("menus", "")		  
		  update ()
		  menus.put (name, this)
		  Pos.app.controlLayout.listeners.add (this)
	 }
	 
	 fun buttonStyle (): String { return "solid" }
	 		  
	 override fun menu (name: String) {
	 }
	 
	 override fun menu (index: Int) {
		  
		  var dir = Gravity.END
		  var edge = Gravity.END
		  val end = layouts.get (index)
		  val scene = Scene (this, end)
		  val slide = Slide (dir)
		  slide.setMode (Fade.MODE_IN)
		  val transition = slide
		  transition.setDuration (250)
		  TransitionManager.go (scene, transition)
		  clearAnimation ()
		  curr = index
	 }
	 
	 override fun home () {

		  menu (0);
	 }

	 override fun update () {

		  removeAllViews ()
		  layouts = mutableListOf <PosLayout> ()
		  tabs = mutableListOf <Jar> ()
		  
		  val posMenus = Pos.app.config.get ("pos_menus").get (name)

		  if (posMenus.getBoolean ("tabs") && (posMenus.getList ("horizontal_menus").size > 1)) {
					 
					 for (menu in posMenus.getList ("horizontal_menus")) {

						  tabs.add (Jar ()
											 .put ("name", menu.getString ("name"))
											 .put ("color", "#555577"))
					 }
		  }

		  for (menu in posMenus.getList ("horizontal_menus")) {

				layouts.add (ControlsGridLayout (menu, this, tabs, attrs))
		  }
		  
		  addView (layouts.first ())
	 }
	 override fun onSwipe (swipeDir: SwipeDir) {
		  
		  var next = 0
		  var dir = Gravity.BOTTOM
		  var edge = Gravity.BOTTOM

		  when (swipeDir) {

				SwipeDir.Up -> {

					 Logger.d ("pos menu up... " + dir)
					 if (curr > 0) next = curr - 1
					 else next = layouts.size - 1
				}
				
				SwipeDir.Down -> {

					 Logger.d ("pos menu down... " + dir)
					 next = (curr + 1) % layouts.size
					 dir = Gravity.TOP
					 edge = Gravity.TOP
				}

				else -> { }
		  }

		  val end = layouts.get (next)
		  val scene = Scene (this, end)
		  val slide = Slide (dir)
		  slide.setMode (Fade.MODE_IN)
		  val transition = slide
		  transition.setDuration (150)
		  TransitionManager.go (scene, transition)
		  clearAnimation ()
		  curr = next
	 }
}
