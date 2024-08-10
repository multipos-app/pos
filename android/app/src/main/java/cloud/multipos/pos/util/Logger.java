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
 
package cloud.multipos.pos.util;

import cloud.multipos.pos.Pos;
import cloud.multipos.pos.BuildConfig;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;

public class Logger  {

	 public static void d (String text) {
		  				
		  String tmp = text;
		  if (tmp.length () > LINE_MAX) {

				int pos = 0;
				while (pos < tmp.length ()) {

					 
					 int len = LINE_MAX;
					 if ((pos + len) >= tmp.length ()) {
						  Log.d (TAG, "[DEBUG]: " + tmp.substring (pos, tmp.length ()));
						  return;
					 }
					 else {
						  Log.d (TAG, "[DEBUG]: " + tmp.substring (pos, pos + len));
						  pos += len;
					 }
					 
					 
				}
				return;
		  }
		  
		  Log.d (TAG, "[DEBUG]: " + tmp);
	 }

	 public static void d (Jar e) {
		  // Logger.d (e.json ());
		  e.toString ();
	 }
	 
	 public static void d (JSONObject json) {

		  if (json == null) {
				d ("Null object in print json");
				stack ("null");
				return;
		  }
		  
		  try {
				Logger.x ("\n" + json.toString (3));
		  }
		  catch (JSONException je) { }
	 }

	 public static void x (String text) {
				
		  if (text.length() > 3000) {
				
				Log.i (TAG, text.substring (0, 3000));
				x (text.substring (3000));
		  }
		  else {

				Log.i (TAG, text); // continuation
		  }
	 }
	 
	 public static void s (String text) {

		  Log.d (TAG, "[DEBUG]: " + text);
	 }
	 
	 public static void i (String text) { 

		  String tmp = text;
		  if (tmp.length () > 500) {
				tmp = text.substring (0, LINE_MAX) + "..." + text.substring (text.length () - LINE_MAX, text.length ());
		  }
		
		  put (APP, INFO, tmp);
		  Log.i (TAG, "[INFO?]: " + tmp); 
	 }

	 public static void i (int category, String text) { 

		  String tmp = text;
		  if (tmp.length () > 500) {
				tmp = text.substring (0, LINE_MAX) + "..." + text.substring (text.length () - LINE_MAX, text.length ());
		  }
		
		  put (category, INFO, tmp);
		  Log.i (TAG, "[INFO?]: " + tmp); 
	 }
	 
	 public static void w (String text) { 

		  Log.i (TAG, "[WARN!]: " + text);
		  // stack ("WARN");
		  // put (APP, WARN, "[WARN!]: " + text);

	 }

	 public static void w (int category, String text) { 

		  Log.w (TAG, text);
		  // put (category, WARN, "[WARN!]: " + text);

	 }
	 
	 public static void posStatus (int category, int severity, String text) {

		  return;
		  
		  // if (Pos.app.posStatus () != null) {
				
		  // 		switch (category) {
					 
		  // 		case APP:
		  // 			 break;
					 
		  // 		case HARDWARE:
		  // 			 Pos.app.posStatus ().devices (severity, text);
		  // 			 break;

		  // 		}
		  // }
	 }

	 public static void put (int category, int severity, String text) {

		  // if (text == null) return;

		  // Params e = new Jar ().
		  // 		put ("category", category).
		  // 		put ("severity", severity).
		  // 		put ("event_text", text);

		  // posStatus (category, severity, text);

	 }

	 public static void stack (String text) {

		  Throwable t = new Throwable ();
		  Log.d (TAG, text, t);
	 }

	 public static void setDebug (boolean d) { debug = d; }

	 private static boolean debug = false;

	 public static final String TAG = "multiPos";
	 public static final int LINE_MAX = 180;
	 
	 // categories
	 
	 public static final int APP =      1;
	 public static final int HARDWARE = 2;
	 public static final int PAYMENT =  3;
	 public static final int NETWORK =  4;
	 
	 // severity

	 public static final int WARN  = 1;
	 public static final int INFO =  2;
	 public static final int DEBUG = 3;
}
