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

import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.IOException;

public class LoadClass {

	 public LoadClass () { }
	 
	 public static Object get (String className) {

		  if (packageName == null) {  // one time init...
				
				pm = Pos.app.activity.getPackageManager ();
				
				try {

					 packageName = Pos.app.activity.getPackageName ();
					 ai = pm.getApplicationInfo (packageName, 0);
					 sourceApk = ai.publicSourceDir;
					 
				} 
				catch (NameNotFoundException e) {
					 
					 Logger.w ("Failed to find source apk for class loader...");
				}
				
				dexOutputDir = Pos.app.activity.getDir ("dex", Context.MODE_PRIVATE);
				loader = new DexClassLoader (sourceApk,
													  dexOutputDir.getAbsolutePath (),
													  null,
													  Pos.app.activity.getClassLoader ());
				
				packageName = Pos.app.activity.getPackageName ();
		  }

		  try {

				Class <?> loadedClass = Class.forName (className, true, loader);
				Object result = loadedClass.getConstructor ().newInstance ();
				return result;
		  }
		  catch (Exception e) {
				
				Logger.w ("Error loading class... " + className);
		  }
		  return null;
	 }

	 private static PackageManager pm;
	 private static String sourceApk;
	 private static String packageName;
	 private static ApplicationInfo ai;
	 private static File dexOutputDir;
	 private static DexClassLoader loader;
	 
}
