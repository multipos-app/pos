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

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.devices.*
import cloud.multipos.pos.models.*
import cloud.multipos.pos.controls.*

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.FrameLayout
import android.widget.BaseAdapter
import android.widget.GridView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.graphics.Color
import android.widget.TextView
import java.io.File
import android.view.View.OnLongClickListener

class PullTabsWeightView (val inPlay: ArrayList <Jar>,
								  val pending: ArrayList <Jar>,
								  val control: InputListener): PosLayout (Pos.app, null), SwipeListener, ThemeListener {
	 
	 enum class State {
		  Idle,
		  WeighTare,
		  WeighTickets,
		  Finish
	 }
	 
	 val pullTabsInPlay = mutableListOf <View> ()
	 val pullTabsPending = mutableListOf <View> ()
	 val results = mutableListOf <Jar> ()
	 
	 lateinit var auxLayout: LinearLayout
	 lateinit var ticketView : LinearLayout
	 lateinit var acceptButton: PosButton
	 
	 var prompt: PosText
	 var curr = Jar ()
	 var scaleData: PosText

	 var state = State.Idle
 
	 companion object {
		  
		  lateinit var callback: ScalesCallback
		  var weight = 0.0
	 }
	 
	 init {
		  
		  auxLayout = Pos.app.findViewById (R.id.aux_container) as LinearLayout
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  
		  val layout = Pos.app.inflater.inflate (R.layout.pull_tabs_weight_layout, auxLayout)
		  val inPlayGrid = layout.findViewById (R.id.pull_tabs_in_play_grid) as GridView
		  val pendingGrid = layout.findViewById (R.id.pull_tabs_pending_grid) as GridView

		  ticketView = layout.findViewById (R.id.pull_tabs_ticket) as LinearLayout
		  
		  for (ptab in inPlay) {

				var pt = PullTabButton (ptab)				 
				pullTabsInPlay.add (pt)
		  }
						  
		  for (ptab in pending) {
					 
				var pt = PullTabButton (ptab)				 
				pullTabsPending.add (pt)
		  }
				
		  inPlayGrid.setAdapter (ListAdapter (pullTabsInPlay, Pos.app.activity))
		  pendingGrid.setAdapter (ListAdapter (pullTabsPending, Pos.app.activity))
		  
		  prompt = layout.findViewById (R.id.pull_tabs_prompt) as PosText
		  scaleData = layout.findViewById (R.id.pull_tabs_weight) as PosText
		  
		  // when (Themed.theme) {
				
		  // 		Themes.Light -> {
					 
		  // 			 scaleData.setTextColor (Color.BLACK)
		  // 		}
				
		  // 		Themes.Dark -> {
					 
		  // 			 scaleData.setTextColor (Color.WHITE)
					 
		  // 		}
		  // }

		  AuxView.swipeListener = this

		  // scales call back
		  
		  callback = object: ScalesCallback {
				
				override fun scaleData (w: Double) {
					 
					 weight = w
					 
					 when (state) {


						  State.WeighTare -> {
						  
								scaleData.setText (String.format ("%.3f", w))
						  }

						  State.WeighTickets -> {
						  
								scaleData.setText (String.format ("%.3f", w))
						  }
						  else -> { }
					 }
				}
		  }

		  // controls
		  
		  acceptButton = layout.findViewById (R.id.pull_tabs_accept_weight) as PosButton
		  acceptButton.setOnClickListener {

				when (state) {

					 State.Idle -> {
					 
						  state = State.WeighTare
						  results.clear ()
				
						  acceptButton.setText (Pos.app.getString ("accept_weight"))
						  prompt.text = Pos.app.getString ("weighing_tare")
						  DeviceManager.scales?.startCapture (callback, true)
					 }

					 State.WeighTare -> {

						  state = State.WeighTickets
						  prompt.text = Pos.app.getString ("select_ticket")
						  acceptButton.setText (Pos.app.getString ("weighing_tickets"))
						  results.add (Jar ()
												 .put ("item_desc", "TARE")
												 .put ("weight", (weight)))
						  scaleData.setText (String.format ("%.3f", 0.0))
				 }
					 
					 State.WeighTickets -> {
						  
						  prompt.text = Pos.app.getString ("select_ticket")
						  ticketView.removeAllViews ()

						  results.add (Jar ()
												 .copy (curr)
												 .put ("weight", (weight)))

						  scaleData.setText (String.format ("%.3f", 0.0))
					 }

					 State.Finish -> {

					 }
					 
					 else -> { }
				}
		  }

		  val newTare = layout.findViewById (R.id.pull_tabs_new_tare) as PosButton
		  newTare.setOnClickListener {

				state = State.Idle
				acceptButton.setText (Pos.app.getString ("accept_weight"))
				prompt.text = Pos.app.getString ("count_tickets_start")
				prompt.text = Pos.app.getString ("weighing_tare")
				DeviceManager.scales?.startCapture (callback, false)
		  }
		  
		  val complete = layout.findViewById (R.id.pull_tabs_complete) as PosButton
		  complete.setOnClickListener {

				Pos.app.auxView.hide ()
				control.accept (Jar ().put ("results", results))
		  }
  
		  Themed.add (this)
		  Pos.app.auxView.show ()
		  DeviceManager.scales?.startCapture (callback, false)
	 }

	 inner class PullTabButton (item: Jar): LinearLayout (Pos.app) {
		  
		  lateinit var bitmap: Bitmap
		  lateinit var imageButton: ImageView
		  
		  init {

				val fname = "${Pos.app.imageDir}/${item.getString ("sku")}.png"
				if (FileUtils.imageExists (fname)) {
			 
					 var imageFile = File (fname)
					 Pos.app.inflater.inflate (R.layout.pull_tab_image_button, this)					 
					 imageButton = findViewById (R.id.pull_tab_image_name) as ImageView
					 imageButton.setScaleType (ScaleType.FIT_XY)
					 bitmap = BitmapFactory.decodeFile (imageFile.getAbsolutePath ())
					 imageButton.setImageBitmap (bitmap);
					 
					 imageButton.setClickable (true)
					 imageButton.setOnClickListener {

						  if (state == State.WeighTickets) {
						  
								acceptButton.setText (Pos.app.getString ("accept_weight"))
								ticketView.removeAllViews ()
								prompt.text = item.getString ("item_desc")
								ticketView.addView (TicketView (bitmap))
								curr = item
								DeviceManager.scales?.startCapture (callback, false)
						  }
					 }

					 val pullTabName = findViewById (R.id.pull_tab_name) as PosText
					 pullTabName.text = item.getString ("item_desc")
				}
		  }
	 }

	 inner class TicketView (bitmap: Bitmap): LinearLayout (Pos.app) {

		  init {
				
				var layout = Pos.app.inflater.inflate (R.layout.pull_tabs_current, this)			 
				val imageView = findViewById (R.id.pull_tab_curr_image_name) as ImageView
				imageView.setImageBitmap (bitmap)
	
		  }
	 }

	 inner class ListAdapter (val list: List <View>, context: Context): BaseAdapter () {

		  init { }
		  
		  override fun getCount (): Int {
				
				return list.size
		  }

		  override fun getItem (position: Int): String? {
				
				return list [position].toString ()
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (position: Int, view: View?, parent: ViewGroup): View {
				
				return list [position]
		  }
	 }

	 override fun onSwipe (dir: SwipeDir) {
		  
		  when (dir) {

				SwipeDir.Right, SwipeDir.Left -> {
					 
					 DeviceManager.scales?.stopCapture ()
				}
				else -> { }
		  }
	 }
}
