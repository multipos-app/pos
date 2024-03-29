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
 
package cloud.multipos.pos.devices;

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.content.res.Configuration
import android.os.Handler
import android.os.Message
import android.view.KeyEvent

/**
 *
 * keyboard scanner class, scanner must be configured as
 * keyboard wedge and send TAB to terminate input
 */

class KeyboardScanner (): Scanner ()  {

	 var chars = mutableListOf <Char> ()
	 override fun deviceName (): String  { return "Keyboard scanner" }

	 init {
		  
		  deviceStatus = DeviceStatus.OnLine
		  success (this)
	 }	  

	 override fun input (o: Any) {

		  val event = o as KeyEvent
		  val keyCode = event.getKeyCode ()
		  
        when (keyCode) {

            KeyEvent.KEYCODE_TAB, KeyEvent.KEYCODE_ENTER -> {
								  
		  			 Pos.app.sendMessage (PosConst.SCAN, String (chars.toCharArray ()))
					 chars.clear ()
				}

				else -> {

					 chars.add (event.getUnicodeChar ().toChar ());
				}
		  }
	 }
}
