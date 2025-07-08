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
 
package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.db.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.views.LaundryJobsView
import cloud.multipos.pos.views.PosDisplays

class LaundryJobs (): Suspend (), InputListener {

	 override fun controlAction (jar: Jar) {

		  val jobs = ArrayList <Jar> ()

		  var count = 0
		  var select = """
		  select 
		  id, 
		  ticket_no as job_no, 
		  complete_time as date_time,
		  state,
		  customer_id,
		  clerk_id,
		  uuid,
		  total,
		  ticket_text
		  from 
		  tickets
		  where 
		  (state = ${Ticket.SUSPEND} or state = ${Ticket.JOB_PENDING} or state = ${Ticket.JOB_COMPLETE}) 
		  order by id asc
		  """		  
		  val jobsResult = DbResult (select, Pos.app.db)
		  
		  while (jobsResult.fetchRow ()) {

				var job = jobsResult.row ()
				
				var clerkID = job.getInt ("clerk_id")
				Logger.d ("clerk... ${job.getInt ("clerk_id")}")
				
				job.remove ("clerk_id")

				if (clerkID > 0) {

					 val clerksResult = DbResult ("select * from employees where id = ${clerkID}", Pos.app.db)
					 if (clerksResult.fetchRow ()) {

						  var emp = Employee (clerksResult.row ())
						  job.put ("attendant", emp.display ())
					 }
				}
				else {
					 
					 job.put ("attendant", "")
				}

				var customerID = job.getInt ("customer_id")
				job.remove ("customer_id")

				if (customerID > 0) {

					 val customersResult = DbResult ("select * from customers where id = ${customerID}", Pos.app.db)
					 if (customersResult.fetchRow ()) {

						  job.put ("customer", Customer (customersResult.row ()).name ())
					 }
				}
				else {
					 
					 job.put ("customer", "")
				}	

				var itemCount = 0
				var weight = 0.0
				
				val itemsResult = DbResult ("select * from ticket_items where ticket_id = ${job.getInt ("id")}", Pos.app.db)
				while (itemsResult.fetchRow ()) {

					 var ticketItem = itemsResult.row ()
					 
					 if (ticketItem.getDouble ("metric") > 0.0) {
						  
						  itemCount ++
					 }
					 weight += ticketItem.getDouble ("metric")
				}
				
				job.put ("item_count", itemCount)
				job.put ("weight", weight)
				
				jobs.add (job)
				count ++
		  }

		  if (count > 0) {

				LaundryJobsView (this, "", jobs)
		  }
		  else {

				PosDisplays.message (Jar ()
												 .put ("prompt_text", Pos.app.getString ("no_suspended_tickets"))
												 .put ("echo_text", ""))
		  }
	 }
	 
	 override fun accept (result: Jar) {

		  var state = -1

		  Logger.x ("job accept... ${result}")
		  
		  when (result.getInt ("state")) {

				Ticket.SUSPEND -> {

					 state = Ticket.JOB_PENDING
				}

				Ticket.JOB_PENDING -> {

					 state = Ticket.JOB_COMPLETE
				}
		  }



		  if (result.has ("employee_id")) {
				
				Pos.app.db.exec ("update tickets set clerk_id = ${result.getInt ("employee_id")} where id = ${result.getInt ("ticket_id")}")
		  }

		  if (state > 0) {
		  
				Pos.app.db.exec ("update tickets set state = ${state} where id = ${result.getInt ("ticket_id")}")
		  }
		  else {
				
				Logger.w ("laundry job bad state... ${result}")
		  }

		  if (result.has ("load")) {

				Control.factory ("Recall").action (Jar ().put ("id", result.getInt ("ticket_id")))
		  }
	 }
}
