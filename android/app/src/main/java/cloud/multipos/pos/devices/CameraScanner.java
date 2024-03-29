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
import cloud.multipos.pos.util.*;
import cloud.multipos.pos.models.Item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.util.AttributeSet;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.view.LayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.LinearLayout;

import com.google.mlkit.common.MlKitException;

import cloud.multipos.pos.devices.scannerlib.CameraXViewModel;
import cloud.multipos.pos.devices.scannerlib.ExchangeScannedData;
import cloud.multipos.pos.devices.scannerlib.VisionImageProcessor;
import cloud.multipos.pos.devices.scannerlib.BarcodeScannerProcessor;
import cloud.multipos.pos.databinding.CameraScannerBinding;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.List;

public class CameraScanner extends LinearLayout {

    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private VisionImageProcessor imageProcessor;
    private boolean needUpdateGraphicOverlayImageSourceInfo;

    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;

    private static final String STATE_SELECTED_MODEL = "selected_model";
    private static final String STATE_LENS_FACING = "lens_facing";
    private CameraScannerBinding binding;

	 private ScanListener scanListener;

	 public CameraScanner (Context context, AttributeSet attrs) {
		  
		  super (context, attrs);

		  activity = Pos.app;
		  
		  Logger.d ("camera scanner init...");
		  
		  this.activity = activity;
		  
		  LayoutInflater inflater = (LayoutInflater) activity.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		  		  
		  binding = CameraScannerBinding.inflate (LayoutInflater.from (activity));
		  removeAllViews ();
		  addView (binding.getRoot ());
 		  
        cameraSelector = new CameraSelector.Builder ().requireLensFacing (lensFacing).build ();

        new ViewModelProvider (activity, ViewModelProvider.AndroidViewModelFactory.getInstance (activity.getApplication ()))
                .get (CameraXViewModel.class)
                .getProcessCameraProvider ()
                .observe (
                        activity,
                        provider -> {
                            cameraProvider = provider;
									 bindAllCameraUseCases ();
                        });
	 }

	 public void scanListener (ScanListener scanListener) {

		  this.scanListener = scanListener;
	 }
	 
	 public void onScan (String scanCode) {

		  scanListener.onScan (scanCode);
	 }
	 
	 public void clear () {

		  imageProcessor.clear ();
	 }
	 
    private void bindAllCameraUseCases () {

		  bindPreviewUseCase ();
        bindAnalysisUseCase ();
    }

    private void bindPreviewUseCase () {

        if (cameraProvider == null) {
				
            return;
        }
        if (previewUseCase != null) {
				
            cameraProvider.unbind (previewUseCase);
        }

        previewUseCase = new Preview.Builder ().build ();
		  
        previewUseCase.setSurfaceProvider (binding.cameraPreview.getSurfaceProvider ());
        cameraProvider.bindToLifecycle (activity, cameraSelector, previewUseCase);
		  
		  binding.cameraPreview.setImplementationMode (androidx.camera.view.PreviewView.ImplementationMode.COMPATIBLE);
		  binding.cameraPreview.getParent ().requestTransparentRegion (binding.cameraPreview);
   }

    private void bindAnalysisUseCase () {
		  
        if (cameraProvider == null) {
				
            Logger.w ("no camera provider...");
            return;
        }
        if (analysisUseCase != null) {
				
            cameraProvider.unbind (analysisUseCase);
        }
        if (imageProcessor != null) {
				
            imageProcessor.stop ();
        }

        try {
								
            imageProcessor = new BarcodeScannerProcessor (activity, this);
				
        }
		  catch (Exception e) {
				
            Logger.w ("Can not create image processor..." + e);
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder ();
        analysisUseCase = builder.build ();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer (
												 												 
                ContextCompat.getMainExecutor (activity),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo ().getRotationDegrees ();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            binding.graphicOverlay.setImageSourceInfo (
                                    imageProxy.getWidth (), imageProxy.getHeight (), isImageFlipped);
                        } else {
                            binding.graphicOverlay.setImageSourceInfo (
                                    imageProxy.getHeight (), imageProxy.getWidth (), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy (imageProxy, binding.graphicOverlay);
                    } catch (MlKitException e) {
								
                        Logger.w ("Failed to process image. Error: " + e.getLocalizedMessage ());
                    }
                });

        cameraProvider.bindToLifecycle (activity, cameraSelector, analysisUseCase);
    }

	 public void stop () {
		  
		  if (analysisUseCase != null) {
				
            cameraProvider.unbind (analysisUseCase);
        }
		  
        if (imageProcessor != null) {
				
            imageProcessor.stop ();
        }

        if (previewUseCase != null) {
				
            cameraProvider.unbind (previewUseCase);
        }
	 }

	 private AppCompatActivity activity;
	 public AppCompatActivity activity () { return activity; }
}
