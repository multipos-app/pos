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

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TicketAddon extends Jar {

	 public TicketAddon () { }

	 public double extAmount () {
		  return new BigDecimal (getDouble ("amount") * (double) getInt ("quantity")).setScale (2, RoundingMode.HALF_UP).doubleValue ();
	 }
	 
	 // Ticket types

	 public static final int CREDIT_SERVICE_FEE =  1;
	 public static final int TICKET_DISOUNT     =  2;
}
