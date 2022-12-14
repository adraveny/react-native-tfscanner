import React, { useEffect, useRef } from 'react';
import {
  requireNativeComponent,
  UIManager,
  Platform,
  ViewStyle,
  findNodeHandle,
  NativeEventEmitter,
  NativeModules,
  EmitterSubscription,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-tf-scanner' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

type TfScannerProps = {
  style?: ViewStyle;
  modelPath: string;
  numThreads?: number;
  detectionThreshold?: number;
  currentDelegate?: string;
  textBackgroundPaintColor?: string;
  textSize?: number;
  textColor?: string;
  boxColor?: string;
  strokeWidth?: string;
  onObjectDetected?: (event?: any) => any;
};

const ComponentName = 'TfScannerView';

const RNTfScannerView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<TfScannerProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

const createFragment = (viewId: any) => {
  return UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.getViewManagerConfig(ComponentName).Commands.create.toString(),
    [viewId]
  );
};

export function TfScannerView(props: TfScannerProps) {
  const ref = useRef(null);
  const eventListener = useRef<EmitterSubscription | null>(null);
  const { onObjectDetected, ...nativeProps } = props;

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);
  }, []);

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(NativeModules.TfScannerModule);
    const _onObjectDetected = (event: any) => {
      if (onObjectDetected !== undefined) {
        return onObjectDetected(event);
      }
    };
    eventListener.current = eventEmitter.addListener(
      'onObjectDetected',
      _onObjectDetected
    );

    return () => {
      if (eventListener.current !== null) {
        eventListener.current.remove();
      }
    };
  }, [onObjectDetected]);

  return <RNTfScannerView ref={ref} {...nativeProps} />;
}
