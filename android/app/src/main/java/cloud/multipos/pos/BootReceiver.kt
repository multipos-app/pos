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
 
package cloud.multipos.pos

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*

import android.os.Bundle
import android.content.Context 
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.BroadcastReceiver

class BootReceiver (): BroadcastReceiver () {

	 /**
	  *
	  * start app on boot
	  *
	  */
	 
	 override fun onReceive (context: Context, intent: Intent) {
		  
		  Logger.d ("on receive boot... " + intent)
		  
        // if (intent.getAction ().equals (Intent.ACTION_BOOT_COMPLETED)) {
				
		  // 		val i = new Intent ("cloud.multipos.pos.VideoCapture");
				
		  // 		//val i = Intent (context, cloud.multipos.pos.Pos.javaClass)
				
        //     i.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        //     context.startActivity (i);
        // }
    }
}
