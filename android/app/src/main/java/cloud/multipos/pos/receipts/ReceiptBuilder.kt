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
 
package cloud.multipos.pos.receipts

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket

import java.util.*

open class ReceiptBuilder () {

	 val printCommands = PrintCommands ()

	 open fun ticket (t: Ticket, type: Int): ReceiptBuilder { return this }
	 open fun header (): ReceiptBuilder { return this } 
	 open fun customer (): ReceiptBuilder { return this } 
	 open fun items (): ReceiptBuilder { return this } 
	 open fun summary (): ReceiptBuilder { return this } 
	 open fun tender (): ReceiptBuilder { return this } 
	 open fun session (): ReceiptBuilder { return this } 
	 open fun orderItems (): ReceiptBuilder { return this } 
	 open fun footer (): ReceiptBuilder { return this } 
	 open fun separator (): ReceiptBuilder  { return this }
	 open fun separator (label: String): ReceiptBuilder  { return this }
	 open fun feed (lines: Int): ReceiptBuilder { return this }
	 open fun notes (): ReceiptBuilder { return this }
	 open fun cut (): ReceiptBuilder  { return this }
	 open fun text (): String  { return "" }
	 open fun print () { return }

	 fun add (list: List <Jar>) {
		  
		  var size = -1
		  var font = -1
		  var justify = -1
		  
		  for (p in list) {

				size = options.get ("size_" + p.getString ("size"))!!;
				font = options.get ("font_" + p.getString ("font"))!!;
				justify = options.get ("justify_" + p.getString ("justify"))!!;
				
				printCommands
					 .add (PrintCommand.getInstance ().directive (size))
					 .add (PrintCommand.getInstance ().directive (font))
					 .add (PrintCommand.getInstance ().directive (justify).text (p.getString ("text").trim ()))
		  }
	 }
	 
	 fun locale (): Locale {

		  var locale = Locale.US;

		  when (Pos.app.config.getString ("locale")) {

				"DK" -> locale = Locale ("da", "DK")
		  }
		  
		  return locale;
	 }
	 
	 val options = mapOf ("font_normal" to PrintCommand.NORMAL_TEXT,
								 "font_bold" to PrintCommand.BOLD_TEXT,
								 "font_inverse" to PrintCommand.INVERSE_TEXT,
								 "font_italic" to PrintCommand.ITALIC_TEXT,
								 "font_banner" to PrintCommand.BANNER_TEXT,
								 
								 "size_small" to PrintCommand.SMALL_TEXT,
								 "size_normal" to PrintCommand.MEDIUM_TEXT,
								 "size_big" to PrintCommand.BIG_TEXT,
								 
								 "justify_left" to PrintCommand.LEFT_TEXT,
								 "justify_center" to PrintCommand.CENTER_TEXT,
								 "justify_right" to PrintCommand.RIGHT_TEXT)

}
