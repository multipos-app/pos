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
 
package cloud.multipos.pos.receipts

import android.app.Activity

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.models.Ticket;

class ReceiptUtils (var activity: Activity) {

	 fun tag (ticket: Ticket): String {

		  var tag =
				activity.getString (R.string.receipt_tag_ticket_no) + ticket.getInt ("ticket_no") + " " +
		      activity.getString (R.string.receipt_tag_pos_no) + ticket.getInt ("pos_no") + " " +
				activity.getString (R.string.receipt_tag_session_no) + ticket.getInt ("pos_session_id")
		  
		  if (Pos.app.employee != null) {
				tag +=
					 " " + 
		  			 activity.getString (R.string.receipt_tag_employee) + Pos.app.employee?.getString ("fname")
		  }
				
		  return tag
	 }
}
