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
 
package cloud.multipos.pos.models

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import java.util.ArrayList

public class TicketTender (jar: Jar): Jar () {

	 init {
		  
		  copy (jar)
	 }

	 companion object {
		  
		  const val COMPLETE         = 0
		  const val NOT_SETTLED      = 1
		  const val SETTLED          = 2
		  const val REFUND           = 3
		  const val REVERSE          = 4
		  
		  const val CASH             = 1
		  const val CREDIT           = 2
		  const val CHECK            = 3
		  const val GIFT_CERTIFICATE = 5
		  const val VOUCHER          = 6
		  const val MOBILEPAY        = 7
		  const val ACCOUNT          = 9
		  const val GIFT_CARD        = 10
	 }
}
