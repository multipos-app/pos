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

import android.widget.LinearLayout

class Themed () {

	 init {
		  
		  Logger.d ("themed init... " + Pos.app.local.getString ("theme", "light"))
		  
		  if (Pos.app.local.has ("theme")) {

				when (Pos.app.local.getString ("theme", "light")) {

					 "light" -> { theme = Themes.Light }
					 "dark" -> { theme = Themes.Dark }
				}
		  }
		  else {

				theme = Themes.Light
				Pos.app.local.put ("theme", "light")
		  }
		  
		  when (theme) {
				
				Themes.Light -> {
					 
					 fg = R.color.light_fg
					 bg = R.color.light_bg
					 oddBg = R.color.light_odd_bg
					 evenBg = R.color.light_even_bg
					 selectBg = R.color.light_select_bg
				}
				
				Themes.Dark -> {
					 
					 fg = R.color.dark_fg
					 bg = R.color.dark_bg
					 oddBg = R.color.dark_odd_bg
					 evenBg = R.color.dark_even_bg
					 selectBg = R.color.dark_select_bg
				}
		  }
		  // var rootLayout = Pos.app.findViewById (R.id.root_layout) as LinearLayout?
		  // if (theme == Themes.Light) rootLayout?.setBackgroundResource (R.color.light_bg) else rootLayout?.setBackgroundResource (R.color.dark_bg)
	 }
	 
	 companion object {

		  var theme = Themes.Light

		  var fg = R.color.light_fg
		  var bg = R.color.light_bg
		  var oddBg = R.color.light_odd_bg
		  var evenBg = R.color.light_even_bg
		  var selectBg = R.color.light_select_bg
		  
		  val listeners = mutableListOf <ThemeListener> ()
		  fun add (listener: ThemeListener) { listeners.add (listener) }
		  
		  fun toggle () {
								
				theme = if (theme == Themes.Light) Themes.Dark else Themes.Light
				
				var rootLayout = Pos.app.findViewById (R.id.root_layout) as LinearLayout?
				if (theme == Themes.Light) rootLayout?.setBackgroundResource (R.color.light_bg) else rootLayout?.setBackgroundResource (R.color.dark_bg)

				when (theme) {
				
					 Themes.Light -> {
					 
						  fg = R.color.light_fg
						  bg = R.color.light_bg
						  oddBg = R.color.light_odd_bg
						  evenBg = R.color.light_even_bg
						  selectBg = R.color.light_select_bg
						  Pos.app.local.put ("theme", "light")
					 }
				
					 Themes.Dark -> {
					 
						  fg = R.color.dark_fg
						  bg = R.color.dark_bg
						  oddBg = R.color.dark_odd_bg
						  evenBg = R.color.dark_even_bg
						  selectBg = R.color.dark_select_bg
						  Pos.app.local.put ("theme", "dark")
					 }
				}
					 
				updateListeners ()
		  }

		  fun updateListeners () {
				
				for (listener in listeners) {
					 
					 listener.update (theme)
				}
		  }

		  fun start () {
				
				var rootLayout = Pos.app.findViewById (R.id.root_layout) as LinearLayout?
				if (theme == Themes.Light) rootLayout?.setBackgroundResource (R.color.light_bg) else rootLayout?.setBackgroundResource (R.color.dark_bg)
				updateListeners ()
		  }
	 }
}
