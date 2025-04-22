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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.net.HttpURLConnection;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.zip.GZIPInputStream;
import android.util.Base64;
import java.util.zip.ZipInputStream;
import java.time.ZonedDateTime;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileUtils {

	 public static void save (String fname, String data) {

		  try {
				
				File filesDir = Pos.app.activity.getFilesDir ();
				File [] files = filesDir.listFiles ();
		  
				for (File f: files) {

					 if (f.toString ().indexOf ("logcat") > 0) {

						  f.delete ();
					 }
				}
				
				FileWriter writer = new FileWriter (filesDir + "/" + fname); 
				writer.write (data); 
				writer.flush ();
				writer.close ();
		  }
		  catch (IOException ioe) {

				Logger.d ("errror writing to sd... " + ioe);
		  }
	 }

	 public static void uploadLog () {
		  
		  try {

				Logger.d ("upload log...");
				
				String format = "hh:mm:ss";
				SimpleDateFormat df = new SimpleDateFormat (format, new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
				String time = df.format (new Date ());
				
				String fname = "logcat-" + android.os.Build.MODEL + "-" + BuildConfig.VERSION_NAME + "-" + time + ".log";
				fname = fname.replace (" ", "-");
				String command = String.format ("logcat -d ");        
				Process process = Runtime.getRuntime ().exec (command);
				
				BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
				StringBuilder result = new StringBuilder ();
				String line = null;
				
            while ((line = reader.readLine ()) != null) {
					 
					 result.append (line);
					 result.append ("\n");
            }
				
				FileUtils.save (fname, result.toString ());
				FileUtils.upload (fname);
				
        }
		  catch (IOException e) {
        }
    }

	 public static void upload (String f) {

		  final String fname = f;

		  Thread thread = new Thread () {

		  			 public void run () {

		  				  HttpURLConnection conn = null;
		  				  DataOutputStream dos = null;
		  				  String lineEnd = "\r\n";
		  				  String twoHyphens = "--";
		  				  String boundary = "*****";
		  				  int bytesRead, bytesAvailable, bufferSize;
		  				  byte [] buffer;
		  				  int maxBufferSize = 1 * 1024 * 1024; 
		  				  String file = fname;
		  				  int serverResponseCode = 0;

		  				  try { 
                    								
						  		File f = new File (Pos.app.activity.getFilesDir () + "/" + fname);
								FileInputStream fin = new FileInputStream (f);

		  				  		String uploadURL = "http://upload.multipos.cloud/?fname="+ fname;
		  				  		URL url = new URL (uploadURL);
                    
		  				  		conn = (HttpURLConnection) url.openConnection ();
		  				  		conn.setRequestMethod ("POST");
		  				  		conn.setDoInput (true);
		  				  		conn.setDoOutput (true);
		  				  		conn.setUseCaches (false);
		  				  		conn.setRequestProperty ("Connection", "Keep-Alive");
		  				  		conn.setRequestProperty ("ENCTYPE", "multipart/form-data");
		  				  		conn.setRequestProperty ("Content-Type", "multipart/form-data;boundary=" + boundary);
		  				  		conn.setRequestProperty ("uploaded_file", fname); 
 
		  				  		dos = new DataOutputStream (conn.getOutputStream ());
          
		  				  		dos.writeBytes (twoHyphens + boundary + lineEnd); 
		  				  		dos.writeBytes ("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fname +"\"" + lineEnd);
		  				  		dos.writeBytes (lineEnd);

		  				  		bytesAvailable = fin.available (); 
		  				  		bufferSize = Math.min (bytesAvailable, maxBufferSize);
		  				  		buffer = new byte [bufferSize];
          
		  				  		bytesRead = fin.read (buffer, 0, bufferSize);  
		  				  		int total = bytesRead;

		  				  		while (bytesRead > 0) {

		  				  			 dos.write (buffer, 0, bufferSize);
		  				  			 bytesAvailable = fin.available ();
		  				  			 bufferSize = Math.min (bytesAvailable, maxBufferSize);
		  				  			 bytesRead = fin.read (buffer, 0, bufferSize); 
  
		  				  			 total += bytesRead;
                  
		  				  		}
          
		  				  		dos.writeBytes (lineEnd);
		  				  		dos.writeBytes (twoHyphens + boundary + twoHyphens + lineEnd);
		  				  		dos.writeBytes (lineEnd);

		  				  		serverResponseCode = conn.getResponseCode ();
		  				  		String serverResponseMessage = conn.getResponseMessage ();

		  				  		fin.close ();
		  				  		dos.flush ();
		  				  		dos.close ();
                     
		  				  }
		  				  catch (MalformedURLException ex) {
		  				  		Logger.w ("Bad url " + ex.toString ());
		  				  } 
		  				  catch (Exception e) {
		  				  		Logger.w ("Unknown exception " + e.toString ());
		  				  }
		  			 }
		  		};
		  
		  thread.start  ();
	 }
	 
	 public static void downloadImage (String fname) {
		  
		  Logger.d ("download image ... " + fname);
		  
		  
		  Thread thread = new Thread () {

					 Bitmap image = null;
		  			 public void run () {
						  
						  try {
								
								URL url = new URL ("https://img.multipos.cloud/" + Pos.app.local.getInt ("merchant_id", 0) + "/" + fname);
								image = BitmapFactory.decodeStream (url.openConnection ().getInputStream ());
								
						  }
						  catch(IOException e) {
								
								Logger.w ("download image error... " + e);
						  }
					 
						  if (image != null) {
						  
								try {
									 
									 FileOutputStream out = new FileOutputStream (new File ("/sdcard/img/" + fname));
									 image.compress (Bitmap.CompressFormat.PNG, 100, out);
									 out.flush ();
									 out.close ();       
								}
								catch (Exception e) {
									 
									 Logger.w ("error saving image... " + fname + " " + e);
								}
						  }
					 }
				};
				
				thread.start ();
    }
}
