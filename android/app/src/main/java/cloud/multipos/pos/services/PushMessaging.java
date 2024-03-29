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
 
package cloud.multipos.pos.services;

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.controls.*;

// import android.content.Context;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.iid.FirebaseInstanceId;
// import com.google.android.gms.tasks.OnCompleteListener;
// import com.google.android.gms.tasks.Task;
// import com.google.firebase.iid.FirebaseInstanceId;
// import com.google.firebase.iid.InstanceIdResult;
// import com.google.firebase.messaging.FirebaseMessaging;
// import com.google.firebase.messaging.FirebaseMessagingService;
// import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// public class PushMessaging extends FirebaseMessagingService {
public class PushMessaging {

	 public PushMessaging () { }

	 // @Override
	 // public void onMessageReceived (RemoteMessage message) {
		  
	 // 	  Logger.d ("fcm message... " + message.getData ().toString ());
		  
	 // 	  Map <String, String> m = message.getData ();
	 // 	  Params p = null;
		  
	 // 	  for (String key : m.keySet ()) {
				
	 // 			Logger.d ("push keys: " + key + " => " + m.get (key));

	 // 			switch (m.get (key)) {

	 // 			case "update":

	 // 				 Pos.app.cloudService.posUpdate ();
	 // 				 break;
					 
	 // 			// case "control":

	 // 			// 	 p = new Params (m.get ("control").toString ());
	 // 			// 	 Pos.app.sendMessage (PosConst.CONTROL, p);
	 // 			// 	 break;
					 
	 // 			// case "message":

	 // 			// 	 p = new Params (m.get ("message").toString ());
	 // 			// 	 Pos.app.sendMessage (PosConst.MESSAGE, p);
	 // 			// 	 break;
					 
	 // 			// case "send_log":
					 
	 // 			// 	 FileUtils.uploadLog ();
	 // 			// 	 break;
					 
	 // 			// case "pipe":
					 
	 // 			// 	 p = new Params (m.get ("pipe").toString ());					 
	 // 			// 	 FileUtils.pipe (p.getString ("command"));
	 // 			// 	 break;
	 // 			}
	 // 	  }
	 // }

	 private String pushToken;
}
