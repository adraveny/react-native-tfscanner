import React, { useState, useEffect } from 'react';

import { StyleSheet, View, PermissionsAndroid } from 'react-native';
import { TfScannerView } from 'react-native-tf-scanner';

export default function App() {
  const [hasPermission, setHasPermission] = useState(null);

  useEffect(() => {
    (async () => {
      const grantedCamera = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA
      );
      setHasPermission(grantedCamera === PermissionsAndroid.RESULTS.GRANTED);
    })();
  }, []);

  return (
    <View style={styles.container}>
      {hasPermission && (
        <TfScannerView
          style={styles.box}
          modelPath={'vehicles/model_with_metadata.tflite'}
          detectionThreshold={0.5}
          currentDelegate={'DELEGATE_GPU'}
          onObjectDetected={(event) => console.log(event)}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#ff0000',
  },
  box: {
    marginVertical: 20,
    width: 350,
    height: 500,
  },
});
