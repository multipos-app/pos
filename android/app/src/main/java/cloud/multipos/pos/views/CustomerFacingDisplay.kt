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
 
package cloud.multipos.pos.views

import cloud.multipos.pos.R
import cloud.multipos.pos.Pos
import cloud.multipos.pos.models.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ListView
import android.widget.TextView
import android.graphics.drawable.Drawable
import android.content.res.Resources
import android.view.LayoutInflater
import android.widget.AdapterView
import android.os.Bundle
import android.widget.BaseAdapter
import android.os.Handler
import android.os.Message
import android.os.Looper

// remote display

import android.view.MotionEvent
import android.app.Presentation
import android.media.MediaRouter

import android.widget.VideoView
import android.net.Uri
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import java.io.File

class CustomerFacingDisplay (): CustomerDisplay () {

	 var enabled = false
	 var idle = true
	 var count = 0
	 val completeListener = CompleteListener ()
	 val timeoutHandler = TimeoutHandler ()
	 val videoFileName = "/sdcard/" + Pos.app.getString ("customer_idle_video_name")
	 val videoFile = File (videoFileName)
	 
	 lateinit var customerView: CustomerView
	 lateinit var listAdapter: ListAdapter
	 lateinit var videoView: VideoView
	 lateinit var ticket: Ticket
	 	 
	 override fun deviceName (): String  { return "Elo customer display" }

	 override fun start (jar: Jar) {
		  
        val mediaRouter = Pos.app.getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter		  
        val route = mediaRouter.getSelectedRoute (MediaRouter.ROUTE_TYPE_LIVE_VIDEO)		  
        val display = route.getPresentationDisplay ()
		  
		  if (display != null) {
				
				customerView = CustomerView (Pos.app, display)
				customerView.show ()
				enabled = true
				
				// idle thread puts screen in idle mode
				
				var t = Thread () {
					 
					 var url: String
					 var fname = "/sdcard/" + Pos.app.getString ("customer_idle_video_name")
					 var localFile = File (fname)
					 if (!localFile.exists ()) {

						  url = Pos.app.getString ("customer_idle_video_url_base") + "/" + Pos.app.dbname () + "/" + Pos.app.getString ("customer_idle_video_name")
						  FileDownload ().download (url, fname)
						  timeoutHandler.sendMessage (Message.obtain ())
					 }
					 
					 fname = "/sdcard/" + Pos.app.getString ("customer_bg_name")
					 localFile = File (fname)
					 if (!localFile.exists ()) {
						  
						  url = Pos.app.getString ("customer_idle_video_url_base") + "/" + Pos.app.dbname () + "/" + Pos.app.getString ("customer_bg_name")
						  FileDownload ().download (url, fname)
					 }

					 while (true) {
						  
						  try { Thread.sleep (1000L) } catch (ie: InterruptedException) { }
						  
						  if (!idle && (count ++ > 20)) {
								
								timeoutHandler.sendMessage (Message.obtain ())
								count = 0
						  }
					 }
				}
				
				t.start ()
				success (this)
		  }
	 }
	 
	 override fun text (text: String, lineNo: Int) { }

	 override fun clear () {

		  if (enabled && !idle) {
				
				customerView.clear ()
		  }
	 }
	 
	 override fun update (t: Ticket) {

		  if (enabled) {

				ticket = t
				customerView.update ()
				count = 0
		  }
	 }
	 
	 inner class CustomerView (context: Context, display: android.view.Display): Presentation (context, display) {

		  lateinit var total: TextView
		  lateinit var listView: ListView
		  
		  init {
		  }
		  
		  override fun onCreate (savedInstanceState: Bundle?) {
				
				super.onCreate (savedInstanceState)
				idle ()
		  }

		  fun idle () {

				idle = true
				setContentView (R.layout.customer_idle)				
				videoView = findViewById (R.id.customer_idle_video) as VideoView

				if (videoFile.exists ()) {
					 
					 videoView.setOnCompletionListener (completeListener)				
					 videoView.setVideoPath (videoFileName)
					 videoView.requestFocus ()
		  			 videoView.start ()
				}
		  }

		  fun clear () {

				listAdapter.notifyDataSetChanged ()
		  }
		  
		  fun update () {
				
				if (idle) {
					 
					 setContentView (R.layout.customer_ticket)
					 
					 var fname = "/sdcard/" + Pos.app.getString ("customer_bg_name")
					 var localFile = File (fname)
					 if (localFile.exists ()) {
						  
						  var layout = findViewById (R.id.customer_layout) as LinearLayout
						  var bg = Drawable.createFromPath (fname)
						  layout.setBackground (bg)
					 }
					 
					 total = findViewById (R.id.customer_ticket_total) as TextView
					 listView = findViewById (R.id.customer_ticket_list) as ListView
					 listAdapter = ListAdapter ()
					 listView.setAdapter (listAdapter)
					 idle = false
					 					 
					 if (Pos.app.bu ().get ("jar").has ("cd_left")) {
						  
						  val bannerLayout = findViewById (R.id.customer_ticket_banner) as LinearLayout
						  
						  for (t in Pos.app.bu ().get ("jar").getList ("cd_left")) {
								
								bannerLayout.addView (CustomerBannerLine (context, t))
						  }
					 }
					 
					 listAdapter.notifyDataSetChanged ()
				}
				else {
					 
					 total.setText (ticket.getDouble ("total").toString ())
					 listAdapter.notifyDataSetChanged ()
				}
		  }
	 }

	 inner class ListAdapter (): BaseAdapter () {
		  
		  override fun getCount (): Int {

				if (ticket.tenders.size > 0) {
					 
					 return ticket.items.size + ticket.tenders.size
				}
				else {
					 
					 return ticket.items.size
				}
		  }

		  override fun getItem (pos: Int): Any {

				if (pos < ticket.items.size) {
					 
					 return ticket.items.get (pos)
				}
				else {
					 
					 return ticket.tenders.get (pos - ticket.items.size)
				}
		  }

		  override fun getItemId (position: Int): Long {
				
				return position.toLong ()
		  }

		  override fun getView (pos: Int, convertView: View?, parent: ViewGroup): View {

				if (pos < ticket.items.size) {
					 
					 return CustomerItemLine (Pos.app, ticket.items.get (pos), pos)
				}
				else {

					 return CustomerTenderLine (Pos.app, ticket.tenders.get (pos - ticket.items.size), pos)
				}
		  }
	 }

	 inner class CompleteListener (): MediaPlayer.OnCompletionListener {

		  override fun onCompletion (mplayer: MediaPlayer) {

				videoView.seekTo (0)
				videoView.start ()
		  }
	 }
	 
	 inner class TimeoutHandler (): Handler () {

        override fun handleMessage (msg: Message) {

				if (enabled) {
					 
					 customerView.idle ()
				}
		  }
	 }
}
