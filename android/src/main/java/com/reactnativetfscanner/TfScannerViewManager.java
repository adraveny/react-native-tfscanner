package com.reactnativetfscanner;

import com.reactnativetfscanner.TfScannerImageAnalyzer;

import android.graphics.Color;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.Map;

public class TfScannerViewManager extends ViewGroupManager<FrameLayout> {
  public static final String REACT_CLASS = "TfScannerView";
  public final int COMMAND_CREATE = 1;
  private TfScannerFragment tfScannerFragment = null;

  private TfScannerImageAnalyzerOptions analyzerOptions;
  private OverlayView.OverlayViewParams overlayViewParams;

  ReactApplicationContext reactContext;
  public TfScannerViewManager(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
    analyzerOptions = new TfScannerImageAnalyzerOptions();
    overlayViewParams = new OverlayView.OverlayViewParams();
  }

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  /**
   * Return a FrameLayout which will later hold the Fragment
   */
  @Override
  public FrameLayout createViewInstance(ThemedReactContext reactContext) {
    return new FrameLayout(reactContext);
  }

  /**
   * Map the "create" command to an integer
   */
  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of("create", COMMAND_CREATE);
  }

  /**
   * Handle "create" command (called from JS) and call createFragment method
   */
  @Override
  public void receiveCommand(
    @NonNull FrameLayout root,
    String commandId,
    @Nullable ReadableArray args
  ) {
    super.receiveCommand(root, commandId, args);
    int reactNativeViewId = args.getInt(0);
    int commandIdInt = Integer.parseInt(commandId);

    switch (commandIdInt) {
      case COMMAND_CREATE:
        createFragment(root, reactNativeViewId);
        break;
      default: {}
    }
  }

  public void createFragment(FrameLayout root, int reactNativeViewId) {
    ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId);
    setupLayout(parentView);

    tfScannerFragment = new TfScannerFragment(analyzerOptions, overlayViewParams);
    FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
    activity.getSupportFragmentManager()
      .beginTransaction()
      .replace(reactNativeViewId, tfScannerFragment, String.valueOf(reactNativeViewId))
      .commit();
  }

  public void setupLayout(ViewGroup view) {
    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        manuallyLayoutChildren(view);
        view.getViewTreeObserver().dispatchOnGlobalLayout();
        Choreographer.getInstance().postFrameCallback(this);
      }
    });
  }

  /**
   * Layout all children properly
   */
  public void manuallyLayoutChildren(ViewGroup view) {
    for(int i=0; i < view.getChildCount(); i++) {
      View child = view.getChildAt(i);
      child.measure(
        View.MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
      child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
    }
    view.measure(
      View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
  }

  private TfScannerImageAnalyzer getImageAnalyzer() {
    if(tfScannerFragment == null) {
      return null;
    }
    return tfScannerFragment.getTfScannerView().getController().getImageAnalyzer();
  }

  private OverlayView getOverlayView() {
    if(tfScannerFragment == null) {
      return null;
    }
    return tfScannerFragment.getTfScannerView().getOverlayView();
  }

  @ReactProp(name = "modelPath")
  public void setModelPath(ViewGroup view, String modelPath) {
    if(tfScannerFragment == null) {
      analyzerOptions.setModelPath(modelPath);
      return;
    }
    getImageAnalyzer().setModelPath(modelPath);
  }

  @ReactProp(name = "detectionThreshold", defaultFloat = 0.5f)
  public void setDetectionThreshold(ViewGroup view, float detectionThreshold) {
    if(tfScannerFragment == null) {
      analyzerOptions.setThreshold(detectionThreshold);
      return;
    }
    getImageAnalyzer().setThreshold(detectionThreshold);
  }

  @ReactProp(name = "currentDelegate")
  public void setCurrentDelegate(ViewGroup view, String currentDelegate) {
    if(currentDelegate == null) {
      return;
    }
    if(tfScannerFragment == null) {
      analyzerOptions.setCurrentDelegate(currentDelegate);
      return;
    }
    getImageAnalyzer().setCurrentDelegate(currentDelegate);
  }

  @ReactProp(name = "numThreads", defaultInt = 2)
  public void setNumThreads(ViewGroup view, int numThreads) {
    if(tfScannerFragment == null) {
      analyzerOptions.setNumThreads(numThreads);
      return;
    }
    getImageAnalyzer().setNumThreads(numThreads);
  }

  @ReactProp(name = "maxResults", defaultInt = 2)
  public void setMaxResults(ViewGroup view, int maxResults) {
    if(tfScannerFragment == null) {
      analyzerOptions.setMaxResults(maxResults);
      return;
    }
    getImageAnalyzer().setMaxResults(maxResults);
  }

  private int getColorFromString(String color) {
    return Color.parseColor(color.toUpperCase());
  }

  @ReactProp(name = "textBackgroundPaintColor")
  public void setTextBackgroundPaintColor(ViewGroup view, String textBackgroundPaintColor) {
    if(textBackgroundPaintColor == null) {
      return;
    }
    if(tfScannerFragment == null) {
      overlayViewParams.setTextBackgroundPaintColor(
        getColorFromString(textBackgroundPaintColor));
      return;
    }
    OverlayView overlayView = getOverlayView();
    overlayView.setTextBackgroundPaintColor(getColorFromString(textBackgroundPaintColor));
    overlayView.clear();
  }

  @ReactProp(name = "textSize", defaultFloat = 50f)
  public void setTextSize(ViewGroup view, float textSize) {
    if(tfScannerFragment == null) {
      overlayViewParams.setTextSize(textSize);
      return;
    }
    OverlayView overlayView = getOverlayView();
    overlayView.setTextSize(textSize);
    overlayView.clear();
  }

  @ReactProp(name="textColor")
  public void setTextColor(ViewGroup view, String textColor) {
    if(textColor == null) {
      return;
    }
    if(tfScannerFragment == null) {
      overlayViewParams.setTextColor(getColorFromString(textColor));
      return;
    }
    OverlayView overlayView = getOverlayView();
    overlayView.setTextColor(getColorFromString(textColor));
    overlayView.clear();
  }

  @ReactProp(name = "boxColor")
  public void setBoxColor(ViewGroup view, String boxColor) {
    if(boxColor == null) {
      return;
    }
    if(tfScannerFragment == null) {
      overlayViewParams.setBoxColor(getColorFromString(boxColor));
      return;
    }
    OverlayView overlayView = getOverlayView();
    overlayView.setBoxColor(getColorFromString(boxColor));
    overlayView.clear();
  }

  @ReactProp(name = "strokeWidth", defaultFloat = 8F)
  public void setStrokeWidth(ViewGroup view, float strokeWidth) {
    if(tfScannerFragment == null) {
      overlayViewParams.setStrokeWidth(strokeWidth);
      return;
    }
    OverlayView overlayView = getOverlayView();
    overlayView.setStrokeWidth(strokeWidth);
    overlayView.clear();
  }
}
