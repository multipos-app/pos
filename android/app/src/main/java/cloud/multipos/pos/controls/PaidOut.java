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
import cloud.multipos.pos.util.*;

public class PaidOut extends DefaultItem {

	 public PaidOut () { super (); }

	 public void controlAction (Jar e) {

		  if (!Pos.app.input.hasInput ()) {
				return;
		  }

		  jar ().put ("amount", (Pos.app.input.getInt () * -1));
		  super.controlAction (jar ());
	 }
}
