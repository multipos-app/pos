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
 
package cloud.multipos.pos.models;

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TicketItemAddon extends Jar {

	 public TicketItemAddon () { }

	 public void update () {

		  if (((int) getInt ("id")) == 0) {
				
				Pos.app.db ().insert ("ticket_item_addons", this);
		  }
		  else {
				
				Pos.app.db ().update ("ticket_item_addons", getInt ("id"), this);
		  }
	 }

	 public double extAmount () {

		  return new BigDecimal (getDouble ("addon_amount") * (double) getInt ("addon_quantity")).setScale (2, RoundingMode.HALF_UP).doubleValue ();
	 }
	 
	 public static TicketItemAddon factory () { return new TicketItemAddon (); }


	 public static int DISCOUNT =       1;
	 public static int MARKDOWN =       2;
	 public static int MODIFIER_PLUS =  3;
	 public static int MODIFIER_MINUS = 4;
}
