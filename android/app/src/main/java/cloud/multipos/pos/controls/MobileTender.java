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
 
package cloud.multipos.pos.controls;

import cloud.multipos.pos.*;
import cloud.multipos.pos.devices.*;
import cloud.multipos.pos.util.*;

public class MobileTender extends Tender {

	 public MobileTender () { super (null); }

	 public String tenderType () { return "mobile"; }
	 public int tenderID () { return 7; } 
	 public String tenderDesc () { return "mobile"; }
	 public boolean printReceipt () { return false; }
	 
	 public boolean openDrawer () {

		  if (jar () != null) {
				return jar ().getBoolean ("open_drawer");
		  }

		  return false;
	 }
}
