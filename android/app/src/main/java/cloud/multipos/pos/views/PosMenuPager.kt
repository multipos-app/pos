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

import cloud.multipos.pos.R;
import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.FrameLayout

class PosMenuPager (context: Context, attrs: AttributeSet): HorizontalScrollView (context, attrs) {
	 
	 val views: MutableList <PosMenuGrid> = mutableListOf ()
	 
	 init {

		  Pos.app.inflater.inflate (R.layout.pos_menu_pager, this)

		  var layout = findViewById (R.id.pos_menu_tabs) as LinearLayout
	  
		  val name = Attrs.getString (attrs, "menus", "")
		  val menus = Pos.app.config.get (name) as Jar

		  for (menu in menus.getList ("horizontal_menus")) {

				when (menu.getString ("type")) {

					 "controls", "controls_grid" -> {

						  var p = PosMenuGrid (context,
						    						  attrs,
						    						  Pos.app.config.get (menu.getString ("name")))						  

						 
						  layout.addView (p)
						  views.add (p)
					 }
				}
		  }
	 }
}
