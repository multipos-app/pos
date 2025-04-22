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

import cloud.multipos.pos.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.controls.Control;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Display;
import android.util.DisplayMetrics;
import android.graphics.Point;

import java.util.*;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import java.net.NetworkInterface;
import java.net.InetAddress;
import android.net.wifi.WifiManager;

import org.json.JSONObject;
import org.json.JSONArray;

import android.content.Context;
import android.provider.Settings.Secure;

public class Config extends Jar {

	 public Config () {

		  super ();

		  if (!Pos.app.db ().ready ()) {

				Pos.app.db ().open ();
		  }

		  DbResult posConfigResult = new DbResult (Pos.app.db ()
																 .find ("pos_configs")
																 .query (),
																 Pos.app.db ());				
		  if (posConfigResult.fetchRow ()) {
				

				Jar config = posConfigResult.row ();
				
				// Logger.d ("load config... " + config.getInt ("id") + " " + config.getString ("config_desc"));

				parse (config.getString ("config"));
				initialize ();
				ready = true;
		  }
		  else {

				Logger.d ("no config...");
		  }

		  deviceData ();
	 }

	 public void initialize () {
						  
		  Configuration configuration = Pos.app.activity.getResources ().getConfiguration ();
		  Resources r = Pos.app.activity.getResources ();
						  
		  try {
				
				put ("version_name", Pos.app.activity.getPackageManager ().getPackageInfo (Pos.app.activity.getPackageName (), 0).versionName);
				PackageInfo pInfo = Pos.app.activity.getPackageManager ().getPackageInfo (Pos.app.activity.getPackageName (), 0);
				put ("version_code", pInfo.versionCode);
		  }
		  catch (NameNotFoundException nnfe) {
				
				Logger.w ("Can't get version name... " + nnfe.toString ());
		  }
		  
		  ready = true;

		  int sessionID = 0;
		  		  
		  // find the current session or create the first one
		  
		  DbResult sessionResult = new DbResult ("select id from pos_sessions order by id desc limit 1", Pos.app.db ());
		  if (sessionResult.fetchRow ()) {
				
				Jar session = sessionResult.row ();
				sessionID = session.getInt ("id");
		  }
		  else {

				Jar posSession = new Jar ()
					 .put ("business_unit_id", getInt ("business_unit_id"))
					 .put ("pos_no", getInt ("pos_no"));
					 
				sessionID = (int) Pos.app.db ().insert ("pos_sessions", posSession);
		  }
				
		  put ("pos_session_id", sessionID);
  
		  // cache the taxes

		  taxes = new HashMap <String, Jar> ();
		  DbResult taxResult = new DbResult ("select tax_groups.id, taxes.rate, taxes.short_desc from tax_groups, taxes " +
														 "where tax_groups.id = taxes.tax_group_id", Pos.app.db ());
		  while (taxResult.fetchRow ()) {
					 
				Jar tax = taxResult.row ();
				Jar t = new Jar ().
					 put ("id", tax.getLong ("id")).
					 put ("rate", tax.getDouble ("rate")).
					 put ("short_desc", tax.getString ("short_desc"));
					 
				taxes.put (Integer.toString (tax.getInt ("id")), t);
		  }
		  		  
		  try {
				
		  		put ("version_name", Pos.app.activity.getPackageManager ().getPackageInfo (Pos.app.activity.getPackageName (), 0).versionName);
		  		PackageInfo pInfo = Pos.app.activity.getPackageManager ().getPackageInfo (Pos.app.activity.getPackageName (), 0);
		  		put ("version_code", pInfo.versionCode);
		  }
		  catch (NameNotFoundException nnfe) {
				
		  		Logger.w ("Can't get version name... " + nnfe.toString ());
		  }

		  getIPs ();

		  put ("device_data", deviceData ());
	 }

	 public void update () {

		  Pos.app.db ().exec ("update pos_configs set config = '" + this.toString () + "'");
	 }

	 public Locale locale () { return Locale.US; }

	 private String currentMenu = null;
	 public ArrayList setMenu (String name) { 

		  if (menus.containsKey (name)) {
				currentMenu = name;
				return (ArrayList) menus.get (name); 
		  }
		  
		  return (ArrayList) menus.get (currentMenu); 

	 }
	 
	 public void getIPs () {
		  
		  String digit = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
		  String regex = "^/" + digit + "\\." + digit + "\\." + digit + "\\." + digit + "$";
		  Pattern pattern = Pattern.compile (regex);
		  String ipAddr = "";
				
		  try {

				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces (); en.hasMoreElements ();) {
					 
					 NetworkInterface intf = (NetworkInterface) en.nextElement ();
					 for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements ();) {
						  
						  InetAddress inetAddress = enumIpAddr.nextElement ();
						  
						  if (!inetAddress.isLoopbackAddress ()) {
								
								String ipaddress = inetAddress .getHostAddress ();
								if (pattern.matcher (inetAddress.toString ()).matches ()) {

									 ipAddr = ipaddress.toString ();
									 put ("ip_address", ipAddr.substring (0));
								}
						  }
					 }
				}

				
				String [] net = ipAddr.split ("\\.");

				if (net.length == 4) {
					 
					 put ("base_ip_address", net [0] + "." + net [1] + "." + net [2]);
				}
		  }
				
		  catch (Exception ex) {
				Logger.w ("Exception in Get IP Address: " + ex.toString());
		  }
	 }

	 public ArrayList menu () { return (ArrayList) menus.get (currentMenu); }
	 public String toString () { return super.toString (); }
	 
	 public Jar deviceData () {

		  Jar deviceData = new Jar ()
				.put ("metrics", getString ("metrics"))
				.put ("model", android.os.Build.MODEL)
				.put ("sdk", android.os.Build.VERSION.SDK_INT)
				.put ("android_release", android.os.Build.VERSION.RELEASE)
				.put ("version_name", getString ("version_name"))
				.put ("version_code", getInt ("version_code"))
				.put ("display_width", getInt ("display_width"))
				.put ("display_height", getInt ("display_height"))
				.put ("density", Pos.app.activity.getResources ().getDisplayMetrics ().densityDpi);

		  Logger.d ("device data... " + deviceData);
		  
		  DisplayMetrics displayMetrics = new DisplayMetrics();
		  Pos.app.getWindowManager ().getDefaultDisplay ().getMetrics (displayMetrics);
		  int height = displayMetrics.heightPixels;
		  int width = displayMetrics.widthPixels;
		  
 		  Logger.d ("window dimesions... " + height + "/" + width);
		  
		  put ("height", height);
		  put ("width", width);

		  return deviceData;
	 }

	 private Map menus = null;
	 private boolean ready = false;
	 private Display display;
	 
	 public boolean ready () { return ready; }
	 public Map menus () { return menus; }
	 private HashMap <String, Control> controls = null;
	 private HashMap <String, Jar> taxes = null;
	 public HashMap controls () { return controls; }
	 public HashMap taxes () { return taxes; }

}
