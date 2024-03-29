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

import cloud.multipos.pos.R;
import cloud.multipos.pos.*;
import cloud.multipos.pos.models.*;
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.views.PosMenus;
import cloud.multipos.pos.devices.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.android.volley.*;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import java.util.concurrent.locks.*;

import java.net.HttpURLConnection;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.zip.GZIPInputStream;
import android.util.Base64;
import java.util.zip.ZipInputStream;
import java.time.ZonedDateTime;

// import com.google.firebase.FirebaseApp;
// import com.google.firebase.iid.FirebaseInstanceId;
// import com.google.android.gms.tasks.OnCompleteListener;
// import com.google.android.gms.tasks.Task;
// import com.google.firebase.iid.FirebaseInstanceId;
// import com.google.firebase.iid.InstanceIdResult;
// import com.google.firebase.messaging.FirebaseMessaging;
// import com.google.firebase.messaging.FirebaseMessagingService;
// import com.google.firebase.messaging.RemoteMessage;

public class CloudService extends Service implements Device, DeviceCallback {

	 public CloudService () {
		  
		  if (Pos.app.local.has ("update_id")) {
				
				updateID = Pos.app.local.getInt ("update_id", 0);
		  }
		  else {
				
				updateID = 0;
				Pos.app.local.put ("update_id", updateID);
		  }

		  queue = Volley.newRequestQueue (Pos.app.activity);
		  lock = new ReentrantLock ();
		  		  
		  Pos.app.devices.add (this);
		  
		  posapi = Pos.app.local.getString ("posapi", Pos.app.getString ("posapi"));
		  start ();
	 }
	 
	 @Override
	 public DeviceStatus deviceStatus () { return deviceStatus; }
	 @Override
	 public DeviceClass deviceClass ()  { return DeviceClass.Cloud; }
	 @Override
	 public String deviceName () { return "Cloud";  }
	 @Override
	 public int deviceIcon () { return R.string.fa_www; }

	 @Override
	 public void success (Device device) { }
	 
	 @Override
	 public void fail () { }

	 public void deviceStatus (DeviceStatus ds) {

		  deviceStatus = ds;
	 }
	 
	 public void start (Jar jar) {

		  Logger.d ("cloud start... " + updateID);
	  
		  ready = true;
		  Message message = Message.obtain ();
		  Pos.app.sendMessage (PosConst.CLOUD_READY);
		  
		  Logger.d ("cloud service start... " + posapi + " " + Pos.app.getString ("posapi") + " " + updateID);
	 }
	 
	 public void register (String username, String password, CloudListener listener) {

		  Jar update = new Jar ()
				.put ("uname", username)
				.put ("passwd", password);

		  String url = Pos.app.getString ("posapi") + "/pos/register";
		  Logger.d ("register... " + url + " " + username + " " + password);
		  
		  JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST,
																			  url,
																			  update.json (),
																			  new Response.Listener <JSONObject> () {
																					@Override
																					public void onResponse (JSONObject json) {
																						 
																						 Jar response = new Jar (json);

																						 Logger.d ("reg... " + response);

																						 if (response.getInt ("status") != 0) {

																							  Pos.app.toast (Pos.app.getString (response.getString ("status_text")));
																							  listener.cloudResponse (response.getString ("status_text"));
																							  return;
																						 }
																						 
																						 Config config = new Config ();
																						 config
																							  .put ("dbname", response.getString ("dbname"))
																							  .put ("pos_unit", response.get ("pos_unit"));
																						 
																						 Pos.app.config = config;
																						 
																						 Pos.app.configs = response.getList ("pos_configs");
																						 Pos.app.businessUnits = response.getList ("business_units");
																						 Pos.app.sendMessage (PosConst.POS_SETUP);
																					}
																			  },
																			  new Response.ErrorListener () {
																					@Override
																					public void onErrorResponse (VolleyError error) {
																						 
																						 deviceStatus = DeviceStatus.OffLine;
																						 Logger.d ("register fail... " + error);
																						 Logger.w ("error response... " + error);
																					}            
																			  });
			  
