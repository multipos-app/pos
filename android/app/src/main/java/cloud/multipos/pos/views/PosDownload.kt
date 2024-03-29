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
import cloud.multipos.pos.services.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.os.Handler
import android.os.Message

class PosDownload (context: Context, attrs: AttributeSet): PosLayout (context, attrs) {
	 
    val progressText: TextView
	 
	 init {
		  
		  setLayoutParams (LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
		  Pos.app.inflater.inflate (R.layout.pos_download_layout, this)
        progressText = findViewById (R.id.progress_text) as TextView
		  
		  val handler = DownloadHandler ()
		  Pos.app.cloudService.online (true)
		  Downloads.start (handler)
	 }

	 inner class DownloadHandler (): Handler () {
		  
		  override fun handleMessage (message: Message) {
				
				if (message?.obj != null) {
					
					 var pu = message.obj as ProgressUpdate

					 if (pu.progress () >= pu.max ()) {
						  
						  Pos.app.start ()
						  return
					 }
					 else {
						  
						  var p = (pu.progress ().toDouble () / pu.max ().toDouble ()) * 100.0
						  
						  if (p > 100.0) {
								
								p = 100.0
						  }
						  
						  progressText.setText (p.toInt ().toString () + "%")
					 }
				}
		  }
	 }
}
