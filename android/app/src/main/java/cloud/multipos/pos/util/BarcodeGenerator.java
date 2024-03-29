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
 
package cloud.multipos.pos.util;

import cloud.multipos.pos.*;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Map;
import java.util.EnumMap;

public class BarcodeGenerator {

	 public BarcodeGenerator () { }

	 public Bitmap build (String data) {

		  try {
				return encodeAsBitmap (data, BarcodeFormat.CODE_128, 600, 300);
		  }
		  catch (WriterException we) {
				Logger.w ("error creating bitmap... " + we);
		  }
		  return null;
	 }

    public Bitmap encodeAsBitmap (String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
		  
		  String contentsToEncode = contents;
		  if  (contentsToEncode == null) {
				return null;
		  }
		  
		  Map<EncodeHintType, Object> hints = null;
		  
		  String encoding = guessAppropriateEncoding (contentsToEncode);
		  
		  if  (encoding != null) {
				
				hints = new EnumMap<EncodeHintType, Object> (EncodeHintType.class);
				hints.put (EncodeHintType.CHARACTER_SET, encoding);
		  }
		  
		  MultiFormatWriter writer = new MultiFormatWriter ();
		  BitMatrix result;
		  
		  try {
				
				result = writer.encode (contentsToEncode, format, img_width, img_height, hints);
				
		  }
		  catch (IllegalArgumentException iae) {
				// Unsupported format
				return null;
		  }
		  
		  int width = result.getWidth ();
		  int height = result.getHeight ();
		  int[] pixels = new int[width * height];
		  for  (int y = 0; y < height; y++) {
				
				int offset = y * width;
				for  (int x = 0; x < width; x++) {
					 
					 pixels[offset + x] = result.get (x, y) ? BLACK : WHITE;
				}
		  }

		  Bitmap bitmap = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
		  bitmap.setPixels (pixels, 0, width, 0, 0, width, height);

		  return bitmap;
    }

    private static String guessAppropriateEncoding (CharSequence contents) {
		  
		  for  (int i = 0; i < contents.length (); i++) {
				if  (contents.charAt (i) > 0xFF) {
					 return "UTF-8";
				}
		  }

		  return null;
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

}

