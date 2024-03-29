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
 
package cloud.multipos.pos.receipts;

class PrintCommand () {
	 
	 var directive = 0
	 var value = 0
	 var text = ""

	 fun directive (d: Int): PrintCommand {
		  
		  directive = d
		  return this
	 }
	 
	 fun value (v: Int): PrintCommand {

		  value = v 
		  return this
	 }
	 
	 fun text (t: String): PrintCommand {

		  text = t 
		  return this
	 }

	 override fun toString (): String {

		  return "pc: " + directive + " " + text + " " + value
	 }
	 
	 companion object {
		  
		  fun getInstance (): PrintCommand {
				
				return PrintCommand ()
		  }
		  
		  // fonts
		  
		  val NORMAL_TEXT  = 1
		  val BOLD_TEXT    = 2
		  val INVERSE_TEXT = 3
		  val ITALIC_TEXT  = 4
		  val BANNER_TEXT  = 5
		  
		  // size
		  
		  val MEDIUM_TEXT  = 6
		  val BIG_TEXT     = 7
		  val SMALL_TEXT   = 8
		  
		  // justify
		  
		  val CENTER_TEXT  = 9
		  val RIGHT_TEXT   = 10
		  val LEFT_TEXT    = 11
		  
		  // operations
		  
		  val OPEN_DRAWER  = 12
		  val CUT          = 13
		  val FEED         = 14
		  val PRINT        = 15
		  
		  // graphics
		  
		  val BARCODE      = 16
		  val QR_CODE      = 17
		  val LOGO         = 18
		  val LINE         = 19
	 }
}
