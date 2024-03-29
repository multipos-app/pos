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

public class ToggleClerkMode extends TicketModifier {

	 public ToggleClerkMode () { super ();	}

	 public void controlAction (Jar e) {

		  if (Pos.app.config.has ("clerk_mode")) {

				Pos.app.config.put ("clerk_mode", !Pos.app.config.getBoolean ("clerk_mode"));
		  }
		  else {
				Pos.app.config.put ("clerk_mode", true);
		  }

		  Logger.d ("clerk mode... " + Pos.app.config.getBoolean ("clerk_mode"));
		  
		  if (Pos.app.config.getBoolean ("clerk_mode")) {
				Pos.app.controls (Control.factory ("EnterClerk"));
		  }

		  Pos.app.ticket ();
	 }
}
