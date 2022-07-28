import {
  requireNativeComponent,
  UIManager,
  Platform,
  ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-tf-scanner' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

type TfScannerProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'TfScannerView';

export const TfScannerView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<TfScannerProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
