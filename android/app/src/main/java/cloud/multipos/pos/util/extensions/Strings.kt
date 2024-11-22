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

package cloud.multipos.pos.util.extensions

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale
import java.security.MessageDigest;
import java.math.BigInteger;
import android.telephony.PhoneNumberUtils

fun String.toDate (dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone ("UTC"),): Date {
	 val parser = SimpleDateFormat (dateFormat, Locale.getDefault ())
	 parser.timeZone = timeZone
	 return parser.parse (this)
}

fun Date.formatTo (dateFormat: String, timeZone: TimeZone = TimeZone.getDefault (),): String {
	 val formatter = SimpleDateFormat(dateFormat, Locale.getDefault ())
	 formatter.timeZone = timeZone
	 return formatter.format(this)
}

fun String.utcToLocal (timestamp: String, format: String): String {
		  
	 return timestamp.toDate ().formatTo (format)
}

fun String.md5Hash (): String {
		  				
	 var md = MessageDigest.getInstance ("MD5")
	 var messageDigest = md.digest (this.toByteArray ())
	 var number = BigInteger (1, messageDigest)
	 var md5 = number.toString (16)
	 
	 while (md5.length < 32) {
		  
		  md5 = "0" + md5
	 }
	 
	 return md5
}

fun String.center (len: Int, pad: String): String {
		  
	 if (this.length >= len) return this.substring (0, len)

	 var sb = StringBuilder ()
		  
	 for (i in 0..(len - this.length) / 2) {
				
		  sb.append (pad)
	 }
		  
	 sb.append (this)
	 
	 while (sb.length < len) {
		  
		  sb.append (pad)
	 }
	 
	 return sb.toString ()		  
}

fun String.phone (locale: String): String {
		  
	 return PhoneNumberUtils.formatNumber (this, locale)
}

fun String.fill (size: Int): String {
		  
	 val sb = StringBuilder ();
	 
	 for (i in 0..size) {
		  
		  sb.append (this);
	 }
	 
	 return sb.toString();		  
}

fun String.trunc (id: Int): String {
				
	 val max = Pos.app.getInt (id);
				
	 if (this.length > max) {
				
		  return this.substring (0, max - 3) + "...";
	 }
	 else {
		  
		  return this;
	 }
}
