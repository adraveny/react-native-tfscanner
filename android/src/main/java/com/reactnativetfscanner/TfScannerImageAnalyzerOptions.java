package com.reactnativetfscanner;

public class TfScannerImageAnalyzerOptions {
  private float threshold = 0.50f;
  private int maxResults = 3;
  private int numThreads = 2;
  private String currentDelegate = TfScannerImageAnalyzer.DELEGATE_CPU;
  private String modelPath;

  public TfScannerImageAnalyzerOptions setNumThreads(int numThreads) {
    this.numThreads = numThreads;
    return this;
  }

  public TfScannerImageAnalyzerOptions setThreshold(float threshold) {
    this.threshold = threshold;
    return this;
  }

  public TfScannerImageAnalyzerOptions setMaxResults(int maxResults) {
    this.maxResults = maxResults;
    return this;
  }

  public TfScannerImageAnalyzerOptions setCurrentDelegate(String currentDelegate) {
    this.currentDelegate = currentDelegate;
    return this;
  }

  public TfScannerImageAnalyzerOptions setModelPath(String modelPath) {
    this.modelPath = modelPath;
    return this;
  }

  public float getThreshold() {
    return threshold;
  }

  public int getMaxResults() {
    return maxResults;
  }

  public int getNumThreads() {
    return numThreads;
  }

  public String getCurrentDelegate() {
    return currentDelegate;
  }

  public String getModelPath() {
    return modelPath;
  }
}