		  queue.add (request);
	 }


	 public void upload (Ticket ticket, int posUpdateID) {
		  
		  final int ticketID = ticket.getInt ("id");
		  				
		  ticket
				.put ("pos_unit_id", Pos.app.posUnitID ());

		  Jar t = new Jar ()
				.put ("dbname", Pos.app.dbname ())
				.put ("ticket", ticket.toString ());
		  
		  JsonObjectRequest request = new JsonObjectRequest (Request.Method.PUT,
																			  posapi + "pos/ticket",
																			  t.json (),
																			  new Response.Listener <JSONObject> () {
																					@Override
																					public void onResponse (JSONObject response) {

																						 int status = -1;
																						 Logger.x ("ticket upload... " + response);
																						 try {

																							  status = response.getInt ("status");
																						 }
																						 catch (JSONException je) {
																							  
																							  Logger.w ("error parsing ticket response... " + je);
																						 }
																						 
																						 if ((status == 0) && (posUpdateID > 0)) {
																							  																							  
																							  Pos.app.db ().exec ("delete from pos_updates where id = " + posUpdateID);
																						 }
																						 
																						 deviceStatus = DeviceStatus.OnLine;
																						 online = true;
																						 offline ();
																					}            
																			  },
																			  new Response.ErrorListener () {
																					@Override
																					public void onErrorResponse (VolleyError error) {
																						 																						 
																						 deviceStatus = DeviceStatus.OffLine;
																						 Logger.w ("error ticket upload... " + error);
																						 
																						 Jar posUpdate = new Jar ()
																							  .put ("status", 0)
																							  .put ("update_table", "tickets")
																							  .put ("update_id", ticketID)
																							  .put ("update_action", 0);
																							 
																						 long id = Pos.app.db ().insert ("pos_updates", posUpdate); 
																						 online = false;
																					}            
																			  });
		  
		  queue.add (request);
	 }

	 private void offline () {

		  if (!Pos.app.db ().ready ()) {

		  		return;
		  }

		  boolean updates = false;
		  DbResult updateResult = new DbResult ("select count(*) as offline from pos_updates", Pos.app.db ());
		  if (updateResult.fetchRow ()) {
				
		  		Jar p = updateResult.row ();
				updates = p.getInt ("offline") > 0; 
		  }

		  if (updates) {
		  
				updateResult = new DbResult ("select * from pos_updates order by id asc", Pos.app.db ());
				if (updateResult.fetchRow ()) {
				
					 Jar update = updateResult.row ();
				
					 switch (update.getString ("update_table")) {
					 
					 case "tickets":
					 
						  Ticket t = new Ticket (update.getInt ("update_id"), Ticket.COMPLETE);
						  upload (t, update.getInt ("id"));
						  break;
					 
					 case "items":
					 case "item_prices":
					 case "item_links":
					 
						  long diff = 0;
						  if (downloadCount > update.getInt ("update_id")) {
						  						  
								Pos.app.db ().exec ("delete from " + update.getString ("update_table") + " where id = " + update.getInt ("id"));
								Pos.app.db ().exec ("delete from pos_updates where id = " + update.getInt ("id"));
						  }
					 
						  break;

					 default:
					 
						  Pos.app.db ().exec ("delete from pos_updates where id = " + update.getInt ("id"));
						  break;
					 }
				}
		  }
	 }

	 public void getTicket (int buID, int posNo, int ticketNo) {
		  
		  Jar getTicket = new Jar ()
				.put ("dbname", Pos.app.dbname ())
				.put ("business_unit_id", buID)
				.put ("pos_no", posNo)
				.put ("ticket_no", ticketNo)
				.put ("ticket_type", Pos.app.ticket.getInt ("ticket_type"));
		  
		  Pos.app.sendMessage (PosConst.WAIT, null);
		  
		  JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST,
																			  Pos.app.getString ("posapi") + "get-ticket",
																			  getTicket.json (),
																			  new Response.Listener <JSONObject> () {
																					@Override
																					public void onResponse (JSONObject response) {

																						 // debug (response);
																						 
																						 try {
																							  																							  
																							  if (response.has ("status") && (response.getInt ("status") == 0)) {
																									Pos.app.sendMessage (PosConst.REMOTE_TICKET, response.getString ("ticket"));
																							  }
																							  else {
																									Pos.app.sendMessage (PosConst.TICKET_NOT_FOUND, Pos.app.getString ("not_found"));
																							  }
																						 }
																						 catch (JSONException e) {
																							  Logger.w ("register error... " + e);
																							  return;
																						 }
																					}
																			  },
																			  new Response.ErrorListener () {
																					@Override
																					public void onErrorResponse (VolleyError error) {
																						 
		  																				 Logger.w ("get ticket error... " + error);
																						 deviceStatus = DeviceStatus.OffLine;
																						 Pos.app.sendMessage (PosConst.TICKET_NOT_FOUND, Pos.app.getString ("not_found"));
		  																			}
		  																	  });
		  queue.add (request);
	 }
	 
	 public void serverLog (String log) {

		  if (Pos.app.config == null) return;
		  
		  Jar t = new Jar ()
				.put ("dbname", Pos.app.dbname ())
				.put ("business_unit_id", Pos.app.buID ())
				.put ("pos_no", Pos.app.posNo ())
				.put ("log_text", log);
		  
		  JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST,
																			  Pos.app.getString ("posapi") + "pos-log",
																			  t.json (),
																			  new Response.Listener <JSONObject> () {
																					@Override
																					public void onResponse (JSONObject response) {
																						 online = true;
																					}            
																			  },
																			  new Response.ErrorListener () {
																					@Override
																					public void onErrorResponse (VolleyError error) {
																						 
																						 deviceStatus = DeviceStatus.OffLine;
																						 online = false;
																					}            
																			  });	  
			  
		  queue.add (request);
	 }

	 public void postVideo (String fname, long startTime, long completeTime, int videoType) {
		  
		  Jar video = new Jar ()
				.put ("dbname", Pos.app.dbname ())
				.put ("business_unit_id", Pos.app.buID ())
				.put ("pos_no", Pos.app.posNo ())
				.put ("start_time", startTime)
				.put ("complete_time", completeTime)
				.put ("file_name", fname)
				.put ("video_type", videoType);
		  
		  JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST,
																			  Pos.app.getString ("posapi") + "save-video",
																			  video.json (),
																			  new Response.Listener <JSONObject> () {
																					@Override
																					public void onResponse (JSONObject response) {
																						 online = true;
																					}            
																			  },
																			  new Response.ErrorListener () {
																					@Override
																					public void onErrorResponse (VolleyError error) {
																						 
																						 deviceStatus = DeviceStatus.OffLine;
																						 online = false;
																					}            
																			  });	 
		  queue.add (request);
	 }
	 
	 public void orders (Handler h) {
		  
		  Jar order = new Jar ()
				.put ("dbname", Pos.app.dbname ())
				.put ("business_unit_id", Pos.app.buID ())
				.put ("pos_no", Pos.app.posNo ());
		  
		  JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST,
																			  Pos.app.getString ("posapi") + "orders",
																			  order.json (),
																			  new Response.Listener <JSONObject> () {
																					@Override
																					public void onResponse (JSONObject response) {
																						 																									  
																						 Message message = Message.obtain ();
																						 message.obj = new Jar (response);
																						 h.sendMessage (message);

																					}            
																			  },
																			  new Response.ErrorListener () {
																					@Override
																					public void onErrorResponse (VolleyError error) {
																						 
																						 deviceStatus = DeviceStatus.OffLine;
																						 online = false;
																					}            
																			  });	 
		  queue.add (request);
	 }

	 // public void posUpdate () {
		  
	 // 	  // download (Pos.app.dbname (), Pos.app.buID (), updateID, Pos.app.posConfigID ());
	 // }
	 	 
	 // public void downloads (Handler progressHandler) {
		  
	 // 	  this.progressHandler = progressHandler;
	 // 	  // downloads ();
	 // }
	 
	 // public void downloads () {

	 // 	  Logger.d ("queue download... ");
	 // 	  // download (Pos.app.dbname (), Pos.app.buID (), updateID, Pos.app.posConfigID ());
		  
	 // 	  if (downloadThread == null) {

	 // 			downloadThread = new Thread (() -> {

	 // 					  while (true) {
								
	 // 							download (Pos.app.dbname (), Pos.app.buID (), updateID, Pos.app.posConfigID ());
	 // 							try { Thread.sleep (downloadTimer); } catch (InterruptedException ie) { }
	 // 					  }
	 // 			});
				
	 // 			downloadThread.start ();
	 // 			downloadThread.interrupt ();
	 // 	  }
	 // 	  else {

	 // 			// don't interrupt if off-line, it may cause a tight loop
				
	 // 			if (online) {
					 
	 // 				 downloadThread.interrupt ();
	 // 			}
	 // 	  }			  
	 // }

	 public void upload (String f) {

		  final String fname = f;

		  Thread thread = new Thread () {

		  			 public void run () {

		  				  HttpURLConnection conn = null;
		  				  DataOutputStream dos = null;
		  				  String lineEnd = "\r\n";
		  				  String twoHyphens = "--";
		  				  String boundary = "*****";
		  				  int bytesRead, bytesAvailable, bufferSize;
		  				  byte [] buffer;
		  				  int maxBufferSize = 1 * 1024 * 1024; 
		  				  String file = fname;
		  				  int serverResponseCode = 0;

		  				  try { 
                    								
						  		File f = new File (Pos.app.activity.getFilesDir () + "/" + fname);
								FileInputStream fin = new FileInputStream (f);

		  				  		String uploadURL = posapi + "pos/upload/" + Pos.app.dbname () + "/" + fname;
								
								Logger.d ("upload file... " + uploadURL);

								URL url = new URL (uploadURL);
                    
		  				  		conn = (HttpURLConnection) url.openConnection ();
		  				  		conn.setRequestMethod ("POST");
		  				  		conn.setDoInput (true);
		  				  		conn.setDoOutput (true);
		  				  		conn.setUseCaches (false);
		  				  		conn.setRequestProperty ("Connection", "Keep-Alive");
		  				  		conn.setRequestProperty ("ENCTYPE", "multipart/form-data");
		  				  		conn.setRequestProperty ("Content-Type", "multipart/form-data;boundary=" + boundary);
		  				  		conn.setRequestProperty ("uploaded_file", fname); 
 
		  				  		dos = new DataOutputStream (conn.getOutputStream ());
          
		  				  		dos.writeBytes (twoHyphens + boundary + lineEnd); 
		  				  		dos.writeBytes ("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fname +"\"" + lineEnd);
		  				  		dos.writeBytes (lineEnd);

		  				  		bytesAvailable = fin.available (); 
		  				  		bufferSize = Math.min (bytesAvailable, maxBufferSize);
		  				  		buffer = new byte [bufferSize];
          
		  				  		bytesRead = fin.read (buffer, 0, bufferSize);  
		  				  		int total = bytesRead;

		  				  		while (bytesRead > 0) {

		  				  			 dos.write (buffer, 0, bufferSize);
		  				  			 bytesAvailable = fin.available ();
		  				  			 bufferSize = Math.min (bytesAvailable, maxBufferSize);
		  				  			 bytesRead = fin.read (buffer, 0, bufferSize); 
  
		  				  			 total += bytesRead;
                  
		  				  		}
          
		  				  		dos.writeBytes (lineEnd);
		  				  		dos.writeBytes (twoHyphens + boundary + twoHyphens + lineEnd);
		  				  		dos.writeBytes (lineEnd);

		  				  		serverResponseCode = conn.getResponseCode ();
		  				  		String serverResponseMessage = conn.getResponseMessage ();

		  				  		fin.close ();
		  				  		dos.flush ();
		  				  		dos.close ();
                     
		  				  }
		  				  catch (MalformedURLException ex) {
		  				  		Logger.w ("Bad url " + ex.toString ());
		  				  } 
		  				  catch (Exception e) {
		  				  		Logger.w ("Unknown exception " + e.toString ());
		  				  }
		  			 }
		  		};
		  
		  thread.start  ();
	 }

	 public static final int DONWLOAD_TIMER_INIT = 10000;   // timer without push messages
	 public static final int DONWLOAD_TIMER_PUSH = 600000;  // timer with push messages
	 
	 private boolean debug = true;
	 private void debug (String s) { if (debug) { Logger.d (s); } }
	 private void debug (JSONObject s) { if (debug) { Logger.d (s); } }
	 
	 private String posapi;
	 public String posapi () { return posapi; }
	 public void posapi (String p) {

		  posapi = p;
		  Pos.app.local.put ("posapi", p);
	 }

	 private RequestQueue queue;
	 protected static int count = 0;
	 protected static int total = 0;
	 private int updateID = -1;
	 private int downloadCount = 0;
	 private String token = null;
	 private Handler progressHandler;
	 private boolean online = false;
	 private boolean stopped = false;
	 private Thread downloadThread;
	 private Thread messageThread;
	 private boolean ready;
	 private int downloadTimer = DONWLOAD_TIMER_INIT;
	 private Lock lock;
	 private JsonObjectRequest downloadRequest;
	 private DeviceStatus deviceStatus = DeviceStatus.Unknown;
	 
	 public boolean process (ServiceMessage message) { return true; }
	 public boolean ready () { return ready; }
	 public void online (boolean online) { this.online = online; }
	 public boolean online () { return online; }
	 public int downloadCount () { return downloadCount; }

}
