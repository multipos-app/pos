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
 
package cloud.multipos.pos.devices;

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.Logger;

import android.os.Handler;
import android.os.Message;
import java.util.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.UUID;
import android.bluetooth.*;

/**
 * scanner setup:
 *
 * Manual DS2278.pdf
 *
 * pair... scan pairing barcode on HID Bluetooth Classic, section 5.5
 * setup ... scan SPP BT Classic (Discoverable) barcode, section 5-8
 *
 */

public class Zebra {

	 public Zebra () {
	 }

	 public void start () {

		  Thread thread = new Thread () {

		  			 String scan = "";
					 
		  			 public void run () {
						  
						  status = false;

						  btAdapter = BluetoothAdapter.getDefaultAdapter ();

						  Set <BluetoothDevice> pairedDevices = btAdapter.getBondedDevices ();

						  BluetoothDevice device = null;
						  for (BluetoothDevice tmp : pairedDevices) {

								Logger.d ("zebra paired... " + tmp.getName () + " " + tmp);
				
								if (tmp.getName ().startsWith ("DS2278")) {
									 device = tmp;
									 deviceName = tmp.getName ();
									 break;
								}
						  }

						  if (device == null) {
								Logger.d ("Device not found");
								return;
						  }

						  while (!status) {
								
								Logger.d ("zebra start connect... " + deviceName);
								btSocket = null;
 
								// Get a BluetoothSocket to connect with the given BluetoothDevice

								try {

									 UUID uuid = UUID.fromString (posUuid);
									 btSocket = device.createRfcommSocketToServiceRecord (uuid);
									 Logger.d ("zebra create socket... " + deviceName + " " + btSocket);

								} catch (IOException e) { 
									 Logger.d ("zebra socket create failed " + e.toString ());
								}

								try {
								
									 btSocket.connect ();
									 Logger.d ("zebra connected socket... " + deviceName);
								}
								catch (IOException connectException) {

									 Logger.d ("zebra socket connect failed " + connectException.toString ());
									 
									 try {
										  btSocket.close ();
									 } catch (IOException closeException) { 
										  Logger.d ("zebra socket connect failed " + closeException.toString ());
									 }
								}
						  

								try {
									 
									 btInStream = btSocket.getInputStream ();
									 btOutStream = btSocket.getOutputStream ();
									 status = true;

								} catch (IOException e) { 
									 Logger.d ("zebra I/O stream failed " + e.toString ());
								}

								try { Thread.sleep (3000); } catch (InterruptedException ie) { }
						  }

						  Logger.d ("zebra connect ok... " + deviceName);
		  
		  				  while (true) {
								
		  						int c = readByte ();
						  
		  						if (c == -1) {

		  							 Pos.app.sendMessage (PosConst.SCANNER_OFF_LINE, null);
									 Logger.d ("zebra scanner disconnect... ");
		  							 break;
		  						}
		  						else if (c == 10) {
								
		  							 Pos.app.sendMessage (PosConst.SCAN, scan);
		  							 scan = "";
		  						}
		  						else if (c >= 20) {
		  							 scan += (char) c;
		  						}
						  
		  				  }
		  			 }
		  		};
		  
		  thread.start ();
	 }

	 public void stop () {
		  
        try {

				btInStream.close ();
				btOutStream.close ();
				btSocket.close ();

        } catch (IOException ioe) { 
				Logger.d ("Socket close failed " + ioe.toString ());
        } catch (Exception e) { 
				Logger.d ("Socket close unknown exception " + e.toString ());
		  }
	 }

	 public int readByte (int timeout) {
		   
		  if (!status) return -1;

		  int timer = timeout;

		  while (timeout > 0) {

				try {
				
					 if (btInStream.available () > 0) {
						  int c = btInStream.read ();
						  return c;
					 }
					 else {
						  timeout -= 50;
					 } 
				} catch (IOException e) {
					 Logger.d ("read failed " + e.toString ());
					 return -1;
				}
		  }
		  return -1;
	 }

	 public int readByte () {
		   
		  if (!status) return -1;

		  try {
				int c = btInStream.read ();
				return c;
		  } catch (IOException e) {
				Logger.d ("read failed " + e.toString ());
				return -1;
		  }
	 }

	 public int write (String msg)  throws IOException {
				
		  if (!status) return -1;

		  byte [] buf = msg.getBytes ();
		  int result = write (buf);

		  if ((result < 0) || (result != buf.length)) {
				status = false;
				return -1;
		  }
		  
		  return result;
	 }

	 public int write (byte [] buf) throws IOException {

		  if (!status) return -1;

		  if (btOutStream == null) {
				Logger.d ("write failed, stream not open");
				return -1;
		  }

		  int writeLen = 0;
		  try {

				for (int i=0; i<buf.length; i++) {
					 btOutStream.write ((int) buf [i]);
					 // sleep (50);
				}

        } catch (IOException e) { 
				Logger.d ("write failed " + e.toString ());
				return -1;
		  }
		  return buf.length;
	 }

    public String toHex (byte [] data, int length) {

        StringBuffer tmp = new StringBuffer ();

        for (int i=0;i<length;i++) {
            String h =  Integer.toHexString ((int) (data [i]));
            if (h.length () > 2) {
                h = h.substring (h.length () - 2, h.length ());
            } else if (h.length () == 1) {
                h = "0" + h;
            }
            tmp.append (h);
            tmp.append (" ");
        }
        return tmp.toString ();
    }

	 public void sleep (int ms) { try { Thread.sleep (ms); } catch (InterruptedException ie) { } }

	 public boolean status () { return status; }
	 public void setDeviceName (String deviceName) { this.deviceName = deviceName; }
	 public void setUUID (String uuid) { this.posUuid = posUuid; }

	 private String posUuid = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothAdapter btAdapter;
	 private int REQUEST_ENABLE_BT = 1;
    private InputStream btInStream;
    private OutputStream btOutStream;
	 private BluetoothSocket btSocket = null;
	 private boolean status = false;
	 private String deviceName;
}
