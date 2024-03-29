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

import cloud.multipos.pos.receipts.*

import com.starmicronics.starioextension.ICommandBuilder

interface StarCommandBuilder {

	 fun build (commands: PrintCommands): MutableList <ICommandBuilder>
    fun width (): Int
    fun boldWidth (): Int
    fun quantityWidth (): Int
    fun descWidth (): Int
    fun amountWidth (): Int
    fun deviceName (): String { return "Unknown Star Printer" }
}
