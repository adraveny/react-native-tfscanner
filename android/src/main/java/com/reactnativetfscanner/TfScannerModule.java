package com.reactnativetfscanner;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class TfScannerModule extends ReactContextBaseJavaModule {
  public TfScannerModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "TfScannerModule";
  }
}
