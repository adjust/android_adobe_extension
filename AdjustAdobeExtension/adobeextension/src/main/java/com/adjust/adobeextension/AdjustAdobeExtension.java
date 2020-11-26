package com.adjust.adobeextension;

import com.adobe.marketing.mobile.ExtensionError;
import com.adobe.marketing.mobile.ExtensionErrorCallback;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_TAG;

/**
 * The main interface to Adjust Adobe Extension.
 * Use the methods of this class to register Adjust Adobe Extension into your app.
 * See the README for details.
 */
public class AdjustAdobeExtension {

    /**
     * Adjust Sdk Config passed to Adobe Adjust Extension.
     * It will be used for initialization of Adjust Sdk.
     */
    private static AdjustAdobeExtensionConfig adjustAdobeExtensionConfig = null;

    /**
     * Private constructor.
     */
    private AdjustAdobeExtension() {
    }

    /**
     * Method used to register Adjust Adobe Extension.
     * @param config extension config to initialize Adjust Sdk
     */
    public static void registerExtension(final AdjustAdobeExtensionConfig config) {
        if (config == null) {
            MobileCore.log(LoggingMode.DEBUG, LOG_TAG,
                    "AdjustAdobeExtensionConfig is null");
            return;
        }

        if (config.getEnvironment() == null) {
            MobileCore.log(LoggingMode.DEBUG, LOG_TAG,
                    "AdjustAdobeExtensionConfig environment is null");
            return;
        }

        ExtensionErrorCallback<ExtensionError> errorCallback =
                new ExtensionErrorCallback<ExtensionError>() {
            @Override

            public void error(final ExtensionError extensionError) {
                MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                        "Failed to register AdjustAdobeExtension" +
                                extensionError != null ?
                                    ", error" + extensionError.getErrorName()
                                    : " without error");
            }};

        if (MobileCore.registerExtension(AdjustAdobeExtensionInternal.class, errorCallback)) {
            adjustAdobeExtensionConfig = config;
        }
    }

    /**
     * Method used to get Adjust Adobe Extension Config.
     * @return AdjustAdobeExtensionConfig config to initialize Adjust Sdk
     */
    public static AdjustAdobeExtensionConfig getAdjustAdobeExtensionConfig() {
        return adjustAdobeExtensionConfig;
    }
}
