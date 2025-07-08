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

class LaundryJobSelectView (laundryJobsView: LaundryJobsView): PosLayout (Pos.app, null), ThemeListener {

	 lateinit var select: PosButton
	 
	 init {

		  val layout = Pos.app.inflater.inflate (R.layout.laundry_job_select_controls_layout, laundryJobsView.controls ())
		  select = layout.findViewById (R.id.job_select) as PosButton
		  select?.setOnClickListener {

				if (laundryJobsView.curr >= 0) {

					 when (laundryJobsView.jobs [laundryJobsView.curr].getInt ("state")) {
	  
						  Ticket.SUSPEND -> {
								
								laundryJobsView.assignView ()
						  }

						  Ticket.JOB_PENDING -> {
								
								laundryJobsView.completeView ()
						  }

						  Ticket.JOB_COMPLETE -> {
								
								Pos.app.auxView.hide ()
								Control.factory ("Recall").action (Jar ().put ("id", laundryJobsView.jobs [laundryJobsView.curr].getInt ("id")))
						  }
					 }
				}
				else {

					 select.text = Pos.app.getString ("job_select_prompt")
				}
		  }

		  val cancel = layout.findViewById (R.id.job_cancel) as PosButton?
		  cancel?.setOnClickListener {
				
				Pos.app.auxView.hide ()
		  }
	 }
}
