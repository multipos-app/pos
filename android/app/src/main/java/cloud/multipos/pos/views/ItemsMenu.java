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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.util.TypedValue;
import android.graphics.Typeface;
import android.graphics.Color;
import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.widget.TextView;

import java.util.*;

public class ItemsMenu extends PosLayout implements PosMenuControl {
	 
	 public ItemsMenu (Context context, AttributeSet attrs, Jar menu, PosLayout tabs, int tabIndex, PosMenus posMenus) {

		  super (context, attrs);
		  
		  // this.menu = menu;
		  // this.posMenus = posMenus;

		  // setLayoutParams (new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		  // setOrientation (LinearLayout.VERTICAL);
 		  
		  // itemControl = new Item ();

		  // if (tabs != null) {
		  // 		addView (tabs);
		  // }

		  // menu ();
	 }
	 	 
	 // public void menu () {
		  	  
	 // 	  int maxButtons = getIntAttr ("num_rows", 5) *  getIntAttr ("num_posMenus.cols ()", 5);
	 // 	  int i = 0;
		  
	 // 	  LinearLayout horz = new LinearLayout (context);
		  
	 // 	  DbResult itemResult  = new DbResult (Pos.app.db
	 // 	  													.find ("items")
	 // 	  													.where (new String [] {"department_id = " + menu.getInt ("department_id")})
	 // 	  													.query (),
	 // 	  													Pos.app.db);
	 // 	  while (itemResult.fetchRow ()) {
				
	 // 	  		Params item = itemResult.row ();
				
	 // 	  		if ((i ++ % posMenus.cols ()) == 0) {
					 
	 // 	  			 horz = new LinearLayout (context);
	 // 	  			 horz.setLayoutParams (new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, 0, 1.0f));
	 // 	  			 horz.setOrientation (LinearLayout.HORIZONTAL);
	 // 	  			 addView (horz);
	 // 	  		}

	 // 	  		horz.addView (new ItemButton (context, item, i, 1.0f));
	 // 	  }
		  
	 // 	  while (i < maxButtons) {  // fill it out
		  				
	 // 	  		if ((i ++ % posMenus.cols ()) == 0) {
					 
	 // 	  			 horz = new LinearLayout (context);
	 // 	  			 horz.setLayoutParams (new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, 0, 1.0f));
	 // 	  			 horz.setOrientation (LinearLayout.HORIZONTAL);
	 // 	  			 addView (horz);
	 // 	  		}
				
	 // 	  		horz.addView (new EmptyButton (context, 1.0f));
	 // 	  }
	 // }
	 
	 public void home () { }
	 public void update () { }
	 public void menu (int menu) { }
	 public void menu (String menu) { }
	 
	 // private class ItemButton extends LinearLayout {

	 // 	  public ItemButton (Context context, Jar item, int index, float width) {
				
	 // 			super (context);
			  			
	 // 			Pos.app.inflater.inflate (Pos.app.resourceID ("item_button", "layout"), this);
	 // 			setBackgroundResource (resources.getIdentifier ("color_" + (index % 10), "color", Pos.app.activity.getPackageName ()));
				
	 // 			LinearLayout.LayoutParams bl = new LinearLayout.LayoutParams (0, posMenus.buttonHeight (), 1.0f);
	 // 			bl.setMargins (posMenus.padding (), posMenus.padding (), posMenus.padding (), posMenus.padding ());
	 // 			setLayoutParams (bl);
	 // 			TextView desc = (TextView) findViewById (Pos.app.resourceID ("item_button_text", "id"));
	 // 			desc.setTextSize (posMenus.textSize ());
	 // 			desc.setText (item.getString ("item_desc"));
	 // 			setElevation (10);

	 // 			setOnClickListener (new ItemClick (item));
				
	 // 	  }
	 // }
	 
	 // private class ItemClick implements OnClickListener {

	 // 	  public ItemClick (Jar item) {
	 // 			this.item = item;
	 // 	  }

	 // 	  public void onClick (View v) {
				
	 // 			if ((itemControl != null) && (itemControl.beforeAction ())) {
					 
	 // 				 itemControl.action (item);
	 // 				 itemControl.afterAction ();
	 // 			}
	 // 	  }

	 // 	  private Params item;
	 // }
	 
	 // public class EmptyButton extends LinearLayout {

	 // 	  public EmptyButton (Context context, float width) {

	 // 			super (context);

	 // 			Pos.app.inflater.inflate (Pos.app.resourceID ("empty_button", "layout"), this);
	 // 			LinearLayout.LayoutParams bl = new LinearLayout.LayoutParams (0, posMenus.buttonHeight (), width);
	 // 			bl.setMargins (posMenus.padding (), posMenus.padding (), posMenus.padding (), posMenus.padding ());
	 // 			setLayoutParams (bl);
	 // 	  }
	 // }

	 private Jar menu;
	 private DefaultItem itemControl;
	 private PosMenus posMenus;

}
