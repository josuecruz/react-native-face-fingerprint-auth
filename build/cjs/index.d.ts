/**
 * Type alias for possible biometry types
 */
export declare type BiometryType = 'TouchID' | 'FaceID' | 'Biometrics';
interface RNBiometricsOptions {
    allowDeviceCredentials?: boolean;
}
interface IsSensorAvailableResult {
    available: boolean;
    biometryType?: BiometryType;
    error?: string;
}
interface CreateKeysResult {
    publicKey: string;
}
interface BiometricKeysExistResult {
    keysExist: boolean;
}
interface DeleteKeysResult {
    keysDeleted: boolean;
}
interface CreateSignatureOptions {
    title: string;
    subTitle?: string;
    description?: string;
    confirmationRequired?: boolean;
    payload: string;
    cancelButtonText?: string;
}
interface CreateSignatureResult {
    success: boolean;
    signature?: string;
    error?: string;
}
interface SimplePromptOptions {
    title: string;
    subTitle?: string;
    description?: string;
    confirmationRequired?: boolean;
    fallbackPromptMessage?: string;
    cancelButtonText?: string;
}
interface SimplePromptResult {
    success: boolean;
    error?: string;
}
/**
 * Enum for touch id sensor type
 */
export declare const TouchID = "TouchID";
/**
 * Enum for face id sensor type
 */
export declare const FaceID = "FaceID";
/**
 * Enum for generic biometrics (this is the only value available on android)
 */
export declare const Biometrics = "Biometrics";
export declare const BiometryTypes: {
    TouchID: string;
    FaceID: string;
    Biometrics: string;
};
export declare module RNFaceFingerprintAuthLegacy {
    /**
     * Returns promise that resolves to an object with object.biometryType = Biometrics | TouchID | FaceID
     * @returns {Promise<Object>} Promise that resolves to an object with details about biometrics available
     */
    function isBiometryAvailable(): Promise<IsSensorAvailableResult>;
    /**
     * Creates a public private key pair,returns promise that resolves to
     * an object with object.publicKey, which is the public key of the newly generated key pair
     * @returns {Promise<Object>}  Promise that resolves to object with details about the newly generated public key
     */
    function getPublicKey(): Promise<CreateKeysResult>;
    /**
     * Returns promise that resolves to an object with object.keysExists = true | false
     * indicating if the keys were found to exist or not
     * @returns {Promise<Object>} Promise that resolves to object with details aobut the existence of keys
     */
    function biometricKeysExist(): Promise<BiometricKeysExistResult>;
    /**
     * Returns promise that resolves to an object with true | false
     * indicating if the keys were properly deleted
     * @returns {Promise<Object>} Promise that resolves to an object with details about the deletion
     */
    function deleteKeys(): Promise<DeleteKeysResult>;
    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.signature,
     * which is cryptographic signature of the payload
     * @param {Object} createSignatureOptions
     * @param {string} createSignatureOptions.promptMessage
     * @param {string} createSignatureOptions.payload
     * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
     */
    function getDataSignatureUsingBiometric(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult>;
    /**
       * Prompts user with biometrics dialog using the passed in prompt message and
       * returns promise that resolves to an object with object.signature,
       * which is cryptographic signature of the payload
       * @param {Object} createSignatureOptions
       * @param {string} createSignatureOptions.payload
       * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
       */
    function getDataSignature(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult>;
    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.success = true if the user passes,
     * object.success = false if the user cancels, and rejects if anything fails
     * @param {Object} simplePromptOptions
     * @param {string} simplePromptOptions.promptMessage
     * @param {string} simplePromptOptions.fallbackPromptMessage
     * @returns {Promise<Object>}  Promise that resolves an object with details about the biometrics result
     */
    function faceOrFingerPrintAuth(simplePromptOptions: SimplePromptOptions): Promise<SimplePromptResult>;
}
export default class RNFaceFingerprintAuth {
    allowDeviceCredentials: boolean;
    /**
     * @param {Object} rnBiometricsOptions
     * @param {boolean} rnBiometricsOptions.allowDeviceCredentials
     */
    constructor(rnBiometricsOptions?: RNBiometricsOptions);
    /**
     * Returns promise that resolves to an object with object.biometryType = Biometrics | TouchID | FaceID
     * @returns {Promise<Object>} Promise that resolves to an object with details about biometrics available
     */
    isBiometryAvailable(): Promise<IsSensorAvailableResult>;
    /**
     * Creates a public private key pair,returns promise that resolves to
     * an object with object.publicKey, which is the public key of the newly generated key pair
     * @returns {Promise<Object>}  Promise that resolves to object with details about the newly generated public key
     */
    getPublicKey(): Promise<CreateKeysResult>;
    /**
     * Returns promise that resolves to an object with object.keysExists = true | false
     * indicating if the keys were found to exist or not
     * @returns {Promise<Object>} Promise that resolves to object with details aobut the existence of keys
     */
    biometricKeysExist(): Promise<BiometricKeysExistResult>;
    /**
     * Returns promise that resolves to an object with true | false
     * indicating if the keys were properly deleted
     * @returns {Promise<Object>} Promise that resolves to an object with details about the deletion
     */
    deleteKeys(): Promise<DeleteKeysResult>;
    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.signature,
     * which is cryptographic signature of the payload
     * @param {Object} createSignatureOptions
     * @param {string} createSignatureOptions.promptMessage
     * @param {string} createSignatureOptions.payload
     * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
     */
    getDataSignatureUsingBiometric(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult>;
    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.signature,
     * which is cryptographic signature of the payload
     * @param {Object} createSignatureOptions
     * @param {string} createSignatureOptions.payload
     * @returns {Promise<Object>}  Promise that resolves to an object cryptographic signature details
     */
    getDataSignature(createSignatureOptions: CreateSignatureOptions): Promise<CreateSignatureResult>;
    /**
     * Prompts user with biometrics dialog using the passed in prompt message and
     * returns promise that resolves to an object with object.success = true if the user passes,
     * object.success = false if the user cancels, and rejects if anything fails
     * @param {Object} simplePromptOptions
     * @param {string} simplePromptOptions.promptMessage
     * @param {string} simplePromptOptions.fallbackPromptMessage
     * @returns {Promise<Object>}  Promise that resolves an object with details about the biometrics result
     */
    faceOrFingerPrintAuth(simplePromptOptions: SimplePromptOptions): Promise<SimplePromptResult>;
}
export {};
