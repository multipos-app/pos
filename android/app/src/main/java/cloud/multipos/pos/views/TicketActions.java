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
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.controls.DefaultItem;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.Button;
import android.graphics.Typeface;

public class TicketActions extends LinearLayout {

	 public TicketActions (Context context, AttributeSet attrs, float weight) {

		  super (context, attrs);
		  this.context = context;

		  Resources resources = context.getResources ();
		  // TypedArray a = context.obtainStyledAttributes (attrs, R.styleable.view);
		  int padding = 5; // Integer.parseInt (a.getString (R.styleable.view_sale_padding));

		  setOrientation (LinearLayout.HORIZONTAL);
		  LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams (LayoutParams.FILL_PARENT, 0, weight);
		  setLayoutParams (ll);
		  ll.setMargins (padding, padding, padding, padding);

		  setPadding (padding, padding, padding, padding);
		  
		  textColor = Color.WHITE; // Color.parseColor (a.getString (R.styleable.view_text_color));
		  textSize = 14; // Integer.parseInt (a.getString (R.styleable.view_text_size)) + 10;

		  // Button voidSale = new Button (context);
		  // voidSale.setLayoutParams (new LinearLayout.LayoutParams (0, LayoutParams.FILL_PARENT, 1f));
		  // voidSale.setTypeface (Views.icons ());
		  // voidSale.setText ("\uf2ed");
		  // voidSale.setTextColor (Color.RED);
		  // voidSale.setTextSize (TypedValue.COMPLEX_UNIT_SP, textSize + 10);
		  // voidSale.setBackgroundResource (resources.getIdentifier ("transparent", "color", Pos.app.activity.getPackageName ()));

		  // addView (voidSale);

		  // Button tender = new Button (context);
		  // LinearLayout.LayoutParams tl = new LinearLayout.LayoutParams (0, LayoutParams.FILL_PARENT, 5f);
		  // tl.setMargins (padding, padding, padding, padding);
		  // tender.setLayoutParams (tl);
		  // tender.setText ("Tender");
		  // tender.setTextColor (Color.BLACK);
		  // tender.setTextSize (TypedValue.COMPLEX_UNIT_SP, textSize);
		  // tender.setBackgroundResource (resources.getIdentifier ("color_3", "color", Pos.app.activity.getPackageName ()));

		  // addView (tender);

		  
	 }
	 
	 private Context context;
	 private int textSize;
	 private int textColor;
}
