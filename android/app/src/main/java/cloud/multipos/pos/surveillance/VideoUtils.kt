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
import cloud.multipos.pos.surveillance.VideoListener
import cloud.multipos.pos.surveillance.VideoResult
import cloud.multipos.pos.util.*

import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.util.Base64
import org.json.JSONObject

import android.os.Looper
import android.os.Handler
import android.os.Message

object VideoUtils {
	 
	 const val SCREEN = 1
    const val CAMERA = 2
 
    fun upload (
		  
        filename: String,
        ts: String,
        m: String,
        b: String,
        p: String,
        t: Int,
        delete: Boolean,
        listener: VideoListener) {
		  
        val type = t.toString ()
        Logger.d ("upload file... $filename " + File (filename).length ())
		  
        val thread: Thread = object : Thread () {
				
            override fun run () {
					 
                var conn: HttpURLConnection? = null
                var dos: DataOutputStream? = null
                val lineEnd = "\r\n"
                val twoHyphens = "--"
                val boundary = "*****"
                var bytesRead: Int
                var bytesAvailable: Int
                var bufferSize: Int
                val buffer: ByteArray
                val maxBufferSize = 1 * 1024 * 1024
                val file = filename
                var serverResponseCode = 0
					 
                try {
						  
                    val f = File (filename)
                    val fin = FileInputStream (f)
                    val uploadURL =
                        // Pos.app.config.getString ("upload_url")
						  "https://video-dev.posappliance.com" + 
						  "?merchant=" + m +
                    "&business_unit_id=" + b +
                    "&pos_no=" + p +
                    "&timestamp=" + ts +
                    "&video_type=" + type

						  Logger.d ("upload... " + uploadURL)
						  
                    val url = URL (uploadURL)
                    conn = url.openConnection () as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn!!.doInput = true
                    conn.doOutput = true
                    conn.useCaches = false

						  conn.setRequestProperty ("Connection", "Keep-Alive")
		  				  conn.setRequestProperty ("ENCTYPE", "multipart/form-data")
		  				  conn.setRequestProperty ("Content-Type", "multipart/form-data;boundary=" + boundary)
		  				  conn.setRequestProperty ("uploaded_file", filename);
						  
		  				  dos = DataOutputStream (conn.getOutputStream ())
						  
		  				  dos.writeBytes (twoHyphens + boundary + lineEnd); 
		  				  dos.writeBytes ("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + filename +"\"" + lineEnd)
                    dos.writeBytes (lineEnd)
						  
                    bytesAvailable = fin.available ()
                    bufferSize = Math.min (bytesAvailable, maxBufferSize)
                    buffer = ByteArray (bufferSize)
                    bytesRead = fin.read (buffer, 0, bufferSize)
						  
                    var total = bytesRead

						  Logger.d ("write... $total")

                    while (bytesRead > 0) {
								
                        dos.write (buffer, 0, bufferSize)
                        bytesAvailable = fin.available ()
                        bufferSize = Math.min (bytesAvailable, maxBufferSize)
                        bytesRead = fin.read (buffer, 0, bufferSize)
                        total += bytesRead
                    }
						  
                    dos.writeBytes (lineEnd)
                    dos.writeBytes (twoHyphens + boundary + twoHyphens + lineEnd)
                    dos.writeBytes (lineEnd)
                    serverResponseCode = conn.responseCode
                    val serverResponseMessage = conn.responseMessage
                    fin.close ()
                    dos.flush ()
                    dos.close ()
                }
					 catch (ex: MalformedURLException) {
						  
                    listener.result (VideoResult.UploadFail, "video bad url $ex")
                    Logger.s ("video bad url $ex")
                    return
                }
					 catch (e: Exception) {
						  
                    listener.result (VideoResult.UploadFail, "video upload unknown exception $e")
                    Logger.s ("video upload unknown exception $e")
                    return
                }
					 
                listener.result (VideoResult.UploadSuccess, filename)
                if (delete) {
						  
                    File (filename).delete ()
                }
            }
        }
        thread.start ()
    }
	 
    operator fun get (ip: String, query: String, listener: VideoListener) {
		  
        val t: Thread = object : Thread () {
				
            override fun run () {
					 
                var conn: HttpURLConnection? = null
                try {
                    val u = "http://posappliance:posappliance@$ip/axis-cgi/$query"
                    Logger.d ("get... $u")
                    val url = URL (u)
                    conn = url.openConnection () as HttpURLConnection
                    conn!!.requestMethod = "GET"
                    val userCredentials = "posappliance:posappliance"
                    val basicAuth = "Basic " + String (Base64.encode (userCredentials.toByteArray (),Base64.DEFAULT))
                    conn.setRequestProperty ("Authorization", basicAuth)
                    conn.connect ()
                    val status = conn.responseCode
                    Logger.d ("video get response... $status")
						  
                }
					 catch (e: Exception) {
						  
                    listener.result (VideoResult.UploadFail, "")
                    Logger.s ("video get error... $e")
                }
					 finally {
						  
                    conn!!.disconnect ()
                }
            }
        }
		  
        t.start ()
    }

    fun post (ip: String, query: String, p: Jar, listener: VideoListener) {
		  
        val t: Thread = object : Thread () {
				
            override fun run () {
					 
                var conn: HttpURLConnection? = null
					 
                try {
						  
                    val u = "http://posappliance:posappliance@$ip/axis-cgi/$query"
                    val url = URL (u)
                    conn = url.openConnection () as HttpURLConnection
                    conn!!.requestMethod = "POST"
                    val userCredentials = "posappliance:posappliance"
                    val basicAuth = "Basic " + String (Base64.encode (userCredentials.toByteArray (),Base64.DEFAULT))
                    conn.doInput = true
                    conn.doOutput = true
                    conn.useCaches = false
                    conn.setRequestProperty ("Content-Type", "application/json")
                    conn.setRequestProperty ("Authorization", basicAuth);
                    conn.connect ()
						  
                    val out = DataOutputStream (conn.outputStream)
                    out.writeBytes (p.toString ())
                    out.flush ()
                    out.close ()
						  
                    var status = conn.responseCode
                    status = conn.responseCode
						  
                    if (status == 200) {
								
                        val `in` = DataInputStream (conn.inputStream)
                        val r = BufferedReader (InputStreamReader (`in`))
                        val s = StringBuilder ()
                        var line: String?
								
                        while (r.readLine ().also { line = it } != null) {
                            s.append (line).append ('\n')
                        }

								Logger.x ("post result... " + s.toString ())
								
                        listener.result (VideoResult.ConnectSuccess, "")
								
                    }
						  else {
								
                        Logger.s ("video post error... $status $u")
                        listener.result (VideoResult.ConnectFail, "")
                    }
                }
					 catch (e: Exception) {
                    Logger.w ("video connect error... $e")
						  
                    listener.result (VideoResult.ConnectFail, "")
                }
					 finally {
						  
                    conn!!.disconnect ()
                }
            }
        }
        t.start ()
    }
}
