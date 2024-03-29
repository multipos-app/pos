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

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;

import android.util.AttributeSet;

public class Attrs {

	 public static int getInt (AttributeSet attrs, String key, int defVal) {
		  
		  for (int i=0; i<attrs.getAttributeCount (); i++ ) {
				
				if (attrs.getAttributeName (i).equals (key)) {
					 
					 return attrs.getAttributeIntValue (i, defVal);
				}
		  }
		  
		  return defVal;
	 }

	 public static String getString (AttributeSet attrs, String key, String defVal) {

		  for (int i=0; i<attrs.getAttributeCount (); i++ ) {
				
				if (attrs.getAttributeName (i).equals (key)) {

					 return attrs.getAttributeValue (i);
				}
		  }
		  return defVal;
	 }
}
