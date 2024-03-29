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

import java.util.ArrayList;

import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.util.*;

public class ItemMarkdownPercent extends ItemMarkdown implements InputListener {

	 public ItemMarkdownPercent () { super (); }

	 public void controlAction (Jar Jar) {

		  jar (jar);

		  if (jar.has ("percent") && (jar.getDouble ("percent") > 0.0)) {
				
				super.controlAction (jar);
		  }
		  else {
				
				// new NumberDialog (this, R.string.enter_percent, NumberDialog.DECIMAL, true)
				// 	 .value (0)
				// 	 .show ();
		  }
	 }
	 
	 public String type () { return ""; }

	 public void accept (Jar result) {

		  double amount = result.getDouble ("value");

		  savedParams = new Jar (jar ().toString ());
		  jar (savedParams);

		  jar ()
				.put ("percent", amount);

		  if (jar ().has ("receipt_desc")) {

				jar ()
					 .put ("receipt_desc", jar ().getString ("receipt_desc") + " " + amount + "%");
		  }

		  super.controlAction (jar ());
	 }

	 public double getMarkdown (TicketItem ti, Jar Jar) {
		  
		  double percent = 0;
		  if (jar.has ("percent")) {
				return
					 -1.0 * (ti.getDouble ("amount") * (jar.getDouble ("percent") / 100.0));
		  }
		  else {
				return 0;
		  }
	 }

	 public void savedParams () { }

	 private Jar savedParams;
}
