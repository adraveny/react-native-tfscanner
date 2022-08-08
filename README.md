# react-native-tf-scanner
Camera view to scan using a tensorflow lite model

This is a basework for a npm package.

For the moment, only the Android code has been done.
## Installation

```sh
npm install react-native-tf-scanner
```

## Usage

```js
// ...
import { TfScannerView } from "react-native-tf-scanner";

const [hasPermission, setHasPermission] = useState(null);

  useEffect(() => {
    (async () => {
      const grantedCamera = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA
      );
      setHasPermission(
        grantedCamera === PermissionsAndroid.RESULTS.GRANTED
      );    
    })();
  }, []);

  return (
    <View style={styles.container}>
      {hasPermission && <TfScannerView
      style={styles.box}
      modelPath={"vehicles/model_with_metadata.tflite"}
      detectionThreshold={0.5}
      currentDelegate={"DELEGATE_GPU"}
      onObjectDetected={(event) => console.log(event)}
      />}
    </View>
  );

// ...
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
