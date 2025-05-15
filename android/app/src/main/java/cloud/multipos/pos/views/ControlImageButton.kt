/*
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

package cloud.multipos.pos.views

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.GridLayout
import android.widget.GridLayout.*
import android.widget.Button
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.graphics.Color
import android.os.Build
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import java.io.File
import android.view.View.OnLongClickListener

/**
 *
 * See PosButton style for setup and padding
 *
 */

class ControlImageButton (params: Jar, controls: MutableMap <String, Control>): ControlButton (params, controls) {

	 override fun draw (params: Jar) {
		  
		  Pos.app.inflater.inflate (R.layout.pos_control_image_button, this)
		  var layoutParams = GridLayout.LayoutParams (GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f),
																	 GridLayout.spec (GridLayout.UNDEFINED, GridLayout.FILL, 1f))
		  layoutParams.height = 0
		  layoutParams.width = 0
		  setLayoutParams (layoutParams)
		  
		  var sku = params.get ("params").getString ("sku")
		  var imageFile = File ("${Pos.app.imageDir}/${sku}.png")
		  
		  if (imageFile.exists ()) {

				Pos.app.inflater.inflate (R.layout.pos_control_image_button, this)
				var imageButton = findViewById (R.id.image_control_button) as ImageView
				imageButton.setScaleType (ScaleType.FIT_XY)
				var bitmap = BitmapFactory.decodeFile (imageFile.getAbsolutePath ())
				imageButton.setImageBitmap (bitmap);
				view = imageButton
		  }
	 }
}
