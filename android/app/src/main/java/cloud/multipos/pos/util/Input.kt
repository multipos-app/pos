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
 
package cloud.multipos.pos.util;

import cloud.multipos.pos.Pos
import cloud.multipos.pos.views.PosDisplays

class Input () {

	 val MAX = 15
	 
	 var sb = StringBuffer ()
	 var quantity = 1
	 
	 fun append (a: String) {

		  if (sb.length < MAX) {
				
				sb.append (a)
				// PosDisplays.update ()
		  }
	 }
	 
	 fun set (i: Int) {

		  sb.setLength (0)
		  sb.append (i.toString ())
		  // PosDisplays.update ()
	 }
	 
	 fun clear () {

		  sb.setLength (0)
		  quantity = 1
		  // PosDisplays.update ()
	 }
	 	 
	 fun del () {

		  // PosDisplays.update ()
		  if (sb.length > 0) {
				
				sb.setLength (sb.length - 1)
		  }
	 }

	 fun getInt (): Int {

		  var i = 0

		  if (sb.length > 0) {
				
				i = sb.toString ().toInt ()
		  }
		  
		  return i
	 }

	 fun getDouble (): Double {

		  var d = 0.0
		  
		  if (sb.length > 0) {
				
				d = sb.toString ().toDouble ()
		  }
		  
		  return d
	 }
	 	 
	 fun getString (): String {

		  var s = ""

		  if (sb.length > 0) {
				
				s = sb.toString ()
		  }
		  
		  return s
	 }
	 
	 fun hasInput (): Boolean {

		  return sb.length > 0
	 }
	 
	 fun sb (): StringBuffer {

		  return sb
	 }

	 fun length (): Int {

		  return sb.length
	 }

	 fun quantity (): Int {

		  if (getInt () > 1) {

				Pos.app.quantity = getInt ()
		  }
		  
		  return quantity
	 }
}
