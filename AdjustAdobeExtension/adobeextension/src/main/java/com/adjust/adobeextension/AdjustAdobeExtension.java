package com.adjust.adobeextension;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_EXTENSION;

import android.content.Context;
import android.net.Uri;

import com.adjust.sdk.Adjust;
import com.adobe.marketing.mobile.services.Log;

/**
 * The main interface to Adjust Adobe Extension.
 * Use the methods of this class to register Adjust Adobe Extension into your app.
 * See the README for details.
 */
public class AdjustAdobeExtension {

    public static final Class<AdjustAdobeExtensionInternal> EXTENSION = AdjustAdobeExtensionInternal.class;
    public static final String ADOBE_ADJUST_ACTION_TRACK_EVENT = "adj.trackEvent";
    public static final String ADOBE_ADJUST_ACTION_SET_PUSH_TOKEN = "adj.setPushToken";
    public static final String ADOBE_ADJUST_EVENT_TOKEN = "adj.eventToken";
    public static final String ADOBE_ADJUST_PUSH_TOKEN = "adj.pushToken";
    public static final String ADOBE_ADJUST_CURRENCY = "adj.currency";
    public static final String ADOBE_ADJUST_REVENUE = "adj.revenue";
    public static final String ADOBE_ADJUST_EVENT_CALLBACK_PARAM_PREFIX = "adj.event.callback.";
    public static final String ADOBE_ADJUST_EVENT_PARTNER_PARAM_PREFIX = "adj.event.partner.";

    private static final String LOG_SOURCE = AdjustAdobeExtension.class.getSimpleName();

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
            Log.debug(LOG_EXTENSION, LOG_SOURCE, "AdjustAdobeExtensionConfig is null");
            return;
        }

        if (config.getEnvironment() == null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE, "AdjustAdobeExtensionConfig environment is null");
            return;
        }

        adjustAdobeExtensionConfig = config;

        Log.debug(LOG_EXTENSION, LOG_SOURCE, "Adjust Adobe Extension initialized");
    }

    /**
     * Method used to process deep link.
     *
     * @param url Deep link URL to process
     * @param context Application context
     */
    public static void openUrl(Uri url, Context context) {
        // Pass deep link to Adjust in order to potentially reattribute user.
        Log.debug(LOG_EXTENSION, LOG_SOURCE, "openUrl: " + url);
        Adjust.appWillOpenUrl(url, context);
    }

    /**
     * Method used to get Adjust Adobe Extension Config.
     * @return AdjustAdobeExtensionConfig config to initialize Adjust Sdk
     */
    public static AdjustAdobeExtensionConfig getAdjustAdobeExtensionConfig() {
        return adjustAdobeExtensionConfig;
    }
}
