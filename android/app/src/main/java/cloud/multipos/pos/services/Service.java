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
import cloud.multipos.pos.db.*;
import cloud.multipos.pos.util.*;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.*;
import java.io.InputStream;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;


public abstract class Service extends Thread {

	 public abstract boolean process (ServiceMessage message);
	 public abstract boolean ready ();

	 public Service () {

		  queue = new ArrayList <ServiceMessage> ();
	 }

	 @Override
	 public void run () {

		  Logger.d ("service run... " + this.getClass ().getName ());
		  
		  Looper.prepare ();
		  handler = new Handler () {
					 
					 @Override
					 public void handleMessage (Message m) {
						  						  
						  if (queue.size () > 0) {

								ServiceMessage message = queue.get (0);
																
								if (process (message)) {
									 queue.remove (0);
								}
						  }
					 }
				};
		  
		  
		  try {
		  		Looper.loop ();
		  } 
		  catch (Throwable t) {
				
		  		Logger.w ("service failure... " + t);
				t.printStackTrace ();
				Looper.loop ();
		  }
	 }

	 public void poke () {
		  handler.sendMessage (Message.obtain ());
	 }

	 public void q (int messageType) {
		  
		  queue.add (new ServiceMessage (messageType, null, null, true));
		  handler.sendMessage (Message.obtain ());

	 }

	 public void q (int messageType, Handler h) {

		  queue.add (new ServiceMessage (messageType, null, h, true));
		  
		  if (handler != null) {
				handler.sendMessage (Message.obtain ());
		  }
	 }

	 public void q (int messageType, Jar e) {

		  queue.add (new ServiceMessage (messageType, e, null, true));

		  if (handler != null) {
				handler.sendMessage (Message.obtain ());
		  }
	 }

	 public void q (int messageType, Jar e, Handler h) {

		  queue.add (new ServiceMessage (messageType, e, h, true));

		  if (handler != null) {
				handler.sendMessage (Message.obtain ());
		  }
	 }


	 public void q (int messageType, Jar e, Handler h, boolean disposable) {

		  queue.add (new ServiceMessage (messageType, e, h, disposable));
		  if (handler != null) {
				handler.sendMessage (Message.obtain ());
		  }
	 }

	 public void qWithDelay (int messageType, int delay) {

		  queue.add (new ServiceMessage (messageType, null, null, true));
		  if (handler != null) {
				handler.sendMessageDelayed (Message.obtain (), delay);
		  }
	 }

	 public int queueLength () {
		  return queue.size ();
	 }

	 public void pause (long t) {
		  
		  try { Thread.sleep (t); } catch (Exception e) { }
	 }

	 public class ServiceMessage {

		  public ServiceMessage (int type) {
				this.type = type;
		  }
		  
		  public ServiceMessage (int type, Jar entity, Handler handler, boolean disposable) {

				this.type = type;
				this.entity = entity;
				this.handler = handler;
				this.disposable = disposable;
		  }

		  public int type;
		  public Jar entity;
		  public Handler handler;
		  public boolean disposable = false;
	 }
	 
	 public Handler handler () { return handler; }

	 private Handler handler = null;
	 private ArrayList <ServiceMessage> queue = new ArrayList <ServiceMessage> ();
}
