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
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.surveillance.*
import cloud.multipos.pos.devices.DeviceManager

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Button
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import java.net.URL
import android.net.Uri 
import android.os.Looper
import android.os.Handler
import android.os.Message
import android.widget.BaseAdapter
import android.widget.ListView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.VideoView

// class VideoSearch (context: Context, attrs: AttributeSet): PosLayout (context, attrs), VideoListener {

// 	 val PING  = 1000
// 	 val IMAGE = 1001

// 	 var ipBase = ""
// 	 var node = 98
// 	 var count = 1
// 	 val videoLines = mutableListOf <VideoLine> ()
	 
// 	 lateinit var listAdapter: ListAdapter
// 	 lateinit var listView: ListView
// 	 lateinit var progress: ProgressBar

	 // init {
						 
	 // 	  if (Pos.app.config.has ("ip_address")) {
				
	 // 			val base: List <String> = Pos.app.config.getString ("ip_address", "").split(".")								
	 // 			for (i in 0..2) {
					 
	 // 				 ipBase += base [i] + ".";
	 // 			}
								
	 // 			Pos.app.inflater.inflate (R.layout.video_search, this)
				
	 // 			listAdapter = ListAdapter ()
	 // 			listView = findViewById (R.id.video_lines) as ListView
	 // 			listView.setAdapter (listAdapter)
				
	 // 			progress = findViewById (R.id.video_search_progress_bar) as ProgressBar
	 // 			progress.setMax (10)
	 // 			progress.setProgress (count)

	 // 			var scan = findViewById (R.id.video_scan) as Button
	 // 			scan.setOnClickListener {

	 // 				 progress.setVisibility (View.VISIBLE)
	 // 				 search ()
	 // 			}
	 // 	  }

	 // 	   val videoView = findViewById <VideoView> (R.id.video_view)

	 // 		val url = "rtsp://posappliance:posappliance@192.168.2.100:554/axis-media/media.amp?videocodec=h264"
	 
	 // 		val video = Uri.parse (url)
			
	 // 		videoView.setVideoURI (video)
	 // 		videoView.setOnPreparedListener {
				 
	 // 			 videoView.start ()
	 // 		}
	 // }
	 
	 // private val handler = object : Handler () {

	 // 	  override fun handleMessage (message: Message) {
				
	 // 			progress.setProgress (count ++)
				
	 // 			when (message.what) {

	 // 				 PING -> {
						  
	 // 					  val result =  message.obj as Jar

	 // 					  when (result.getInt ("status")) {

	 // 							1, -1 -> {

	 // 								 node ++
	 // 								 search ()
	 // 							}

	 // 							0 -> {

	 // 								 getBitmap (result.getString ("ip"), this)
	 // 							}
	 // 					  }
	 // 				 }

	 // 				 IMAGE -> {

	 // 					  val image = message.obj as VideoImage
	 // 					  videoLines.add (VideoLine (image.ip, image.bitmap))

	 // 					  node ++
	 // 					  search ()
	 // 				 }
	 // 			}
				
	 // 			listAdapter.notifyDataSetChanged ()
	 // 	  }
	 // }

	 // fun search () {

	 // 	  if (node > 105) {

	 // 			Logger.i ("video search complete...")
	 // 			progress.setVisibility (View.GONE)
	 // 			return;
	 // 	  }
		  
	 // 	  var systemReady = Jar ()
	 // 			.put ("apiVersion", "1.0")
	 // 			.put ("context", node.toString ())
	 // 			.put ("method", "systemready")
	 // 			.put ("jar", Jar ()
	 // 						 .put ("timeout", 100))
				
	 // 	  val ip = ipBase + node
		  		  		  
	 // 	  VideoUtils.post (PING,
	 // 							 ip,
	 // 							 "systemready.cgi",
	 // 							 systemReady,
	 // 							 this) 
	 // }
	 
	 // fun getBitmap (ip: String, handler: Handler) {

	 // 	  val post = "http://$ip/axis-cgi/jpg/image.cgi"
	 // 	  // val post = "https://webcams.borealisbroadband.net/arcticvalley/arcticvalleymega.jpg"
		  
	 // 	  val thread = Thread (Runnable {
											
	 // 										Looper.prepare ()
											
	 // 										val input = URL (post).openStream ()

	 // 										val bitmap = Bitmap.createBitmap (BitmapFactory.decodeStream (input))

	 // 										val image = VideoImage (ip, bitmap) 
	 // 										val message = Message.obtain ()
	 // 										message.what = IMAGE
	 // 										message.obj = image
	 // 										handler.sendMessage (message)
	 // 	  }).start ()
	 // }
	 
	 // override fun result (result: VideoResult, resultText: String) {
	 // }

	 // data class VideoImage (var ip: String, var bitmap: Bitmap)
	 
	 // inner class VideoLine (val ip: String?, val imageBitmap: Bitmap?): LinearLayout (Pos.app.activity) {
		  
	 // 	  init {
				
	 // 			Pos.app.inflater.inflate (R.layout.video_image_line, this)

	 // 			var text = findViewById (R.id.video_line_text) as TextView
	 // 			var image = findViewById (R.id.video_line_image) as ImageView

	 // 			var videoIP = ""
	 // 			if (ip != null) {
					 
	 // 				 text.text = ip
	 // 				 videoIP = ip
	 // 			}
				
	 // 			if (imageBitmap != null) {
					 
	 // 				 image.setImageBitmap (imageBitmap)
	 // 			}

	 // 			image.setOnClickListener {

	 // 				 Logger.d ("clicked... $ip")
	 // 				 Pos.app.local.put ("camera_ip", videoIP)		
	 // 				 DeviceManager.videoCapture?.start (Jar ())
	 // 			    Pos.app.setContentView (Pos.app.resourceID (Pos.app.config.getString ("root_layout"), "layout"))
	 // 			}
	 // 	  }

	 // 	  fun bitmap (): Bitmap? { return imageBitmap }
	 // }

	 // inner class ListAdapter (): BaseAdapter () {
		  
	 // 	  override fun getCount (): Int {

	 // 			return videoLines.size
	 // 	  }

	 // 	  override fun getItem (pos: Int): Any {
				
	 // 			return videoLines.get (pos)
	 // 	  }
		  
	 // 	  override fun getItemId (position: Int): Long {
				
	 // 			return position.toLong ()
	 // 	  }
		  
	 // 	  override fun getView (pos: Int, convertView: View?, parent: ViewGroup): View {

	 // 			val videoLine = videoLines.getOrNull (pos)					 
	 // 			return VideoLine (videoLine?.ip, videoLine?.bitmap ())
	 // 	  }
	 // }
// }
