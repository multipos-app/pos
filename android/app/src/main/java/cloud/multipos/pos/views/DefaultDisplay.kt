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
 
package cloud.multipos.pos.views

import cloud.multipos.pos.*
import cloud.multipos.pos.util.Jar

import android.view.View

open class DefaultDisplay: PosDisplay {

	 override fun update () { }
	 override fun view (): View { return View (Pos.app) }
	 override fun clear () { }
	 override fun message (message: String) { }
	 override fun message (message: Jar) { }
	 // override fun select (): Int { return 0 }
	 // override fun select (position: Int) { }
	 // override fun selectValues (): MutableList <Int>  { return mutableListOf <Int> () }
}
