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
import android.widget.TextView;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.view.Gravity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

public class TicketLine extends LinearLayout {

	 public TicketLine (Context context) {

		  super (context);
	 }
	 
	 public TicketLine (Context context, TicketItem ti, int position, ListDisplay listDisplay) {

		  super (context);
		  this.context = context;
		  this.position = position;
		  this.listDisplay = listDisplay;
		  this.posEntity = ti;

		  if (ti.getInt ("state") == TicketItem.VOID_ITEM) { return; }

		  Pos.app.inflater.inflate (Pos.app.resourceID ("ticket_item_line", "layout"), this);
		  
		  TextView quantity = ticketLine ("ticket_item_line_quantity");
		  TextView desc = ticketLine ("ticket_item_line_desc");
		  TextView amount = ticketLine ("ticket_item_line_amount");

		  // if (listDisplay.multiSelect ()) {

				for (Integer i: listDisplay.selectValues ()) {
					 
					 if (position == i.intValue ()) {
						  
						  setBackgroundResource (listDisplay.selectBg ());

						  switch (Pos.app.ticket.getInt ("ticket_type")) {

						  case Ticket.RETURN_SALE:
						  case Ticket.REMOTE_RETURN:
								
								quantity.setTextColor (R.color.warn);
								desc.setTextColor (R.color.warn);
								amount.setTextColor (R.color.warn);

								ti
									 .put ("stte", TicketItem.REFUND_ITEM)
									 .put ("amount", ti.getDouble ("amount") * -1.0);

								break;

						  default:
								break;
						  }
					 }
				}
		  // }
		  // else if (listDisplay.select () == position) {
				
		  // 		setBackgroundResource (listDisplay.selectBg ());
		  // }
		  
		  switch (ti.getInt ("state")) {

		  case TicketItem.VOID_ITEM:

		  		quantity.setPaintFlags (desc.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG);
		  		desc.setPaintFlags (desc.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG);
		  		amount.setPaintFlags (amount.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG);
		  		break;
						  
		  case TicketItem.REFUND_ITEM:
						  
		  		quantity.setTextColor (R.color.warn);
		  		desc.setTextColor (R.color.warn);
		  		amount.setTextColor (R.color.warn);
		  		break;
				
		  default:
				
				quantity.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, listDisplay.fg ()));				
				desc.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, listDisplay.fg ()));				
				amount.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, listDisplay.fg ()));
				break;
		  }
		  
		  quantity.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, listDisplay.fg ()));				
		  desc.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, listDisplay.fg ()));				
		  amount.setTextColor (ContextCompat.getColorStateList (Pos.app.activity, listDisplay.fg ()));

		  quantity.setText (String.valueOf (ti.getInt ("quantity")));

		  String descText = ti.getString ("item_desc");
		  
		  if (ti.getInt ("quantity") > 1) {
				descText += " " + ti.getInt ("quantity") + Pos.app.getString ("quantity_sep") + Strings.currency (ti.getDouble ("amount"), false);
		  }
		  
		  desc.setText (descText);
		  amount.setText (Strings.currency (ti.extAmount (), false));
		  
		  voided = (ti.has ("state") && (ti.getInt ("state") == TicketItem.VOID_ITEM));

		  if (ti.hasAddons ()) {
				
				LinearLayout addonLayout = (LinearLayout) findViewById (Pos.app.resourceID ("ticket_item_addon_layout", "id"));
				for (TicketItemAddon tia: ti.addons) {
					 
					 addonLayout.addView (new TicketItemAddonLine (context, tia, position));
				}
		  }
		  
		  if ((ti.getInt ("flags") & Ticket.EXCHANGE_ITEMS) > 0) {
				
					 desc.setText (ti.getString ("item_desc") + "*");
		  }
		  
		  Logger.x ("ticket line... " + ti);

		  setClickable (true);
		  setOnClickListener (new LineClick (position));
	 }
	 
	 public TicketLine (Context context, TicketTender tender, int position, ListDisplay listDisplay) {

		  super (context);
		  this.context = context;
		  this.position = position;
		  
		  Pos.app.inflater.inflate (Pos.app.resourceID ("tender_line", "layout"), this);
		  
		  TextView desc = ticketLine ("tender_line_desc");
		  TextView amount = ticketLine ("tender_line_amount");
		  desc.setText (Pos.app.getString (tender.getString ("tender_type")));
		  amount.setText (Strings.currency (tender.getDouble ("tendered_amount"), false));
	 }
	 
	 public TicketLine (Context context, Jar entity, int position, ListDisplay listDisplay) {

		  super (context);
		  this.context = context;
		  this.position = position;
		  
		  Pos.app.inflater.inflate (Pos.app.resourceID ("ticket_info_line", "layout"), this);

		  TextView desc = ticketLine ("ticket_info_line_desc");
		  TextView amount = ticketLine ("ticket_info_line_amount");

		  desc.setText (Pos.app.getString (entity.getString ("desc")));
		  amount.setText (entity.getString ("val"));
	 }
	 
	 protected TextView ticketLine (String res) {

		  TextView textView = (TextView) findViewById (Pos.app.resourceID (res, "id"));
		  textView.setTypeface (Views.displayFont ());
		  return textView;
	 }


	 private class LineClick implements OnClickListener {
		  
		  public LineClick (int pos) {
				
				this.pos = pos;
		  }

		  @Override
		  public void onClick (View v) {

				listDisplay.select (position = pos);				
				// if (listDisplay.multiSelect ()) {
					 
				// PosDisplays.selected (pos);
				// }
				// else {
					 
				// 	 Pos.app.ticket.setCurrentItem (pos);
					 
				// 	 if ((Pos.app.ticket.getInt ("state") == Ticket.OPEN) && Pos.app.config.getBoolean ("item_popup_menus")) {
						  
				// 		  new TicketItemDialog ().show ();
				// 	 }
				// }
		  }
		  
		  private int pos;
	 }
 
	 private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		  public GestureListener (int position) {
				
				super ();
				this.position = position;
		  }


        @Override
        public boolean onDown (MotionEvent e) {

				super.onDown (e);
				Pos.app.ticket.setCurrentItem (position);
				listDisplay.select (position);
				return false;
        }

        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
												
				float distanceX = e2.getX () - e1.getX ();
            float distanceY = e2.getY () - e1.getY ();
				
            if (Math.abs (distanceX) > Math.abs (distanceY) && Math.abs (distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs (velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					 
                if (distanceX > 0) {
						  // onSwipeRight ();
					 }
                else {
						  // onSwipeLeft ();
					 }
					 
                return true;
            }
            return false;
        }

        @Override
		  public void onLongPress (MotionEvent e) {

				super.onLongPress (e);

				if ((Pos.app.ticket.getInt ("state") == Ticket.OPEN) &&
					 Pos.app.config.has ("item_popup_menus") &&
					 Pos.app.config.getBoolean ("item_popup_menus")) {
					 
					 // TicketItemDialog tid = new TicketItemDialog ();
					 // tid.show ();
				}

		  }

		  int position;
    }

	 public static TicketLine factory  (Context context, Jar e, int position, ListDisplay listDisplay) {
		  		  
		  if (e instanceof TicketItem) { return new TicketLine (context, (TicketItem) e, position, listDisplay); }
		  else if (e instanceof TicketTender) { return new TicketLine (context, (TicketTender) e, position, listDisplay); }
		  else { return new TicketLine (context, (Jar) e, position, listDisplay); }
		  
	 }
	 
	 private int position;
	 private boolean voided;
	 private Context context;
	 private GestureDetector gestureDetector;
	 private ListDisplay listDisplay;
	 private Jar posEntity;
}
