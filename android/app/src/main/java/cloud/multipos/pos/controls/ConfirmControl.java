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

import android.app.Dialog;

public abstract class ConfirmControl extends TicketModifier {

	 public ConfirmControl () {
		  
		  super ();
		  instance = this;
	 }

	 public ConfirmControl confirmed (boolean confirmed) {

		  this.confirmed = confirmed;
		  return this;
	 }

	 public boolean confirmed () {

		  if ((jar () != null) && jar ().has ("confirmed")) {
				
				return jar ().getBoolean ("confirmed");
		  }
		  
		  return confirmed;
	 }
	 
	 private boolean confirmed = false;

	 public String confirmText () { return confirmText; }
	 public void confirmText (String confirmText) { this.confirmText = confirmText; }
	 private String confirmText = null;
	 protected ConfirmControl instance;
}
