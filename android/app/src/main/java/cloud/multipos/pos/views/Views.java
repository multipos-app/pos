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

import android.graphics.Typeface;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.TextView;
import android.graphics.Color;

public class Views {

	 public static Typeface banner () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("banner_font")); }
	 public static Typeface buttonFont () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("button_font")); }
	 public static Typeface displayFont () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("display_font")); }
	 public static Typeface font () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("default_font")); }
	 public static Typeface icons () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("icon_font")); }
	 public static Typeface receiptFont () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("receipt_font")); }
	 public static Typeface receiptFontBold () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("receipt_bold_font")); }
	 public static Typeface receiptFontItalic () { return Typeface.createFromAsset (Pos.app.activity.getAssets (), Pos.app.getString ("receipt_italic_font")); }

	 public static void toast (String t) {
		  
		  // Toast toast = Toast.makeText (Pos.app.activity, t, Toast.LENGTH_LONG);
		  // LinearLayout linearLayout = (LinearLayout) toast.getView ();
		  // View view = toast.getView ();
		  // linearLayout.setGravity (Gravity.CENTER);
		  // TextView messageTextView = (TextView) linearLayout.getChildAt (0);
		  // messageTextView.setTextColor (Color.WHITE);
		  // messageTextView.setTextSize (50);
		  // toast.show ();

	 }

	 public static void listViews (ViewGroup v) {

		  Logger.d ("view count... " + v.getChildCount ());

		  for (int index=0; index < v.getChildCount (); ++index) {
				
				if (v.getChildAt (index) instanceof ViewGroup) {
					 listViews ((ViewGroup) v.getChildAt (index));
				}
				
				Logger.d ("view... " + v.getChildAt (index).getClass ().getName ());
		  }
	 }
}
