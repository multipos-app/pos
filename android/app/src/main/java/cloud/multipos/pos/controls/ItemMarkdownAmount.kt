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

import java.util.ArrayList

import cloud.multipos.pos.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*

class ItemMarkdownAmount(): ItemMarkdown (), InputListener {

	 override fun controlAction (jar: Jar) {

		  jar (jar)

		  Logger.d ("item markdown... " + jar ())
		  
		  if (jar.has ("amount") && (jar.getDouble ("amount") > 0.0)) {
				
				super.controlAction (jar)
		  }
		  else {
		  }
	 }

	 override fun accept (result: Jar) {

		  var amount = result.getDouble ("value")
		  jar ().put ("amount", amount)

		  if (jar ().has ("receipt_desc")) {

				jar ()
					 .put ("receipt_desc", jar ().getString ("receipt_desc") + " " + amount)
		  }
		  else {
				jar ().put ("receipt_desc", Pos.app.getString ("discount") + " " + amount.currency (true))
		  }
		  
		  super.controlAction (jar ())
	 }
	 	 
	 override fun getMarkdown (ti: TicketItem, jar: Jar): Double {
		  
		  var amount = 0.0
		  if (jar.has ("amount")) {
				
		  		amount = jar.getDouble ("amount")
		  }
		  else {
				return 0.0
		  }
		  
		  return -1.0 * amount
	 }
}

