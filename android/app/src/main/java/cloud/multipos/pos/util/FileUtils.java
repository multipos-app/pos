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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;

import cloud.multipos.pos.*;
import cloud.multipos.pos.db.DB;

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
				
				String format = "hh:mm:ss";
				SimpleDateFormat df = new SimpleDateFormat (format, new Locale (Pos.app.config.getString ("country"), Pos.app.config.getString ("locale")));
				String time = df.format (new Date ());
				
				String fname = "logcat-" + android.os.Build.MODEL + "-" + Pos.app.getString ("app_build") + "-" + time + ".log";
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
        }
		  catch (IOException e) {
        }
    }
}
