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
import cloud.multipos.pos.views.PosDisplays;

public class GiftCardSale extends DefaultItem implements InputListener {

	 public GiftCardSale () { super (); }

	 public void controlAction (Jar Jar) {

		  jar (jar);
		  // new NumberDialog (this, R.string.enter_amount, NumberDialog.CURRENCY, true)
		  // 		.value (0)
		  // 		.show ();
	 }

	 public void accept (Jar result) {

		  double amount = result.getDouble ("value");
		  
		  Logger.d ("gift card sale... " + jar ());

		  if (amount == 0) {
				PosDisplays.message (Pos.app.getString ("enter_amount"));
				return;
		  }

		  jar ().put ("amount", amount);
		  super.controlAction (jar ());
	 }

	 public String type () { return ""; }
}
