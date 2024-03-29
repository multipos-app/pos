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
 
package cloud.multipos.pos.surveillance

import cloud.multipos.pos.Pos
import cloud.multipos.pos.util.*
import cloud.multipos.pos.devices.*

import android.content.Context
import android.content.Intent
import android.hardware.display.VirtualDisplay
import android.hardware.display.DisplayManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Message
import android.util.SparseIntArray
import android.util.DisplayMetrics
import android.view.Surface
import java.io.IOException
import java.io.File

/**
 *
 * merge...
 *
 * https://unix.stackexchange.com/questions/233832/merge-two-video-clips-into-one-placing-them-next-to-each-other
 * ffmpeg -i left.mp4 -i right.mp4 -filter_complex hstack output.mp4
 * ffmpeg -i cam_1650286959.mp4 -i cam_1650287439.mp4 -filter_complex hstack output.mp4
 * ffmpeg -i tab_1650321685.mp4 -i cam_1650321763.mp4 -filter_complex hstack output.mp4
 *
 * ffmpeg \
 * -i tab_1650321685.mp4 \
 * -i cam_1650321763.mp4 \
 * -filter_complex '[0:v]pad=iw*2:ih[int];[int][1:v]overlay=W/2:0[vid]' \
 * -map '[vid]' \
 * -c:v libx264 \
 * -crf 23 \
 * -preset veryfast \
 * output.mp4
 *
 */

class ScreenCapture : VideoListener {
	 
    var screenHandler: ScreenHandler
    private val screenDensity: Int
    private val projectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaProjectionCallback: MediaProjection.Callback? = null
    private val mediaRecorder: MediaRecorder
    private val reset = false
    private val fnum = 1
    private val count = 0
    private var resultCode = 0
    private var intent: Intent? = null
    private var fname: String = ""
    private var startTime: Long = 0
	 private var recording = false
	 private var duration = 0L
	 private var timestamp = 0L

    protected var screenRecorder: ScreenCapture

    init {
		  
        screenRecorder = this
        screenHandler = ScreenHandler ()
        mediaRecorder = MediaRecorder ()
        projectionManager = Pos.app.activity.getSystemService (Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val metrics = DisplayMetrics ()
        Pos.app.activity.windowManager.defaultDisplay.getMetrics (metrics)
        screenDensity = metrics.densityDpi
        val intent = projectionManager.createScreenCaptureIntent ()
        Pos.app.activity.startActivityForResult (intent, INIT)
    }
	 
	 inner class ScreenHandler : Handler () {
		  
        override fun handleMessage (msg: Message) {
								
				when (msg.what) {

					 INIT -> {

						  val data = msg.obj as ScreenInit

						  screenRecorder.init (data)
					 }

					 START_RECORDING -> {

						  val data = msg.obj as StartRecord

						  if (!recording) {

								recording = true
								timestamp = data.timestamp
								duration = data.duration
								screenRecorder.start ()
						  }
						  // else {

						  // 		timestamp = data.timestamp
						  // }
					 }

					 STOP_RECORDING -> {

						  stop ()
					 }
				}
        }
    }

    private fun init (data: ScreenInit) {
		  
		  resultCode = data.resultCode
		  intent = data.intent		  
   }

    private fun start () {
		  
		  fname = Pos.app.activity.filesDir.toString () + "/tab_" + timestamp.toString () + ".mp4"
        Logger.d ("screen record... " + fname)
		  
        mediaRecorder.setOnInfoListener (InfoListener ())
        mediaRecorder.setVideoSource (MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat (MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setVideoSize (DISPLAY_WIDTH, DISPLAY_HEIGHT)
        mediaRecorder.setVideoEncoder (MediaRecorder.VideoEncoder.H264)
		  
        mediaRecorder.setVideoEncodingBitRate (128 * 1000)
        mediaRecorder.setVideoFrameRate (10)
        mediaRecorder.setMaxDuration (duration.toInt () * 1000)
		  
        val rotation = Pos.app.activity.windowManager.defaultDisplay.rotation
        val orientation = ORIENTATIONS[rotation + 90]
        mediaRecorder.setOrientationHint (orientation)
		  
        mediaRecorder.setOutputFile (fname)
		  
        mediaRecorder.prepare ()
        mediaProjectionCallback = MediaProjectionCallback ()
		  val mp = projectionManager.getMediaProjection (resultCode, intent!!)
        mp.registerCallback (mediaProjectionCallback, screenHandler)
		  mediaProjection = mp
        virtualDisplay = createVirtualDisplay ()
        mediaRecorder.start ()
    }

	 private fun stop () {
		  
        mediaRecorder.reset ()
        // mediaRecorder.stop ()
        // stopScreenSharing ()
	 }
	 
    private fun stopScreenSharing () {
		  
        if (virtualDisplay == null) {
				
            return
        }
		  
        virtualDisplay!!.release ()
        destroyMediaProjection ()
    }

    private fun createVirtualDisplay (): VirtualDisplay? {
		  
        if (mediaProjection == null) {
				
            Logger.w ("recorder failed to get media projection...")
            return null
        }
		  
        return mediaProjection!!.createVirtualDisplay ("POS",
																		 DISPLAY_WIDTH,
																		 DISPLAY_HEIGHT,
																		 screenDensity,
																		 DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
																		 mediaRecorder.surface,
																		 null,
																		 null
        )
    }

    private inner class InfoListener : MediaRecorder.OnInfoListener {
		  
        override fun onInfo (mr: MediaRecorder, what: Int, extra: Int) {

            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

					 val f = "tab_" + timestamp.toString () + ".mp4"

					 
		  			 VideoUtils.upload (fname,
											  timestamp.toString (),
											  Pos.app.dbname (),
											  Pos.app.buID ().toString (),
											  Pos.app.posNo ().toString (),
											  VideoUtils.SCREEN,
											  true,
											  screenRecorder);
					 
					 Pos.app.cloudService.postVideo (f,
																timestamp,
																System.currentTimeMillis () / 1000,
																VideoUtils.SCREEN)

					 // set up for next capture
					 
					 timestamp += duration
						  
                mediaRecorder.stop ()
                mediaRecorder.reset ()
                stopScreenSharing ()
					 
					 start ()
            }
        }
    }

    private fun destroyMediaProjection () {
		  
        if (mediaProjection != null) {
				
            mediaProjection!!.unregisterCallback (mediaProjectionCallback)
            mediaProjection!!.stop ()
            mediaProjection = null
        }
    }

    private inner class MediaProjectionCallback : MediaProjection.Callback () {
		  
        override fun onStop () {
				
            mediaRecorder.reset ()
            mediaProjection = null
            stopScreenSharing ()
        }
    }
	 
	 override fun result(result: VideoResult, resultText: String) {
	 }
	 
    companion object {
		  
        const val INIT = 1000
        const val START_RECORDING = 1001
        const val STOP_RECORDING = 1002
		  
        private const val DISPLAY_WIDTH = 640
        private const val DISPLAY_HEIGHT = 480
        private const val REQUEST_PERMISSIONS = 10
        private val ORIENTATIONS = SparseIntArray ()

        init {
				
            ORIENTATIONS.append (Surface.ROTATION_0, 90)
            ORIENTATIONS.append (Surface.ROTATION_90, 0)
            ORIENTATIONS.append (Surface.ROTATION_180, 270)
            ORIENTATIONS.append (Surface.ROTATION_270, 180)
        }
    }
}
