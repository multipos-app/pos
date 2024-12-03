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

/**
 *
 * Jar JSON-array
 *
 */

package cloud.multipos.pos.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Jar extends HashMap {

	 public Jar () {

		  super (new HashMap <String, Object> ());
	 }
	 
	 public Jar (String json) {

		  super (new HashMap <String, Object> ());
		  parse (json);
	 }

	 public Jar (JSONObject json) {

		  parse (json);
	 }

	 public Jar (Jar p) {
		  
		  Iterator it = p.entrySet ().iterator ();
		  while (it.hasNext ()) {
				
		  		Map.Entry pair = (Map.Entry) it.next ();
				put (pair.getKey (), pair.getValue ());
		  }
	 }
	 
	 public Jar parse (String jsonString) {
		  
		  if (jsonString.length () == 0) {

				Logger.stack ("jar, attempt to parse empty string...");
				return new Jar ();
		  }
		  
		  try {
				
				if (jsonString.length () > 0) {

					 json = new JSONObject (jsonString);
					 parse (json);
				}
				else {
					 
					 Logger.stack ("parse error... " + json.length () + " " + json);
					 return null;
				}
		  }
		  catch (JSONException je) {
				
				Logger.w ("Error json parse str... " + je.toString () + "\n" + json);
		  }
		  return this;
	 }
	 
	 public Jar parse (JSONObject json) {

		  if (json != null) {
		  
				try {
				
					 Iterator <String> iter = json.keys ();
					 while (iter.hasNext ()) {
						  
						  String key = iter.next ();

						  if (json.get (key) instanceof JSONObject) {

								Jar p = new Jar ((JSONObject) json.get (key));
								put (key, p);
						  }
						  else if (json.get (key) instanceof JSONArray) {

								JSONArray array = (JSONArray) json.get (key);
								ArrayList <Jar> list = new ArrayList <Jar> ();
								for (int i=0; i<array.length (); i++) {

									 Jar p = new Jar ((JSONObject) array.get (i));
									 list.add (p);
								}
								put (key, list);

						  }
						  else if  (json.get (key) instanceof String) {
								
								String s = (String) json.get (key);
								if (s.startsWith ("{")) {
									 Jar p = new Jar (new JSONObject (s));
									 put (key, p);
								}
								else {
									 put (key, json.get (key));
								}
						  }
						  else {
								put (key, json.get (key));
						  }
					 }
				}
				catch (JSONException je) {
					 Logger.w ("Error json parse obj... " + je.toString () + "\n" + json);
				}
		  }
		  
		  return null;
	 }
	 
	 public String toString () {
		  
		  StringBuffer sb = new StringBuffer ();
		  sb.append ("{");
		  String sep = "";
		  
		  Iterator it = entrySet ().iterator ();
		  while (it.hasNext ()) {
				
		  		Map.Entry pair = (Map.Entry) it.next ();

				// Logger.x ("to string... " + pair);
				
				if (pair.getValue () instanceof Jar) {
	 
					 Jar p = (Jar) pair.getValue ();
					 sb.append (sep + "\"" + pair.getKey () + "\": " + p.toString ());
					 sep = ",";
				}
				else if (pair.getValue () instanceof ArrayList) {
					 
					 sb.append (sep + "\"" + pair.getKey () + "\": \n[");
					 ArrayList <Jar> a = (ArrayList <Jar>) pair.getValue ();
					 sep = "";
					 for (Jar p: a) {

						  sb.append (sep + p);
						  sep = ",";
					 }
					 sb.append ("]");
				}
				else if (pair.getValue () instanceof String) {

					 String val =  (String) pair.getValue ();
					 if (val.startsWith ("{")) {
						  sb.append (sep + "\"" + pair.getKey () + "\": " + pair.getValue ());
					 }
					 else {
						  sb.append (sep + "\"" + pair.getKey () + "\": \"" + pair.getValue () + "\"");
					 }
				}
				else {
					 sb.append (sep + "\"" + pair.getKey () + "\": " + pair.getValue ());
				}
				sep = ", ";
		  }

		  sb.append ("}");
		  
		  return sb.toString ();
	 }
	 
	 public ArrayList <Jar> getList (String key) {

		  if (containsKey (key)) {
				return (ArrayList <Jar>) super.get (key);
		  }
		  return new ArrayList <Jar> ();
	 }

	 public boolean has (String key) { return containsKey (key); }

	 /**
	  *
	  * get
	  *
	  */

	 
	 public Object getObj (String key) {
		  
		  if (containsKey (key)) {
				
				return (Object) super.get (key);
		  }

		  return null;
	 }
	 
	 public Jar get (String key) {
		  
		  if (containsKey (key)) {

				if (super.get (key) instanceof Jar) {
					 
					 return (Jar) super.get (key);
				}
				else if (super.get (key) instanceof String) {
					 
					 return new Jar ((String) super.get (key));
				}
				else if (super.get (key) instanceof ArrayList) {
					 
					 return new Jar ();
				}
				else {
					 
					 Logger.w ("Jar get failed... " + key + " " + super.get (key) + " " + super.get (key).getClass ().getName ());
				}
		  }
		  
		  return new Jar ();
	 }
	 
	 public int getInt (String key) {

		  if (containsKey (key)) {
				
				if (super.get (key) instanceof Integer) {
					 
					 return ((Integer) super.get (key)).intValue ();
				}
				else if (super.get (key) instanceof Long) {
					 
					 return (int) ((Long) super.get (key)).intValue ();
				}
				else if (super.get (key) instanceof String) {

					 String val = (String) super.get (key);
					 if (val.equals ("true")) {

						  return new Integer (1);
					 }
					 else if (val.equals ("false")) {
						  return new Integer (0);
					 }

					 return new Integer (String.valueOf (super.get (key)));
				}
		  }
		  return 0;
	 }
		  
	 public long getLong (String key) {

		  if (super.get (key) instanceof Long) {
				return ((Long) super.get (key)).longValue ();
		  }
		  else if (super.get (key) instanceof String) {
				
				return (new Long (String.valueOf (super.get (key))));
		  }
		  return 0;
	 }
	 
	 public String getString (String key, String defaultValue) {

		  Object o = super.get (key);
		  
		  if (o instanceof Integer) {
				return String.valueOf (((Integer) o).intValue ());
		  }
		  else if (o instanceof Double) {
				return String.valueOf (((Double) o).doubleValue ());
		  }
		  else if (o instanceof Long) {
				return String.valueOf (((Long) o).longValue ());
		  }
		  else if (o instanceof Boolean) {
				return String.valueOf (((Boolean) o).booleanValue ());
		  }
		  else if (o instanceof String) {
				return (String) o;
		  }
		  else {
				return defaultValue;
		  }
	 }
	 
	 public String getString (String key) {
		  return getString (key, "");
	 }
	 
	 public double getDouble (String key) {

		  if (containsKey (key)) {
				
				if (super.get (key) instanceof Double) {
					 return ((Double) super.get (key)).doubleValue ();
				}
				else if (super.get (key) instanceof Integer) {
					 return (double) ((Integer) super.get (key)).intValue ();
				}
				else if (super.get (key) instanceof String) {
					 return Double.valueOf ((String) super.get (key));
				}
		  }
		  return  0.0;
	 }

	 public boolean getBoolean (String key) {

		  if (super.get (key) != null) {

				if (super.get (key) instanceof String) {
					 
					 return ((String) super.get (key)).equals ("true");
				}
				else if (super.get (key) instanceof Integer) {

					 return ((Integer) super.get (key)).intValue () == 1;
				}
								
				return ((Boolean) super.get (key)).booleanValue ();
		  }
		  return false;
	 }

	 /**
	  *
	  * put
	  *
	  */

	 public Jar put (String key, Jar p) {

		  super.put (key, p);
		  return this;
	 }

	 public Jar put (String key, int value) {

		  super.put (key, new Integer (value));
		  return this;
	 }

	 public Jar put (String key, long value) {

		  super.put (key, new Long (value));
		  return this;
	 }

	 public Jar put (String key, boolean value) {
		  
		  super.put (key, new Boolean (value));
		  return this;
	 }

	 public Jar put (String key, double value) {

		  super.put (key, new Double (value));
		  return this;
	 }

	 public Jar put (String key, String value) {

		  super.put (key, value);
		  return this;
	 }

	 public Jar put (String key, ArrayList value) {

		  super.put (key, value);
		  return this;
	 }
	 public Jar put (String key, List value) {

		  super.put (key, value);
		  return this;
	 }

	 /**
	  *
	  * remove
	  *
	  */
	 
	 public Jar remove (String key) {

		  super.remove (key);
		  return this;
	 }
	 
	 /**
	  *
	  * utils
	  *
	  */

	 public JSONObject json () {
		  try {
				return new JSONObject (toString ());
		  }
		  catch (JSONException je) {
				// Logger.stack ("Error json parser... ");
		  }
		  return null;
	 }

	 
	 public Iterator <String> keys () { return entrySet ().iterator (); }

	 public Jar copy (Jar p) {
		  
		  Iterator it = p.entrySet ().iterator ();
		  
		  while (it.hasNext ()) {
		  
		  		Map.Entry pair = (Map.Entry) it.next ();
				put (pair.getKey (), pair.getValue ());
		  }
		  
		  return this;
	 }

	 public String stringify () {

		  String s = "{\"result\": \"json parse error\"}";
		  try {
				
				// s = json.toString (3).replace ("\\", "").replace ("\"[", "[").replace ("]\"", "]");
				JSONObject j = new JSONObject (toString ());
				s = j.toString (3);
		  }
		  catch (JSONException je) { }
		  return s;
	 }
	 
	 private JSONObject json;
}
