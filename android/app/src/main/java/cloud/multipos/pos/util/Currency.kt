/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 
package cloud.multipos.pos.util

import cloud.multipos.pos.Pos

import java.math.RoundingMode
import java.math.BigDecimal
import java.text.DecimalFormat

class Currency () {
	 
	 companion object {
		  
		  @JvmStatic
		  fun round (d: Double): Double {

				var df = DecimalFormat ("#.##")
				df.roundingMode = RoundingMode.HALF_UP
				var r = df.format (d).toDouble ()

				if (Pos.app.configInit ()) {
					 
					 when (Pos.app.config.getString ("currency")) {

						  "DKK" -> {

					 			/**
					 			 *
					 			 * Denmark round to nearest .50
					 			 * Øre 1-24 round down to 00.  (1,24 = 1,00)
								 * Øre 25-74 round “down” to 50  (1,25 or 1,74 = 1,50)
								 * Øre 75-99 round up  (1,75 or 1,99 = 2,00)
								 *
					 			 */
						  
					 			var negative = r < 0  // if return, convert to positive, then back
					 			if (negative) {
						  			 r *= -1
								}

								var ival = r.toInt ()
								var decimal = (df.format (r - r.toInt ()).toDouble () * 100.0).toInt ()
								var dval = 0.0
								
								when (decimal) {
									 
									 in 25..74 -> dval = .50
									 in 75..99 -> dval = 1.0
								} 
						  		
								r = ival + dval
								
								if (negative) {
						  			 r *= -1
								}
						  }
					 }
				}
				return r
		  }

		  fun toDouble (d: String): Double {

				return d.replace (",", ".", false).toDouble ()
		  }
	 }
}
