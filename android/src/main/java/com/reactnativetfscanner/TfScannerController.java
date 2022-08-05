package com.reactnativetfscanner;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TfScannerController {
  private PreviewView mPreviewView;
  private OverlayView mOverlayView;
  private Context mContext;

  private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
  private ImageAnalysis imageAnalysis = null;
  private TfScannerImageAnalyzer imageAnalyzer = null;

  private ExecutorService cameraExecutor = null;

  private Camera mCamera;


  public TfScannerController(@NonNull Context context,
                             TfScannerView tfScannerView,
                             TfScannerImageAnalyzer imageAnalyzer) {
    super();
    mContext = context;
    mPreviewView = tfScannerView.getPreviewView();
    mOverlayView = tfScannerView.getOverlayView();
    this.imageAnalyzer = imageAnalyzer;
  }

  public void onCreate() {
    cameraProviderFuture = ProcessCameraProvider.getInstance(mContext);
    cameraProviderFuture.addListener(() -> {
      try {
        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
        bindPreview(cameraProvider);
      } catch (ExecutionException | InterruptedException e) {
        // No errors need to be handled for this Future.
        // This should never be reached.
      }
    }, ContextCompat.getMainExecutor(mContext));
  }

  void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
    Preview preview = new Preview.Builder()
      .build();

    CameraSelector cameraSelector = new CameraSelector.Builder()
      .requireLensFacing(CameraSelector.LENS_FACING_BACK)
      .build();

    preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

    cameraExecutor = Executors.newSingleThreadExecutor();
    imageAnalysis = new ImageAnalysis.Builder()
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
      .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
      .build();
    
    imageAnalysis.setAnalyzer(cameraExecutor, imageAnalyzer);

    mCamera = cameraProvider.bindToLifecycle((LifecycleOwner) mContext,
      cameraSelector,
      preview,
      imageAnalysis);
  }

  public void onDestroy() {
    if(imageAnalysis != null) {
      imageAnalysis.clearAnalyzer();
    }
    if(cameraExecutor != null) {
      cameraExecutor.shutdown();
    }
  }

  public TfScannerImageAnalyzer getImageAnalyzer() {
    return imageAnalyzer;
  }
}
