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
import cloud.multipos.pos.controls.Tender;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.content.res.TypedArray;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.LayoutInflater;
import android.widget.Button;

public class TenderControls extends LinearLayout {

	 public TenderControls (Context context, AttributeSet attrs, String controls) {
	  
		  super (context, attrs);
		  this.context = context;
		  
		  LayoutInflater inflater = LayoutInflater.from (context);
        inflater.inflate (Pos.app.resourceID ("tender_controls", "layout"), this);
		  
		  setLayoutParams (new LayoutParams (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		  LinearLayout bottom = (LinearLayout) findViewById (Pos.app.resourceID ("tender_controls_bottom", "id"));	 }

	 private Context context;
}
