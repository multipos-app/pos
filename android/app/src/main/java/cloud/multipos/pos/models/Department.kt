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
import cloud.multipos.pos.db.*
import cloud.multipos.pos.util.*

class Department (val department: Jar?): Jar (), Model {
		  
	 init {

		  if (department != null) {
								
				copy (department)
		  }
	 }

	 override fun display (): String {

		  return getString ("department_desc")
	 }

	 	 companion object {
		  
		  // department types

 		  const val MERCHANDISE = 1
 		  const val MENU        = 2
 		  const val BANK        = 3
 		  const val DEPOSIT     = 4
 		  const val COMMENT     = 5
 		  const val LABOR       = 6
		 }
}
