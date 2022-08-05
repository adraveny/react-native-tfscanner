package com.reactnativetfscanner;

import android.content.Context;

public class TfScannerImageAnalyzerBuilder {
  private TfScannerImageAnalyzer tfScannerImageAnalyzer;
  public TfScannerImageAnalyzerBuilder(Context context, OverlayView overlayView) {
    super();
    tfScannerImageAnalyzer = new TfScannerImageAnalyzer(context, overlayView);
  }

  public TfScannerImageAnalyzer build() {
    tfScannerImageAnalyzer.setupObjectDetector();
    return tfScannerImageAnalyzer;
  }

  public TfScannerImageAnalyzerBuilder setModelPath(String modelPath) {
    tfScannerImageAnalyzer.setModelPath(modelPath);
    return this;
  }

  public TfScannerImageAnalyzerBuilder setNumThreads(int numThreads) {
    tfScannerImageAnalyzer.setNumThreads(numThreads);
    return this;
  }

  public TfScannerImageAnalyzerBuilder setThreshold(float threshold) {
    tfScannerImageAnalyzer.setThreshold(threshold);
    return this;
  }

  public TfScannerImageAnalyzerBuilder setMaxResults(int maxResults) {
    tfScannerImageAnalyzer.setMaxResults(maxResults);
    return this;
  }

  public TfScannerImageAnalyzerBuilder setCurrentDelegate(String currentDelegate) {
    tfScannerImageAnalyzer.setCurrentDelegate(currentDelegate);
    return this;
  }

  public static TfScannerImageAnalyzerBuilder fromOptions(TfScannerImageAnalyzerOptions options,
                                                          Context context,
                                                          OverlayView overlayView) {
    return new TfScannerImageAnalyzerBuilder(context, overlayView)
      .setModelPath(options.getModelPath())
      .setNumThreads(options.getNumThreads())
      .setThreshold(options.getThreshold())
      .setMaxResults(options.getMaxResults())
      .setCurrentDelegate(options.getCurrentDelegate());
  }
}
