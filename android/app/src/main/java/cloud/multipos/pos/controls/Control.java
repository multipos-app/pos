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
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.views.PosDisplays;
import cloud.multipos.pos.views.ManagerOverrideView;

import java.util.ArrayList;

public abstract class Control implements PosControl {

	 public Control () { }
	 public boolean beforeAction () { return true; }
	 public boolean afterAction () { return true; }
	 public void setJar (Jar jar) { this.jar = jar; }
	 public Control jar (Jar jar) { this.jar = jar; return this; }
	 public Jar jar () { return jar; }
	 public Jar jar;
	 
	 public abstract void controlAction (Jar jar);
	 
	 public void action (Jar jar) {
				
		  if (!permission ()) {

				new ManagerOverrideView (this);
				return;
		  }
		  		  
		  if (beforeAction ()) {

				controlAction (jar);						  
				afterAction ();
		  }
	 }

	 public boolean permission () {
		  		  
		  if (Pos.app.employee == null) {
				
				return true;
		  }
		  
		  ArrayList deny = null;
		  		  
		  if (Pos.app.ticket.has ("manager")) {
				
				deny = (ArrayList) ((Employee) Pos.app.ticket.get ("manager")).deny ();
		  }
		  else {
				
				deny = (ArrayList) Pos.app.employee.deny ();
		  }

		  for (Object o: deny) {
				
				if (this.getClass ().getName ().endsWith ((String) o)) {
					 
					 return false;
				}
		  }
		  return true;
	 }

	 public void updateDisplays () {
		  
		  PosDisplays.update ();
	 }
	 
	 public String toString () {
		  
		  String tmp = getClass ().getName ();
		  if (jar != null) {
				
				tmp += " " + jar;
		  }
		  return tmp;
	 }

	 public static Control factory (String controlName) {

		  Control control;
		  control = (Control) LoadClass.get ("cloud.multipos.pos.controls." + controlName);

		  if (control == null) {

				Logger.w ("failed to load class... " + controlName);
				control = (Control) LoadClass.get ("cloud.multipos.pos.controls.Null");
		  }
		  
		  return control;
	 }
}
