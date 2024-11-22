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

package cloud.multipos.pos.util.extensions

import cloud.multipos.pos.util.*

fun Double.currency (): String {

	 return this.currency (false)
}

fun Double.currency (showSymbol: Boolean): String {

	 var symbol = "$"
	 var fmt = "%.2f";
	 if (showSymbol) {
		  
		  fmt = "${symbol}%.2f";
	 }
	 
	 if (this < 0.0) {
		  
		  return "(" + String.format (fmt, Math.abs (this)) + ")";
	 }
	 else {
					 
		  return String.format (fmt, this);
	 }
}
