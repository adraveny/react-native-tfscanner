package com.reactnativetfscanner;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;

public class TfScannerView extends FrameLayout {
  private PreviewView mPreviewView;
  private TfScannerController mController;
  private OverlayView mOverlayView;

  public TfScannerView(@NonNull Context context,
                       TfScannerImageAnalyzerOptions analyzerOptions,
                       OverlayView.OverlayViewParams overlayViewParams) {
    super(context);

    mPreviewView = new PreviewView(context);
    mPreviewView.setScaleType(PreviewView.ScaleType.FILL_START);
    mPreviewView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT));

    this.addView(mPreviewView);

    mOverlayView = OverlayView.OverlayViewBuilder.fromParams(context, overlayViewParams).build();
    mOverlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT));

    this.addView(mOverlayView);

    this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT));

    TfScannerImageAnalyzer imageAnalyzer =
      TfScannerImageAnalyzerBuilder.fromOptions(analyzerOptions, context, mOverlayView).build();

    mController = new TfScannerController(context, this, imageAnalyzer);
  }

  public PreviewView getPreviewView() {
    return mPreviewView;
  }
  public OverlayView getOverlayView() { return mOverlayView; }

  public TfScannerController getController() {
    return mController;
  }

  public void onCreate() {
    mController.onCreate();
  }
  public void onDestroy() {
    mOverlayView.clear();
    mController.onDestroy();
  }
}
