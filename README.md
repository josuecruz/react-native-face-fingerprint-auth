
# react-native-face-fingerprint-auth

With react-native-face-fingerprint-auth you have the possibility to use the authentication system by biometrics (face/fingerprint/iris), face Id, fingerprint, face, schema, pin stored in your phone. Using open source projet https://source.android.com/docs/security/biometric

##### Demo

![react-native-face-fingerprint-auth](https://user-images.githubusercontent.com/27282579/186913741-964f2bcf-20e5-492d-8f5b-cd4b2b016192.gif)


# Getting started
Start by installing react-native-face-fingerprint-auth using the following command:

#### 1. Usage
- install the latest **react-native-face-fingerprint-auth** using `npm install react-native-face-fingerprint-auth` or `yarn add react-native-face-fingerprint-auth`
- import **RNFaceFingerprintAuth, {
  BiometryTypes}** from 'react-native-face-fingerprint-auth';


### 2. Biometry Types
Detects the type of biometric sensor available on your phone.
**BiometryTypes** is a string indicating what type of biometric data is available in a phone: **TouchID, FaceID, Biometrics, or undefined** if biometrics is not available.

##### Example

```js
const rnFaceFingerprintAuth = new ReactNativeBiometrics()

rnFaceFingerprintAuth.isBiometryAvailable()
  .then((result) => {
    const { available, biometryType } = result

    if (available && biometryType === BiometryTypes.TouchID) {
      console.log('TouchID is supported')
    } else if (available && biometryType === BiometryTypes.FaceID) {
      console.log('FaceID is supported')
    } else if (available && biometryType === BiometryTypes.Biometrics) {
      console.log('Biometrics is supported')
    } else {
      console.log('Biometrics not supported')
    }
  })
```

### 3. Face or fingerprint or iris authentification
authenticate yourself using the type of biometric sensor available on your phone (**TouchID, FaceID, Biometrics**).

##### Example

```js
const rnFaceFingerprintAuth = new ReactNativeBiometrics()

rnFaceFingerprintAuth.
  .faceOrFingerPrintAuth({
        title: "Biometric login for my app",
        subTitle: "Log in using your biometric credential",//Optionnel
        description: "",//Optionnel
        cancelButtonText: "Use account password",//Optionnel
        confirmationRequired:false,//Optionnel
        allowDeviceCredentials:false//Optionnel default false
  })
  .then(result => {
        const { success } = result;

        if (success) {
          console.log("authentification reussit");
        } else {
          console.log("l'utilisateur a annuler");
        }
  })
  .catch(() => {
      console.log("erreur d'authentification !");
});
```

