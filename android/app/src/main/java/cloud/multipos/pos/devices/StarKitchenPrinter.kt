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
 
package cloud.multipos.pos.devices

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.models.Ticket
import cloud.multipos.pos.receipts.*

import android.os.Handler
import android.os.Message
import com.starmicronics.stario.StarIOPort
import com.starmicronics.starioextension.StarIoExtManager
import com.starmicronics.stario.StarPrinterStatus
import com.starmicronics.stario.StarIOPortException
import com.starmicronics.starioextension.IConnectionCallback
import com.starmicronics.starioextension.StarIoExtManagerListener
import com.starmicronics.starioextension.ICommandBuilder
import com.starmicronics.stario.PortInfo
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import android.os.Looper;

class StarKitchenPrinter (): StarPrinter () {
	 
	 override fun deviceIcon (): Int { return R.string.fa_kitchen_printer }
	 override fun deviceClass (): DeviceClass { return DeviceClass.KitchenPrinter }

	 init {

		  DeviceManager.kitchenPrinter = this
        deviceStatus = DeviceStatus.OnLine
		  success (this)
	 }
}
