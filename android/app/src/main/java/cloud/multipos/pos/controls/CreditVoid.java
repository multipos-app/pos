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
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.util.*;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;

public class CreditVoid extends Tender {

	 public CreditVoid () {
		  super (null);
		  this.handler = new CreditVoidHandler () ;
	 }

	 public boolean openDrawer () { return false; }

	 public void controlAction (Jar e) {

		  if (Pos.app.ticket.items.size () == 0) {
				return;
		  }

		  if (confirmed ()) {

				Jar payment = null;
				for (TicketTender tt: Pos.app.ticket.tenders) {

					 Jar tmp = new Jar (tt.getString ("data_capture"));
					 payment = new Jar (tmp.getString ("data_capture"));
				}
								
		  		confirmed (false);
				dialog.dismiss ();
		  }
		  else {
				
		  		setJar (e);

		  }
	 }

	 private class CreditVoidHandler extends Handler {

		  @Override
		  public void handleMessage (Message msg) {
								
				switch (msg.what) {
					 
				case PosConst.PAYMENT_COMPLETE:
				case PosConst.PAYMENT_FAILED:
					 
					 break;
					 
				}
		  }
	 }
	 
	 public String tenderType () { return "credit_void"; }
	 public String tenderDesc () { return "credit_void"; } 
	 public boolean printReceipt () { return false; }

	 private Dialog dialog = null;
	 private Handler handler;
}
