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
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.DeviceManager;
import cloud.multipos.pos.views.PosDisplays

public class Enter (): Control () {

	 override fun  controlAction (jar: Jar) {

		  jar (jar)
		  
		  if (Pos.app.controls.size > 0) {

				val control = Pos.app.controls.removeFirst ()

				if (control is InputListener) {

					 control.accept (Jar ())
				}
				else if (control is DefaultItem) {
					 
					 control.jar ()
						  .put ("merge_like_items", false)
						  .put ("amount", Pos.app.input.getDouble () / 100.0)
					 
					 Pos.app.input.clear ()
					 control.complete ()
					 return;
				}
				else {
					 
					 control.action (control.jar ())
				}
		  }
		  else if (jar ().has ("value")) {
				
				val value = jar ().getString ("value")
				Control.factory ("DefaultItem")
					 .action (Jar ()
									  .put ("sku", value)
									  .put ("entry_mode", "keyed"))

				
		  }
		  else if (Pos.app.input.length () > 0) {
					 
					 // operator input sku?

					 
					 when (Pos.app.input.length ()) {  
													
													5, 6, 7, 8, 12, 13 -> {
														 
														 Control.factory ("DefaultItem")
															  .action (Jar ()
																				.put ("sku", Pos.app.input.getString ())
																				.put ("entry_mode", "keyed"))
													}
													
													11, 36 -> {  // recall by id
																	 
																	 Control.factory ("RecallByUUID")
																		  .action (Jar ().put ("recall_key", Pos.app.input.getString ()))
													}
					 }
				}
		  
		  // PosDisplays.enter ()
	 }
}
