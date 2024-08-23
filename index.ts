import { NativeModules } from 'react-native'

const { RNFaceFingerprintAuth: bridge } = NativeModules

/**
 * Type alias for possible biometry types
 */
export type BiometryType = 'TouchID' | 'FaceID' | 'Biometrics'

interface RNBiometricsOptions {
  allowDeviceCredentials?: boolean
}

interface IsSensorAvailableResult {
  available: boolean
  biometryType?: BiometryType
  error?: string
}

interface CreateKeysResult {
  publicKey: string
}

interface BiometricKeysExistResult {
  keysExist: boolean
}

interface DeleteKeysResult {
  keysDeleted: boolean
}

interface CreateSignatureOptions {
  title: string
  subTitle?: string
  description?: string
  confirmationRequired?: boolean
  payload: string
  cancelButtonText?: string
}

interface CreateSignatureResult {
  success: boolean
  signature?: string
  error?: string
}

interface SimplePromptOptions {
  title: string
  subTitle?: string
  description?: string
  confirmationRequired?: boolean
  fallbackPromptMessage?: string
  cancelButtonText?: string
}

interface SimplePromptResult {
  success: boolean
  error?: string
}

/**
 * Enum for touch id sensor type
 */
export const TouchID = 'TouchID'
/**
 * Enum for face id sensor type
 */
export const FaceID = 'FaceID'
/**
 * Enum for generic biometrics (this is the only value available on android)
 */
export const Biometrics = 'Biometrics'

export const BiometryTypes = {
  TouchID,
  FaceID,
  Biometrics
}

export module RNFaceFingerprintAuthLegacy {
  /**
   * Returns promise that resolves to an object with object.biometryType = Biometrics | TouchID | FaceID
   * @returns {Promise<Object>} Promise that resolves to an object with details about biometrics available
   */
  export function isBiometryAvailable(): Promise<IsSensorAvailableResult> {
    return new RNFaceFingerprintAuth().isBiometryAvailable()
  }

  /**
   * Creates a public private key pair,returns promise that resolves to
   * an object with object.publicKey, which is the public key of the newly generated key pair
   * @returns {Promise<Object>}  Promise that resolves to object with details about the newly generated public key
   */
  export function getPublicKey(): Promise<CreateKeysResult> {
    return new RNFaceFingerprintAuth().getPublicKey()
  }

  /**
   * Returns promise that resolves to an object with object.keysExists = true | false
   * indicating if the keys were found to exist or not
   * @returns {Promise<Object>} Promise that resolves to object with details aobut the existence of keys
   */
  export function biometricKeysExist(): Promise<BiometricKeysExistResult> {
    return new RNFaceFingerprintAuth().biometricKeysExist()
  }

  /**
   * Returns promise that resolves to an object with true | false
   * indicating if the keys were properly deleted
   * @returns {Promise<Object>} Promise that resolves to an object with details about the deletion
   */
  export function deleteKeys(): Promise<DeleteKeysResult> {
    return new RNFaceFingerprintAuth().deleteKeys()
  }

