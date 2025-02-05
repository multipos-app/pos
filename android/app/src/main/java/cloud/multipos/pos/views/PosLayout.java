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

import android.content.Context;
import android.util.AttributeSet;
import android.content.res.Resources;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.View;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;

import java.util.ArrayList;

public class PosLayout extends LinearLayout implements SwipeListener {

	 public PosLayout (Context context, AttributeSet attrs) {
		  
		  super (context, attrs);

		  this.context = context;
		  this.attrs = attrs;
		  this.resources = context.getResources ();
		  setLayoutParams (new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	 }
	 
	 public int getIntAttr (String name, int defValue) {

		  for (int i=0; i<attrs.getAttributeCount (); i++ ) {
				
				if (attrs.getAttributeName (i).equals (name)) {

					 return attrs.getAttributeIntValue (i, defValue);
				}
		  }
		  return defValue;
	 }
	 
	 public String getAttr (String name, String defValue) {

		  for (int i=0; i<attrs.getAttributeCount (); i++ ) {
				
				if (attrs.getAttributeName (i).equals (name)) {

					 return attrs.getAttributeValue (i);
				}
		  }
		  return defValue;
	 }
	 
	 @Override
	 public void onSwipe (SwipeDir dir) { }
	 	 	 
	 protected Context context;
	 protected AttributeSet attrs;
	 protected Resources resources;
}
