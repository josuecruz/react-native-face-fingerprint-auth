package com.rnFaceFingerprintAuth;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricPrompt.AuthenticationCallback;
import androidx.biometric.BiometricPrompt.PromptInfo;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

/**
 * Created by Kanga Dominique Bernard.
 */

public class ReactNativeFaceFingerprintAuth extends ReactContextBaseJavaModule {

    protected String biometricKeyAlias = "biometric_key";
    protected String EMPTY_STRING = "";

    @interface AuthPromptOptions {
        String TITLE = "title";
        String SUBTITLE = "subTitle";
        String DESCRIPTION = "description";
        String CONFIRMATION_REQUIRED ="confirmationRequired";
        String CANCEL_BUTTON_TEXT ="cancelButtonText";
        String ALLOW_DEVICE_CREDENTIALS ="allowDeviceCredentials";
        String SIGNATURE ="signature";
        String NONE_SIGNATURE ="no sign";
    }


    public ReactNativeFaceFingerprintAuth(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNFaceFingerprintAuth";
    }

    @ReactMethod
    public void isBiometryAvailable(final ReadableMap params, final Promise promise) {
        try {
            if (isCurrentSDKMarshmallowOrLater()) {
                boolean allowDeviceCredentials = params.getBoolean("allowDeviceCredentials");
                ReactApplicationContext reactApplicationContext = getReactApplicationContext();
                BiometricManager biometricManager = BiometricManager.from(reactApplicationContext);
                int canAuthenticate = biometricManager.canAuthenticate(getAllowedAuthenticators(allowDeviceCredentials,true));

                if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                    WritableMap resultMap = new WritableNativeMap();
                    resultMap.putBoolean("available", true);
                    resultMap.putString("biometryType", "Biometrics");
                    promise.resolve(resultMap);
                } else {
                    WritableMap resultMap = new WritableNativeMap();
                    resultMap.putBoolean("available", false);

                    switch (canAuthenticate) {
                        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                            resultMap.putString("error", "BIOMETRIC_ERROR_NO_HARDWARE");
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                            resultMap.putString("error", "BIOMETRIC_ERROR_HW_UNAVAILABLE");
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                            resultMap.putString("error", "BIOMETRIC_ERROR_NONE_ENROLLED");
                            break;
                    }

                    promise.resolve(resultMap);
                }
            } else {
                WritableMap resultMap = new WritableNativeMap();
                resultMap.putBoolean("available", false);
                resultMap.putString("error", "Unsupported android version");
                promise.resolve(resultMap);
            }
        } catch (Exception e) {
            promise.reject("Error detecting biometrics availability: " + e.getMessage(), "Error detecting biometrics availability: " + e.getMessage());
        }
    }

    @ReactMethod
    public void getPublicKey(final ReadableMap params, Promise promise) {
        try {
            if (isCurrentSDKMarshmallowOrLater()) {
                deleteBiometricKey();
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(biometricKeyAlias, KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                        .setUserAuthenticationRequired(true)
                        .build();
                keyPairGenerator.initialize(keyGenParameterSpec);

                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                byte[] encodedPublicKey = publicKey.getEncoded();
                String publicKeyString = Base64.encodeToString(encodedPublicKey, Base64.DEFAULT);
                publicKeyString = publicKeyString.replaceAll("\r", "").replaceAll("\n", "");

                WritableMap resultMap = new WritableNativeMap();
                resultMap.putString("publicKey", publicKeyString);
                promise.resolve(resultMap);
            } else {
                promise.reject("Cannot generate keys on android versions below 6.0", "Cannot generate keys on android versions below 6.0");
            }
        } catch (Exception e) {
            promise.reject("Error generating public private keys: " + e.getMessage(), "Error generating public private keys");
        }
    }

    private boolean isCurrentSDKMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @ReactMethod
    public void deleteKeys(Promise promise) {
        if (doesBiometricKeyExist()) {
            boolean deletionSuccessful = deleteBiometricKey();

            if (deletionSuccessful) {
                WritableMap resultMap = new WritableNativeMap();
                resultMap.putBoolean("keysDeleted", true);
                promise.resolve(resultMap);
            } else {
                promise.reject("Error deleting biometric key from keystore", "Error deleting biometric key from keystore");
            }
        } else {
            WritableMap resultMap = new WritableNativeMap();
            resultMap.putBoolean("keysDeleted", false);
            promise.resolve(resultMap);
        }
    }

    @ReactMethod
    public void getDataSignatureUsingBiometric(final ReadableMap params, final Promise promise) {
        if (isCurrentSDKMarshmallowOrLater()) {
            UiThreadUtil.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String payload = params.getString("payload");

                                Signature signature = Signature.getInstance("SHA256withRSA");
                                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                                keyStore.load(null);

                                PrivateKey privateKey = (PrivateKey) keyStore.getKey(biometricKeyAlias, null);
                                signature.initSign(privateKey);

                                BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(signature);

                                AuthenticationCallback authCallback = new DataSignatureCallback(promise, payload);
                                FragmentActivity fragmentActivity = (FragmentActivity) getCurrentActivity();
                                Executor executor = Executors.newSingleThreadExecutor();
                                BiometricPrompt biometricPrompt = new BiometricPrompt(fragmentActivity, executor, authCallback);

                                biometricPrompt.authenticate(getPromptInfo(params,true),cryptoObject);
                            } catch (Exception e) {
                                promise.reject("Error signing payload: " + e.getMessage(), "Error generating signature: " + e.getMessage());
                            }
                        }
                    });
        } else {
            promise.reject("Cannot generate keys on android versions below 6.0", "Cannot generate keys on android versions below 6.0");
        }
    }


    @ReactMethod
    public void getDataSignature(final ReadableMap params, final Promise promise) {
        if (isCurrentSDKMarshmallowOrLater()) {
            UiThreadUtil.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String payload = params.getString("payload");

                                Signature signature = Signature.getInstance("SHA256withRSA");
                                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                                keyStore.load(null);

                                PrivateKey privateKey = (PrivateKey) keyStore.getKey(biometricKeyAlias, null);
                                signature.initSign(privateKey);

                                BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(signature);

                                Signature cryptoSignature = cryptoObject.getSignature();
                                cryptoSignature.update(payload.getBytes());
                                byte[] signed = cryptoSignature.sign();
                                String signedString = Base64.encodeToString(signed, Base64.DEFAULT);
                                signedString = signedString.replaceAll("\r", "").replaceAll("\n", "");

                                WritableMap resultMap = new WritableNativeMap();
                                resultMap.putBoolean("success", true);
                                resultMap.putString("signature", signedString);
                                promise.resolve(resultMap);
                            } catch (Exception e) {
                                promise.reject("Error signing payload: " + e.getMessage(), "Error generating signature: " + e.getMessage());
                            }
                        }
                    });
        } else {
            promise.reject("Cannot generate keys on android versions below 6.0", "Cannot generate keys on android versions below 6.0");
        }
    }

    private PromptInfo getPromptInfo(@Nullable final ReadableMap option,boolean isBiometric) {
        return getPromptInfos(option,isBiometric);
    }

    private int getAllowedAuthenticators(boolean allowDeviceCredentials,boolean isBiometric) {
        if(!isBiometric){
            if (allowDeviceCredentials && !isCurrentSDK29OrEarlier()) {
                return BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
            }
            return BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK;
        }else{
            if (allowDeviceCredentials && !isCurrentSDK29OrEarlier()) {
                return BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
            }
            return BiometricManager.Authenticators.BIOMETRIC_STRONG;
        }

    }

    private int getAllowedAuthenticatorsForSignature(boolean allowDeviceCredentials) {
        if (allowDeviceCredentials && !isCurrentSDK29OrEarlier()) {
            return BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        }
        return BiometricManager.Authenticators.BIOMETRIC_STRONG;
    }

    private boolean isCurrentSDK29OrEarlier() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q;
    }

    @ReactMethod
    public void faceOrFingerPrintAuth(final ReadableMap params, final Promise promise) {
        if (isCurrentSDKMarshmallowOrLater()) {
            UiThreadUtil.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {

                                AuthenticationCallback authCallback = new SimpleFaceFingerprintAuthCallback(promise);
                                FragmentActivity fragmentActivity = (FragmentActivity) getCurrentActivity();
                                Executor executor = Executors.newSingleThreadExecutor();
                                BiometricPrompt biometricPrompt = new BiometricPrompt(fragmentActivity, executor, authCallback);

                                biometricPrompt.authenticate(getPromptInfo(params,false));
                            } catch (Exception e) {
                                promise.reject("Error displaying local biometric prompt: " + e.getMessage(), "Error displaying local biometric prompt: " + e.getMessage());
                            }
                        }
                    });
        } else {
            promise.reject("Cannot display biometric prompt on android versions below 6.0", "Cannot display biometric prompt on android versions below 6.0");
        }
    }

    @ReactMethod
    public void biometricKeysExist(Promise promise) {
        try {
            boolean doesBiometricKeyExist = doesBiometricKeyExist();
            WritableMap resultMap = new WritableNativeMap();
            resultMap.putBoolean("keysExist", doesBiometricKeyExist);
            promise.resolve(resultMap);
        } catch (Exception e) {
            promise.reject("Error checking if biometric key exists: " + e.getMessage(), "Error checking if biometric key exists: " + e.getMessage());
        }
    }

    protected boolean doesBiometricKeyExist() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            return keyStore.containsAlias(biometricKeyAlias);
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean deleteBiometricKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            keyStore.deleteEntry(biometricKeyAlias);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @NonNull
    protected PromptInfo getPromptInfos(@Nullable final ReadableMap options,boolean isBiometric) {
        final ReadableMap promptInfoOptionsMap = options;

        final PromptInfo.Builder promptInfoBuilder = new PromptInfo.Builder();
        if (null != promptInfoOptionsMap && promptInfoOptionsMap.hasKey(AuthPromptOptions.TITLE)) {
            String promptInfoTitle = promptInfoOptionsMap.getString(AuthPromptOptions.TITLE);
            promptInfoBuilder.setTitle(promptInfoTitle);
        }
        if (null != promptInfoOptionsMap && promptInfoOptionsMap.hasKey(AuthPromptOptions.SUBTITLE)) {
            String promptInfoSubtitle = promptInfoOptionsMap.getString(AuthPromptOptions.SUBTITLE);
            promptInfoBuilder.setSubtitle(promptInfoSubtitle);
        }
        if (null != promptInfoOptionsMap && promptInfoOptionsMap.hasKey(AuthPromptOptions.DESCRIPTION)) {
            String promptInfoDescription = promptInfoOptionsMap.getString(AuthPromptOptions.DESCRIPTION);
            promptInfoBuilder.setDescription(promptInfoDescription);
        }
        if (null != promptInfoOptionsMap && promptInfoOptionsMap.hasKey(AuthPromptOptions.CONFIRMATION_REQUIRED)) {
            Boolean promptInfoConfirmationRequired = promptInfoOptionsMap.getBoolean(AuthPromptOptions.CONFIRMATION_REQUIRED);
            promptInfoBuilder.setConfirmationRequired(promptInfoConfirmationRequired);
        }

        Boolean promptInfoAllowDeviceCredentials =false;
        if (null != promptInfoOptionsMap && promptInfoOptionsMap.hasKey(AuthPromptOptions.ALLOW_DEVICE_CREDENTIALS)) {
            promptInfoAllowDeviceCredentials = promptInfoOptionsMap.getBoolean(AuthPromptOptions.ALLOW_DEVICE_CREDENTIALS);
        }

        if (null != promptInfoOptionsMap && promptInfoOptionsMap.hasKey(AuthPromptOptions.CANCEL_BUTTON_TEXT)) {
            String promptInfoCancelButtonText = promptInfoOptionsMap.getString(AuthPromptOptions.CANCEL_BUTTON_TEXT);
            if (promptInfoAllowDeviceCredentials == false  || isCurrentSDK29OrEarlier()) {
                promptInfoBuilder.setNegativeButtonText(promptInfoCancelButtonText);
            }
        }

        promptInfoBuilder.setAllowedAuthenticators(getAllowedAuthenticators(promptInfoAllowDeviceCredentials,isBiometric));

        final PromptInfo promptInfo = promptInfoBuilder.build();

        return promptInfo;
    }
}
