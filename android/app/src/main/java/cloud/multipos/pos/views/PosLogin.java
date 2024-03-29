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
 
package cloud.multipos.pos.views;

import cloud.multipos.pos.R;
import cloud.multipos.pos.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.controls.Control;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import java.util.ArrayList;
import android.widget.Button;
import android.widget.TextView;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.view.Gravity;
import android.graphics.Typeface;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import android.widget.ProgressBar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class PosLogin extends PosLayout {

	 public PosLogin (Context context, AttributeSet attrs) {

		  super (context, attrs);
		  
 		  Pos.app.inflater.inflate (Pos.app.resourceID ("login_buttons", "layout"), this);

		  // final ProgressBar progress = (ProgressBar) findViewById (R.id.login_loading);
		  // progress.setScaleX (.25f);
		  // progress.setScaleY (.25f);
		  
		  Button del = (Button) findViewById (Pos.app.resourceID ("login_del", "id"));
		  if (del != null) {

				del.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, R.color.white));
				del.setOnClickListener (new View.OnClickListener () {
						  @Override
						  public void onClick (View view) {
						  
								if (getPin) {
								
									 if (pin.length () > 0) {
										  pin = pin.substring (0, pin.length () - 1);
										  mask = mask.substring (0, mask.length () - 1);
										  loginInput.setText (mask);
									 }
								}
								else {
									 if (cashier.length () > 0) {
										  cashier = cashier.substring (0, cashier.length () - 1);
										  loginInput.setText (cashier);
									 }
								}
						  }
					 });
		  }

		  int [] buttons = {R.id.login_button_0,
								  R.id.login_button_1,
								  R.id.login_button_2,
								  R.id.login_button_3,
								  R.id.login_button_4,
								  R.id.login_button_5,
								  R.id.login_button_6,
								  R.id.login_button_7,
								  R.id.login_button_8,
								  R.id.login_button_9};
		  
		  for (int i=0; i<buttons.length; i ++) {
				
		  		final String val = String.valueOf (i);
				
		  		Button digit = (PosButton) findViewById (buttons [i]);
		  		digit.setOnClickListener (new View.OnClickListener () {
		  				  @Override
		  				  public void onClick (View view) {

		  						if (getPin) {
		  							 pin += val;
		  							 mask += "\u2022";
		  							 loginInput.setText (mask);
		  						}
		  						else {
		  							 cashier += val;
		  							 loginInput.setText (cashier);
		  						}
		  				  }
		  			 });
		  }
		  
		  loginInput = (PosText) findViewById (R.id.login_input);
		  
		  login = (Button) findViewById (R.id.login_button); 
		  login.setOnClickListener (new View.OnClickListener () {
		  			 @Override
		  			 public void onClick (View view) {

		  				  if (getPin) {

		  						if ((cashier.length () > 0) && (pin.length () > 0)) {

		  							 DbResult employeeResult  = new DbResult ("select username, password, fname, lname, profile_id, id from employees " +
																							"where username = '" +  cashier + "'" +
																							" and password = '" + Strings.getMd5Hash (pin) + "'",
																							Pos.app.db);
									 
		  							 if (employeeResult.fetchRow ()) {

										  login.setText (Pos.app.getString ("please_wait"));

										  new Thread (() -> {

													 try { Thread.sleep (200); } catch (InterruptedException ie) { }
													 Pos.app.sendMessage (PosConst.LOGIN, employeeResult.row ());
													 
										  }).start ();
									 }

		  							 else {
										  
		  								  loginInput.setText (Pos.app.getString ("invalid_login"));
		  								  cashier = "";
		  								  pin = "";
		  								  mask = "";
		  								  getPin = false;
		  							 }
		  						}
		  				  }
		  				  else if (cashier.length () > 0) {
								
		  						getPin = true;
		  						loginInput.setText ("");
		  						loginInput.setHint (Pos.app.getString ("pincode"));
		  						login.setText (Pos.app.getString ("login"));
		  				  }
		  			 }
		  		});

		  cashier = "";
		  pin = "";
		  mask = "";
	 }

	 private boolean getPin = false;
	 private String cashier;
	 private String pin;
	 private String mask;
	 private Context context;
	 private PosText loginInput;
	 private Button login;
}
