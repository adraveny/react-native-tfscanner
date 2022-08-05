package com.reactnativetfscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class TfScannerFragment extends Fragment {
  TfScannerView tfScannerView;
  private TfScannerImageAnalyzerOptions analyzerOptions;
  private OverlayView.OverlayViewParams overlayViewParams;


  public TfScannerFragment(TfScannerImageAnalyzerOptions analyzerOptions,
                           OverlayView.OverlayViewParams overlayViewParams) {
    super();
    this.analyzerOptions = analyzerOptions;
    this.overlayViewParams = overlayViewParams;
  }

  public TfScannerView getTfScannerView() {
    return tfScannerView;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);

    tfScannerView = new TfScannerView(this.getContext(), analyzerOptions, overlayViewParams);

    return tfScannerView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    tfScannerView.onCreate();
  }

  @Override
  public void onPause() {
    super.onPause();
    // do any logic that should happen in an `onPause` method
    // e.g.: tfScannerView.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    // do any logic that should happen in an `onResume` method
    // e.g.: tfScannerView.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    tfScannerView.onDestroy();
  }
}
