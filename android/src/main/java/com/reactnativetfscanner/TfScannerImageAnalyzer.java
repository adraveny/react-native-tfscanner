package com.reactnativetfscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TfScannerImageAnalyzer implements ImageAnalysis.Analyzer {
  private static final String TAG = "TfScannerImageAnalyzer";
  public static final String DELEGATE_NNAPI = "DELEGATE_NNAPI";
  public static final String DELEGATE_GPU = "DELEGATE_GPU";
  public static final String DELEGATE_CPU = "DELEGATE_CPU";

  private Context mContext;
  private OverlayView mOverlayView;

  private float threshold = 0.50f;
  private int maxResults = 3;
  private int numThreads = 2;
  private String currentDelegate = TfScannerImageAnalyzer.DELEGATE_CPU;
  private String modelPath;

  private ObjectDetector objectDetector = null;


  public TfScannerImageAnalyzer(Context context, OverlayView overlayView) {
    super();

    mContext = context;
    mOverlayView = overlayView;
  }

  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
    resetObjectDetector();
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
    resetObjectDetector();
  }

  public void setMaxResults(int maxResults) {
    this.maxResults = maxResults;
    resetObjectDetector();
  }

  public void setCurrentDelegate(String currentDelegate) {
    this.currentDelegate = currentDelegate;
    resetObjectDetector();
  }

  public void setModelPath(String modelPath) {
    this.modelPath = modelPath;
    resetObjectDetector();
  }

  @SuppressLint("UnsafeOptInUsageError")
  public void analyze(ImageProxy image) {
    if(objectDetector == null) {
      setupObjectDetector();
    }

    ImageProcessor imageProcessor =
      new ImageProcessor.Builder()
        .add(new Rot90Op(- image.getImageInfo().getRotationDegrees() / 90))
        .build();

    Image im = image.getImage();

    Bitmap bitmapBuffer = Bitmap.createBitmap(im.getWidth(), im.getHeight(),
      Bitmap.Config.ARGB_8888);
    bitmapBuffer.copyPixelsFromBuffer(image.getPlanes()[0].getBuffer());
    TensorImage tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmapBuffer));

    List<Detection> detections = objectDetector.detect(tensorImage);

    List<Detection> filteredDetections = new LinkedList<Detection>();

    for(Detection detection : detections) {
      List<Category> categories = detection.getCategories();
      RectF boundingBox = detection.getBoundingBox();
      for(Category category : categories) {
        float score = category.getScore();
        float width = boundingBox.width();
        float height = boundingBox.height();
        float imageHeight = (float) image.getHeight();
        float imageWidth = (float) image.getWidth();
        boolean isBigEnough =  height > 0.1f*imageHeight && width > 0.1f*imageWidth;
        boolean isSmallEnough = height < 0.9f*imageHeight && width < 0.9f*imageWidth;
        if(isBigEnough && isSmallEnough) {
          Log.d(TAG, String.format("Category : %s, score : %f", category.getLabel(), score));
          Log.d(TAG, String.format("Bounding box width: %f, height: %f", width, height));
          filteredDetections.add(detection);
        }
      }
    }
    mOverlayView.setResults(filteredDetections, tensorImage.getHeight(), tensorImage.getWidth());
    mOverlayView.invalidate();

    bitmapBuffer.recycle();
    image.close();
  }

  public void setupObjectDetector() {
    ObjectDetector.ObjectDetectorOptions.Builder optionsBuilder =
      ObjectDetector.ObjectDetectorOptions.builder()
      .setScoreThreshold(threshold)
      .setMaxResults(maxResults);
    BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads);
    switch (currentDelegate) {
      case DELEGATE_GPU:
        if((new CompatibilityList()).isDelegateSupportedOnThisDevice()) {
          baseOptionsBuilder.useGpu();
        } else {
          Log.e(TAG, "GPU is not available");
        }
        break;
      case DELEGATE_NNAPI:
        baseOptionsBuilder.useNnapi();
        break;
      default:
        break;
    }
    optionsBuilder.setBaseOptions(baseOptionsBuilder.build());
    Log.d(TAG, "Loading object detector...");
    try {
      objectDetector = ObjectDetector.createFromFileAndOptions(mContext, modelPath, optionsBuilder.build());
      Log.d(TAG, "Object detector loaded");
    } catch (IOException e) {
      Log.e(TAG, "Couldn't load model file");
    }
  }

  public void resetObjectDetector() {
    if(objectDetector == null) {
      return;
    }
    setupObjectDetector();
  }
}
