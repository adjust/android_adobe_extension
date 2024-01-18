package com.adjust.adobeextension;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_TAG;

import android.util.Log;

/**
 * The main interface to Adjust Adobe Extension.
 * Use the methods of this class to register Adjust Adobe Extension into your app.
 * See the README for details.
 */
public class AdjustAdobeExtension {

    public static final Class<AdjustAdobeExtensionInternal> EXTENSION = AdjustAdobeExtensionInternal.class;

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
    public static void setConfiguration(final AdjustAdobeExtensionConfig config) {
        if (config == null) {
            Log.d(LOG_TAG, "AdjustAdobeExtensionConfig is null");
            return;
        }

        if (config.getEnvironment() == null) {
            Log.d(LOG_TAG, "AdjustAdobeExtensionConfig environment is null");
            return;
        }

        adjustAdobeExtensionConfig = config;

        Log.d(LOG_TAG, "Adjust Adobe Extension initialized");
    }

    /**
     * Method used to get Adjust Adobe Extension Config.
     * @return AdjustAdobeExtensionConfig config to initialize Adjust Sdk
     */
    public static AdjustAdobeExtensionConfig getAdjustAdobeExtensionConfig() {
        return adjustAdobeExtensionConfig;
    }
}