  /**
   * Prompts user with biometrics dialog using the passed in prompt message and
   * returns promise that resolves to an object with object.signature,
   * which is cryptographic signature of the payload
   * @param {Object} createSignatureOptions
   * @param {string} createSignatureOptions.promptMessage
   * @param {string} createSignatureOptions.payload
   * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
   */
  export function getDataSignatureUsingBiometric(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult> {
    return new RNFaceFingerprintAuth().getDataSignatureUsingBiometric(createSignatureOptions)
  }

/**
   * Prompts user with biometrics dialog using the passed in prompt message and
   * returns promise that resolves to an object with object.signature,
   * which is cryptographic signature of the payload
   * @param {Object} createSignatureOptions
   * @param {string} createSignatureOptions.payload
   * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
   */
 export function getDataSignature(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult> {
  return new RNFaceFingerprintAuth().getDataSignature(createSignatureOptions)
}

  /**
   * Prompts user with biometrics dialog using the passed in prompt message and
   * returns promise that resolves to an object with object.success = true if the user passes,
   * object.success = false if the user cancels, and rejects if anything fails
   * @param {Object} simplePromptOptions
   * @param {string} simplePromptOptions.promptMessage
   * @param {string} simplePromptOptions.fallbackPromptMessage
   * @returns {Promise<Object>}  Promise that resolves an object with details about the biometrics result
   */
  export function faceOrFingerPrintAuth(simplePromptOptions: SimplePromptOptions): Promise<SimplePromptResult> {
    return new RNFaceFingerprintAuth().faceOrFingerPrintAuth(simplePromptOptions)
  }
}

export default class RNFaceFingerprintAuth {
    allowDeviceCredentials = false

    /**
     * @param {Object} rnBiometricsOptions
     * @param {boolean} rnBiometricsOptions.allowDeviceCredentials
     */
    constructor(rnBiometricsOptions?: RNBiometricsOptions) {
      const allowDeviceCredentials = rnBiometricsOptions?.allowDeviceCredentials ?? false
      this.allowDeviceCredentials = allowDeviceCredentials
    }

    /**
     * Returns promise that resolves to an object with object.biometryType = Biometrics | TouchID | FaceID
     * @returns {Promise<Object>} Promise that resolves to an object with details about biometrics available
     */
     isBiometryAvailable(): Promise<IsSensorAvailableResult> {
      return bridge.isBiometryAvailable({
        allowDeviceCredentials: this.allowDeviceCredentials
      })
    }

    /**
     * Creates a public private key pair,returns promise that resolves to
     * an object with object.publicKey, which is the public key of the newly generated key pair
     * @returns {Promise<Object>}  Promise that resolves to object with details about the newly generated public key
     */
     getPublicKey(): Promise<CreateKeysResult> {
      return bridge.getPublicKey({
        allowDeviceCredentials: this.allowDeviceCredentials
      })
    }

    /**
     * Returns promise that resolves to an object with object.keysExists = true | false
     * indicating if the keys were found to exist or not
     * @returns {Promise<Object>} Promise that resolves to object with details aobut the existence of keys
     */
    biometricKeysExist(): Promise<BiometricKeysExistResult> {
      return bridge.biometricKeysExist()
    }

    /**
     * Returns promise that resolves to an object with true | false
     * indicating if the keys were properly deleted
     * @returns {Promise<Object>} Promise that resolves to an object with details about the deletion
     */
    deleteKeys(): Promise<DeleteKeysResult> {
      return bridge.deleteKeys()
    }

    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.signature,
     * which is cryptographic signature of the payload
     * @param {Object} createSignatureOptions
     * @param {string} createSignatureOptions.promptMessage
     * @param {string} createSignatureOptions.payload
     * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
     */
     getDataSignatureUsingBiometric(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult> {
      createSignatureOptions.cancelButtonText = createSignatureOptions.cancelButtonText ?? 'Cancel'

      return bridge.getDataSignatureUsingBiometric({
        allowDeviceCredentials: this.allowDeviceCredentials,
        ...createSignatureOptions
      })
    }

    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.signature,
     * which is cryptographic signature of the payload
     * @param {Object} createSignatureOptions
     * @param {string} createSignatureOptions.payload
     * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
     */
     getDataSignature(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult> {
      createSignatureOptions.cancelButtonText = createSignatureOptions.cancelButtonText ?? 'Cancel'

      return bridge.getDataSignature({
        allowDeviceCredentials: this.allowDeviceCredentials,
        ...createSignatureOptions
      })
    }

    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.success = true if the user passes,
     * object.success = false if the user cancels, and rejects if anything fails
     * @param {Object} simplePromptOptions
     * @param {string} simplePromptOptions.promptMessage
     * @param {string} simplePromptOptions.fallbackPromptMessage
     * @returns {Promise<Object>}  Promise that resolves an object with details about the biometrics result
     */
     faceOrFingerPrintAuth(simplePromptOptions: SimplePromptOptions): Promise<SimplePromptResult> {
      simplePromptOptions.cancelButtonText = simplePromptOptions.cancelButtonText ?? 'Cancel'
      simplePromptOptions.fallbackPromptMessage = simplePromptOptions.fallbackPromptMessage ?? 'Use Passcode'

      return bridge.faceOrFingerPrintAuth({
        allowDeviceCredentials: this.allowDeviceCredentials,
        ...simplePromptOptions
      })
    }
  }
