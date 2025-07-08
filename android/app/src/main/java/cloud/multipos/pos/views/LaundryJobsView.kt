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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.util.SparseArray

class LaundryJobsView (val control: InputListener, title: String, val jobs: ArrayList <Jar>): PosLayout (Pos.app, null), ThemeListener {

	 lateinit var jobReceiptText: PosText
	 lateinit var jobControlsLayout: LinearLayout
	 lateinit var jobKeysLayout: LinearLayout
	 lateinit var auxLayout: LinearLayout
	 lateinit var jobsList: LinearLayout
	 lateinit var select: PosButton
	 lateinit var laundryJobSelectView: LaundryJobSelectView
	 lateinit var keyListener: PosKeyListener
	 lateinit var job: Jar

	 var curr = -1
	 
	 val keyValues = SparseArray <String> ()
	 val jobViews = mutableListOf <JobLine> ()
	 
	 init {
		  
		  auxLayout = Pos.app.findViewById (R.id.aux_container) as LinearLayout
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  val layout = Pos.app.inflater.inflate (R.layout.laundry_jobs_layout, auxLayout)

		  // get the receipt layout
		  
		  jobReceiptText = layout.findViewById (R.id.job_receipt_text) as PosText
		  jobReceiptText.setTypeface (Views.receiptFont ())
		  
		  // get the controls layout and add initial control
		  
		  jobControlsLayout = layout.findViewById (R.id.job_controls) as LinearLayout

		  // get the job list layout

		  jobsList = Pos.app.findViewById (R.id.jobs_list) as LinearLayout

		  // set up the keys

		  jobKeysLayout = layout.findViewById (R.id.job_keyboard) as LinearLayout
		  
		  // render the jobs
		  
		  render ()

		  // init to select view

		  selectView ()
		  
		  Themed.add (this)
		  Pos.app.auxView.show ()
	 }

	 fun controls (): LinearLayout { return jobControlsLayout }
	 
	 fun render () {

		  jobsList.removeAllViews ()
		  
		  var i = 0
		  for (job in jobs) {

				var jobLine = JobLine (job, i)

				if (curr == i) {

					 jobLine.setBackgroundResource (Themed.selectBg)
				}
				
				jobsList.addView (jobLine)
				i ++
				
		  }
	 }

	 fun selectView () {
		  
		  jobControlsLayout.removeAllViews ()
		  jobControlsLayout.addView (LaundryJobSelectView (this))
	 }

	 fun assignView () {
		  
		  jobControlsLayout.removeAllViews ()
		  val view = LaundryJobAssignView (this)
		  keyListener = view as PosKeyListener
		  initKeys (jobKeysLayout)
		  jobControlsLayout.addView (view)
	 }

	 fun completeView () {
		  
		  jobControlsLayout.removeAllViews ()
		  val view = LaundryJobCompleteView (this)
		  jobControlsLayout.addView (view)
	 }

	 fun complete (result: Jar) {

		  result
		  	 .put ("ticket_id", jobs [curr].getInt ("id"))
			 .put ("state", jobs [curr].getInt ("state"))
	 
		  control.accept (result)
	 }
	 
	 inner class JobLine (job: Jar, i: Int): LinearLayout (Pos.app) {

		  init {
				
				val layout = Pos.app.inflater.inflate (R.layout.laundry_job_line_layout, this)
				layout.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
				layout.setClickable (true)

				val jobNo = layout.findViewById (R.id.job_no) as PosText?
				val dateTime = layout.findViewById (R.id.job_date) as PosText?
				val customer = layout.findViewById (R.id.job_customer) as PosText?
				val attendant = layout.findViewById (R.id.job_attendant) as PosText?
				val state = layout.findViewById (R.id.job_state) as PosText?
				val items = layout.findViewById (R.id.job_items) as PosText?
				val weight = layout.findViewById (R.id.job_weight) as PosText?
				val total = layout.findViewById (R.id.job_total) as PosText?

				customer?.text = job.getString ("customer")
				attendant?.text = job.getString ("attendant")
				jobNo?.text = String.format ("%04d", job.getInt ("job_no"))
				dateTime?.text = job.getString ("date_time").localDate ("M/d hh:mm a")
				items?.text = job.getInt ("item_count").toString ()
				weight?.text = job.getDouble ("weight").toString ()
				total?.text = job.getDouble ("total").currency (false)

				var status = "unknown"
				when (job.getInt ("state")) {

					 Ticket.SUSPEND -> {

						  status = Pos.app.getString ("job_open")
					 }
					 
					 Ticket.JOB_PENDING -> {

						  status = Pos.app.getString ("job_pending")
					 }

					 Ticket.JOB_COMPLETE -> {

						  status = Pos.app.getString ("job_complete")
					 }
				}
				
				state?.text = status

				if (i.odd ()) {

					setBackgroundResource (R.color.light_odd_bg) 
				}
				
				jobViews.add (this)

				val select = findViewById (R.id.job_select) as LinearLayout?
				select?.setOnClickListener {
					 					 
					 jobReceiptText?.text = job.getString ("ticket_text")
					 curr = i
					 render ()
				}
		  }
	 }

	 fun initKeys (keysLayout: LinearLayout) {
		  
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
									R.id.keyboard_00)

		  if (this::keyListener.isInitialized) {
				
				for (k in keys) {
				
					 var button = keysLayout.findViewById (k) as PosButton?
					 button?.setOnClickListener () {
						  
						  keyListener.text (button.getText ().toString ())
					 }
				}

				var del = keysLayout.findViewById (R.id.keyboard_del) as PosButton?
				del?.setOnClickListener {
					 
					 keyListener.del ()
				}
				
				var enter = keysLayout.findViewById (R.id.keyboard_enter) as PosButton?
				enter?.setOnClickListener {
					 
					 keyListener.enter ()
				}
		  }
	 }
	 
	 // theme listener
	 
	 override fun update (theme: Themes) {
		  
	 }
}
