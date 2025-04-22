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

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.*
import cloud.multipos.pos.db.DB
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.models.TicketTender
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.views.ScalesView

import java.util.Date

class PullTabsWeigh (): Control (), InputListener {
	 	 	 
	 override fun controlAction (jar: Jar) {

		  ScalesView (this,
						 "Weight Item",
						 InputListener.DECIMAL,
						 3)
	 }
	 
	 override fun accept (result: Jar) {

		  Logger.d ("weight accept... ${result}")

	 }
}
