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
 
package cloud.multipos.pos.devices.scannerlib;

import cloud.multipos.pos.*;
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.devices.CameraScanner;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import android.os.Message;

/**
 * Barcode Detector.
 */
public class BarcodeScannerProcessor extends VisionProcessorBase<List<Barcode>> {

    private static final String TAG = "BarcodeProcessor";

    private static final String MANUAL_TESTING_LOG = "BarcodeProcessor_LOG";

    private final BarcodeScanner barcodeScanner;

	 private String scannedCode = "";
	 private CameraScanner scanner = null;
	 
    // private ExchangeScannedData exchangeScannedData;

	 public void clear () {

		  scannedCode = "";
	 }
	 
    public BarcodeScannerProcessor (Context context, CameraScanner scanner) {
		  
        super (context);
		  this.scanner = scanner;

        // Comment this code if you want to allow open Barcode format.
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder ()
				.setBarcodeFormats (Barcode.FORMAT_UPC_A,
										 Barcode.FORMAT_UPC_E,
										 Barcode.FORMAT_QR_CODE,
										 Barcode.FORMAT_CODE_39,
										 Barcode.FORMAT_CODE_128)
				.build ();

        barcodeScanner = BarcodeScanning.getClient (options);

        // this.exchangeScannedData = exchangeScannedData;
    }

    @Override
    public void stop () {
        super.stop ();
        barcodeScanner.close ();
    }

    @Override
    protected Task<List<Barcode>> detectInImage (InputImage image) {
        return barcodeScanner.process (image);
    }

    @Override
    protected void onSuccess (
            @NonNull List<Barcode> barcodes, @NonNull GraphicOverlay graphicOverlay) {
		  
        // if (barcodes.isEmpty ()) {
        // }
		  
        for (int i = 0; i < barcodes.size (); ++i) {
				
            Barcode barcode = barcodes.get (i);
            graphicOverlay.add (new BarcodeGraphic (graphicOverlay, barcode));

            if (barcode != null && barcode.getRawValue () != null && !barcode.getRawValue ().isEmpty ()) {
					 						  
					 scannedCode = barcode.getRawValue ();
					 scanner.onScan (scannedCode);
				}
        }
    }

    @Override
    protected void onFailure (@NonNull Exception e) {
	 }
	 
    @Override
    protected void onTimeout () {
    }
}
