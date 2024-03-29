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
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.controls.LoadTicket;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.AdapterView;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

public class TicketItemAddonLine extends TicketLine {

	 public TicketItemAddonLine (Context context, TicketItemAddon tia, int position) {

		  super (context);
		  this.context = context;

		  Pos.app.inflater.inflate (Pos.app.resourceID ("ticket_item_addon_line", "layout"), this);
		  setLayoutParams (new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		  TextView addonExtra = ticketLine ("ticket_item_addon_line_extra");
		  TextView addonDesc = ticketLine ("ticket_item_addon_line_desc");
		  TextView addonAmount = ticketLine ("ticket_item_addon_line_amount");
				
		  if (tia.getInt ("addon_quantity") > 0) {
						  						  
				addonDesc.setText (tia.getString ("addon_description"));
				addonAmount.setText (Strings.currency (tia.extAmount (), false));
						  
				// switch (ti.getInt ("state")) {
								
				// case TicketItem.VOID_ITEM:
								
				// 	 addonDesc.setPaintFlags (desc.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG);
				// 	 addonAmount.setPaintFlags (desc.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG);
				// 	 break;
						  
				// case TicketItem.REFUND_ITEM:
								
				// 	 addonDesc.setTextColor (Pos.app.resources ().getColor (Pos.app.resourceID ("warn", "color")));
				// 	 addonAmount.setTextColor (Pos.app.resources ().getColor (Pos.app.resourceID ("warn", "color")));
				// 	 break;
				// }
		  }
		  // else {
		  // 		desc.setText (ti.getString ("item_desc") + "*");
		  // }
	 }
	 
	 private Context context;
}
