/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.graphics.Color

abstract class DialogView (title: String): PosLayout (Pos.app, null), PosDisplay, SwipeListener {

	 protected var dialogLayout: LinearLayout
	 lateinit var dialogActions: LinearLayout
	 var fg = Color.BLACK
	 
	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (layout (), this)

		  dialogLayout = findViewById (R.id.dialog_layout) as LinearLayout
		  if (Themed.theme == Themes.Light) {

				setBackgroundResource (R.color.light_bg)
				dialogLayout.setBackgroundResource (R.color.light_bg)
				fg = Color.BLACK
		  }
		  else {

				setBackgroundResource (R.color.dark_bg)
				dialogLayout.setBackgroundResource (R.color.dark_bg)
				fg = Color.WHITE
		  }

		  if (layout () == R.layout.dialog_container_layout) {
				
				dialogActions = findViewById (R.id.dialog_actions_layout) as LinearLayout
		  }
	  		  				
		  val header = findViewById (R.id.dialog_title) as TextView
		  header.setText (title)

		  actions (this)
	 }

	 open fun layout (): Int { return (R.layout.dialog_container_layout) }
								 
	 open fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.dialog_default_action, dialogActions)	  		  
		  val accept = findViewById (R.id.dialog_accept) as Button
		  accept.setOnClickListener () {
				
				accept ()
		  }
	 }
	 
	 open fun accept () { }
	 open fun cancel () {

		  Pos.app.input.clear ()
	 }
	 
	 open fun sticky (): Boolean { return false }
	 	 
	 // PosDisplay

	 override fun enter () {

		  accept ()
	 }
	 
	 override fun message (message: String) { }
	 override fun message (message: Jar) { }
}
