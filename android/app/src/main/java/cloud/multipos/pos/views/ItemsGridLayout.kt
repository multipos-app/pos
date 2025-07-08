/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
import cloud.multipos.pos.db.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.graphics.Color
import android.widget.GridView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import java.io.*;
import android.widget.BaseAdapter

class ItemsGridLayout (jar: Jar, posMenus: PosMenus, tabs: List <Jar>, attrs: AttributeSet): PosLayout (Pos.app.activity, attrs) {
	 
	 val item = Control.factory ("DefaultItem")
	 val items: MutableList <View> = mutableListOf ()
	 var grid: GridView

	 init {

		  Logger.x ("items grid... ${jar}")
		  
		  Pos.app.inflater.inflate (R.layout.items_grid_layout, this)
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  
		  grid = findViewById (R.id.items_grid) as GridView
	 	  grid.setNumColumns (6)
		  grid.setAdapter (ListAdapter (Pos.app.activity))

		  val select = """
		  select id, sku, item_desc from items 
		  where enabled = "true" and department_id = ${jar.getInt ("department_id")} 
		  order by item_desc"""
		  
		  var itemsResult = DbResult (select, Pos.app.db)
		  
	 	  while (itemsResult.fetchRow ()) {

		  		items.add (ItemAction (itemsResult.row (), item))
		  }
	 }

	 inner open class ItemAction (item: Jar, itemControl: Control): LinearLayout (Pos.app.activity) {
		  
		  init {
				
				val fname = "${Pos.app.imageDir}/${item.getString ("sku")}.png"

				if (FileUtils.imageExists (fname)) {
					 
					 Pos.app.inflater.inflate (R.layout.item_image_action, this)
					 var imageActionView = findViewById (R.id.item_image_action_view) as ImageView
					 imageActionView.setScaleType (ScaleType.FIT_XY)
					 var imageFile = File (fname)
					 var bitmap = BitmapFactory.decodeFile (imageFile.getAbsolutePath ())
					 imageActionView.setImageBitmap (bitmap)
					 var imageText = findViewById (R.id.item_image_action_text) as PosText
					 imageText.text = item.getString ("item_desc")
				}
				else {
					 
					 Pos.app.inflater.inflate (R.layout.item_action, this)
					 var button = findViewById (R.id.item_action_button) as PosButton
					 // button.setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
					 button.text = item.getString ("item_desc")
				}

				var action = findViewById (R.id.item_action) as LinearLayout
				action.setClickable (true)
				action.setOnClickListener () {

					 itemControl.action (Jar ()
													 .put ("sku", item.getString ("sku"))
													 .put ("merge_like_items", true))
				}
		  }
	 }
	 
	 inner class ListAdapter (context: Context): BaseAdapter () {
		  
		  override fun getCount (): Int {
				
				return items.size
		  }

		  override fun getItem (position: Int): String? {
				
				return items [position].toString ()
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return items [position]
		  }
	 }
}
