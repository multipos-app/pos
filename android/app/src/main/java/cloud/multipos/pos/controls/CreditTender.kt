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
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.util.*

open class CreditTender (): Tender (null) {

	 init {
		  
		  tenderType = "credit"
	 }

	 override fun tenderType (): String { return "credit" }
	 override fun tenderDesc (): String { return "credit" }
	 override fun subTenderDesc (): String { return "external_credit" }
	 override fun openDrawer (): Boolean { return  jar ().getBoolean ("open_drawer") }
	 override fun printReceipt (): Boolean { return jar ().getBoolean ("print_receipt") }
}
