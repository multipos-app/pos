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
import cloud.multipos.pos.controls.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.devices.*

import android.view.Gravity
import android.widget.Button
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.LinearLayout

class ReportView (val title: String, jar: Jar): DialogView (title) {
	 
	 init {
		  
		  Pos.app.inflater.inflate (R.layout.report_layout, dialogLayout)
		  
		  val text = findViewById (R.id.report_text) as PosText
		  text.setTypeface (Views.receiptFont ())
		  text.setTextColor (fg)

		  if (jar.has ("ticket")) {
				
				var t = jar.get ("ticket").getString ("ticket_text")
				
				if (jar.get ("ticket").has ("aux_receipts")) {

					 t += "\n\n" + jar.get ("ticket").getString ("aux_receipts")
				}
				
				text.setText (t)
		  }
		  else if (jar.has ("report_text")) {

				text.setText (jar.getString ("report_text"))
		  }

		  // scroll to bottom

		  val scroller = findViewById (R.id.report_scroller) as ScrollView
		  scroller.post (Runnable () {
									fun run () {
										 
										 scroller.fullScroll (View.FOCUS_UP)              
									}
		  })

		  DialogControl.addView (this)
	 }
	 
	 override fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.dialog_print_action, dialogActions)	  		  
		  val accept = findViewById (R.id.dialog_accept) as Button
		  accept.setOnClickListener () {
				
				accept ()
		  }
	 }
	 
	 override fun accept () {

		  Pos.app.receiptBuilder ().print ();
		  DialogControl.close ()
	 }
	 
	 override fun sticky (): Boolean { return true }
}
