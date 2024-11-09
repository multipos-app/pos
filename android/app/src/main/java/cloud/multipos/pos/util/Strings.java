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

import android.util.DisplayMetrics;

import java.util.Locale;
import java.util.Currency;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Strings {

	 public static String center (String s, int size, String pad) {

		  if (pad == null) return "";
		  if (s == null) return "";
		  
		  if (s.length () >= size) return s.substring (0, size);

		  StringBuilder sb = new StringBuilder ();
		  for (int i = 0; i < (size - s.length ()) / 2; i++) {
				sb.append (pad);
		  }
		  
		  sb.append (s);
		  
		  while (sb.length () < size) {
		  		sb.append (pad);
		  }
		  
		  return sb.toString();		  
	 }

	 public static String fill (String s, int size) {
		  
		  StringBuilder sb = new StringBuilder ();
		  for (int i = 0; i < size; i++) {
				sb.append (s);
		  }
		  return sb.toString();		  
	 }

	 public static String trim (String s, int len) {

		  return s;
		  
		  // if (s.length () > len) {
		  // 		return s.substring (0, len);
		  // }
		  // else {
		  // 		return s;
		  // }
	 }
	 
	 public static String stretch (String s1, String s2, int len) {

		  String tmp = s1;
		  for (int i=0; i< len - (s1.length () + s2.length ()); i++) { tmp += " "; }
		  tmp += s2;
		  return tmp;
	 }
	 
	 public static String trimElipses (String s, int len) {

		  if (s.length () > len) {
				return s.substring (0, len) + "...";
		  }
		  else {
				return s;
		  }
	 }
	 
	 public static String phone (String s, String locale) {

        if (s == null) {
            return "";
		  }

		  if (s.length () == 11) {
				s = s.substring (1);
		  }
		  
        StringBuffer sbuf = new StringBuffer (s.trim ());

        if (locale.equals ("en_US")) {

            switch (sbuf.length ()) {
            case 7:
                sbuf.insert (3, "-");
                break;

            case 10:
                sbuf.insert (0, "(");
                sbuf.insert (4, ") ");
                sbuf.insert (9, "-");
                break;
            default:
            }
        } 
		  else {
				return s;
        }
        return sbuf.toString ();
    }

	 public static String getStars (int value) {

		  String tmp = Integer.toString (value);
		  StringBuffer result = new StringBuffer ();
		  for (int i=0; i<tmp.length (); i++) { result.append ("*"); }
		  return result.toString ();
	 }

    public static String currency (double amount, boolean showCurrencySymbol) {

		  DecimalFormat format;
		  DecimalFormatSymbols symbols;
		  String currency;
		  Locale locale = Locale.US;
		  		  
		  switch (Pos.app.config.getString ("locale")) {

		  case "DK":
		  case "DE":
		  case "SE":
		  case "NO":
				
				locale = Locale.GERMANY;
		      format = (DecimalFormat) NumberFormat.getCurrencyInstance (locale);
				symbols = format.getDecimalFormatSymbols ();
				symbols.setCurrencySymbol("");
				currency = format.format (amount);
				
				return currency.substring (0, currency.length () - 2);
		  
		  default:
				
				if (amount < 0.0) {
					 
					 return "(" + String.format ("$%.2f", Math.abs (amount)) + ")";
				}
				else {
					 
					 return String.format ("$%.2f", amount);
				}
		  }
    }
	 
    public static String decimalPoint () {

		  switch (Pos.app.config.getString ("locale")) {

		  case "DK":
		  case "DE":
		  case "SE":
		  case "NO":
				
				return ",";
		  }
				
		  return ".";
    }
	 
    public static String decimal (double value, int places) {

		  String fmt = "###,###.###";
		  if (places > 0) {

		  		for (int i=0; i<places; i++) {
					 
		  			 fmt += "#";
		  		}
		  }
		  else {

		  		fmt += "#";
		  }
		  
		  DecimalFormat df = null;
		  Locale locale = Locale.US;
		  
		  switch (Pos.app.config.getString ("locale")) {

		  case "DK":
		  case "DE":
		  case "SE":
		  case "NO":
				
				locale = Locale.GERMAN;
				break;
		  default:
				
				break;
		  }
		  
		  df = new DecimalFormat (fmt, new DecimalFormatSymbols (locale));
		  return df.format (value);
	 }

	 public static String prompt (String prompt, char fill, int len) {

		  StringBuffer sb = new StringBuffer (prompt);
		  len = len - sb.length ();
		  for (int i=0; i<len; i++) {
				sb.append (fill);
		  }
		  
		  return sb.toString ();
	 }
	 
	 public static Jar parseTracks (String track) {

		  Jar e = new Jar ();

		  if (track.indexOf ("B") > 0) {

				String cc = track.substring (track.indexOf ("B") + 1, track.indexOf ("^"));

				int firstCaret = track.indexOf ("^");
				String name = track.substring (firstCaret, track.indexOf ("^", firstCaret + 1));

				e.put ("cc", cc);
				e.put ("name", name);
		  
				String masked = cc.substring (0, 4) + "..." + cc.substring (cc.length () - 4);
				e.put ("masked", masked);
		  }

		  return e;
	 }
	 
	 public static String getMaskedCCNum (String track) {

		  String tmp = track;
		  String cardNo = tmp.substring (tmp.indexOf ("%B") + 2, tmp.indexOf ("%B") + 6);
		  cardNo += "...";
		  cardNo += tmp.substring (tmp.indexOf ("^") - 4, tmp.indexOf ("^"));
		  return cardNo;
	 }

	 public static String getCustomer (String track) {
		  return "";
	 }

	 public static String getDisplay (int m) {

		  String metric = "";
		  
		  switch (m) {
				
		  case DisplayMetrics.DENSITY_LOW:
				metric = "ldpi/" + m;
				break;
		  case DisplayMetrics.DENSITY_MEDIUM:
				metric = "mdpi/" + m;
				break;
		  case DisplayMetrics.DENSITY_HIGH:
				metric = "hdpi/" + m;
				break;
		  case DisplayMetrics.DENSITY_XHIGH:
				metric = "xhdpi/" + m;
				break;
		  case DisplayMetrics.DENSITY_XXHIGH:
				metric = "xxhdpi/" + m;
				break;
		  case DisplayMetrics.DENSITY_XXXHIGH:
				metric = "xxxhdpi/" + m;
				break;
		  default:
				metric = "unknown/" + m;
		  }

		  return metric;
	 }

	 public static double round (double d) {
		  return new BigDecimal (d).setScale (2, RoundingMode.HALF_UP).doubleValue ();
	 }

	 public static String base64encode (String s) {
		  return Base64.encodeToString (s.getBytes (), Base64.DEFAULT).trim ();
	 }

	 public static String base64decode (String s) {
		  return new String (Base64.decode (s, Base64.DEFAULT));
	 }
	 
	 public static String getMd5Hash (String input) {
		  
		  try {
				
				MessageDigest md = MessageDigest.getInstance ("MD5");
				byte[] messageDigest = md.digest (input.getBytes ());
				BigInteger number = new BigInteger (1, messageDigest);
				String md5 = number.toString (16);
				
				while (md5.length () < 32) {
					 md5 = "0" + md5;
				}
				
				return md5;
				
		  } catch (NoSuchAlgorithmException e) {
				return "";
		  }
	 }

	 public static SimpleDateFormat dateFormat () {

		  String format = "E, MMM d h:mm:ss a";

		  switch (Pos.app.config.getString ("locale")) {

		  case "DK":
				
				format = "d MMMM yyyy HH:mm:ss";
				break;

		  }
		  				
		  SimpleDateFormat df = new SimpleDateFormat (format, new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
		  df.setTimeZone (TimeZone.getTimeZone (Pos.app.config.getString ("timezone")));

		  return df;
	 }

	 public static String utcToLocal (String timestamp) {
		  
		  SimpleDateFormat fout = null;
		  switch (Pos.app.config.getString ("locale")) {

		  case "DK":
				
				fout = new SimpleDateFormat ("d MMMM yyyy HH:mm:ss", new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
				break;

		  default:
				
				fout = new SimpleDateFormat ("E, MMM d h:mm:ss a", new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
				break;
		  }
		  
		  SimpleDateFormat fin = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		  
		  Date date = null;
		  try {
				
				date = fin.parse (timestamp);
		  }
		  catch (java.text.ParseException e) {

		  		Logger.w ("error parsing date... " + e);
		  		return "";
		  }

		  fout.setTimeZone (TimeZone.getTimeZone (Pos.app.config.getString ("timezone")));
		  return fout.format (date);

	 }
	 
	 public static String utcToLocal (String timestamp, String outputFormat) {
		  
		  SimpleDateFormat fout = null;
		  switch (Pos.app.config.getString ("locale")) {

		  case "DK":
				
				fout = new SimpleDateFormat ("d MMMM yyyy HH:mm:ss", new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
				break;

		  default:
				
				fout = new SimpleDateFormat (outputFormat, new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
				break;
		  }
		  
		  SimpleDateFormat fin = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		  
		  Date date = null;
		  try {
				
				date = fin.parse (timestamp);
		  }
		  catch (java.text.ParseException e) {

		  		Logger.w ("error parsing date... " + e);
		  		return "";
		  }

		  fout.setTimeZone (TimeZone.getTimeZone (Pos.app.config.getString ("timezone")));
		  return fout.format (date);

	 }

	 public static long dateToEpoch (String d) {

		  SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		  try {
				
				Date date = df.parse (d);
				return date.getTime () / 1000L;
		  }
		  catch (java.text.ParseException pe) {
		  }
		  return 0;
	 }
	 
	 public static String toHex (int d, int len) {
		  
		  String hex = Integer.toHexString (d).toUpperCase ();
		  while (hex.length () < len) { hex = "0" + hex; }
		  return hex;
	 }

	 public static void dumpConst (Class c) {
		  
		  try {
				
		  		Field [] fields = c.getFields();
				
		  		Logger.d ("Constants for: " + c.getName());
				
		  		for (Field f: fields) {
					 
		  			 f.setAccessible (true);
		  			 Logger.d ("  " + f.getName());
		  		}
				
		  }
		  catch (Exception e) {
		  		Logger.d ("dump const... " + e);
		  }
	 }

	 public static void dumpMethods  (Object o) {

		  Class c = o.getClass ();
		  Method m [] = c.getMethods ();
		  for (int i=0; i<m.length; i ++) {

				Logger.d ("class... " + c.getName () + " : " + m [i]);
		  }
	 }
	 
	 public static String trunc (String text, int id) {
				
		  int max = Pos.app.getInt (id);
				
		  if (text.length () > max) {
				
				return text.substring (0, max - 3) + "...";
		  }
		  else {
				
				return text;
		  }
	 }

	 public static final int EAN =       13;
	 public static final int UPC_A =     12;
	 public static final int UPC_E =      6;
	 public static final int TICKET_NO = 11;


}

