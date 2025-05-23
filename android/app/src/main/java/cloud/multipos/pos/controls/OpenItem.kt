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
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.views.PosDisplays
import cloud.multipos.pos.db.*

open class OpenItem (): DefaultItem (), InputListener {

	 override fun controlAction (jar: Jar) {

		  jar (jar)
		  
		  if (jar ().has ("prompt_text")) {

				PosDisplays.message (Jar ()
												 .put ("prompt_text", jar ().getString ("prompt_text"))
												 .put ("echo_text", 0.0.currency ()))
											
		  }
		  else {
				
				PosDisplays.message (Jar ()
												 .put ("prompt_text", R.string.enter_price)
												 .put ("echo_text", 0.0.currency ()))
		  }
		  
		  Pos.app.controls.push (this)
	 }
	 
	 override fun accept (jar: Jar) {

		  // get the amount from main input and complete the item
		  
		  var amount = Pos.app.input.getDouble ()
		  Pos.app.input.clear ()
		  
		  if (amount != 0.0) {
				
				jar ().put ("amount", amount)
				super.controlAction (jar ())
				super.afterAction ()
		  }
	 }
	 
	 override fun type (): String { return "currency" }
	 
	 override fun mergeItems (): Boolean {

		  return false
	 }
}
