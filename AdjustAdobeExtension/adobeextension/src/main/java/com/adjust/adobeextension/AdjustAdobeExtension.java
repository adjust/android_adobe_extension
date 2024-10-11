package com.adjust.adobeextension;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_EXTENSION;

import android.content.Context;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustDeeplink;
import com.adjust.sdk.OnDeeplinkResolvedListener;
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
     * @param adjustDeeplink Deep link object to process
     * @param context Application context
     */
    public static void processDeeplink(AdjustDeeplink adjustDeeplink, Context context) {
        if (!adjustDeeplink.isValid()) {
            Log.error(LOG_EXTENSION, LOG_SOURCE, "processDeeplink: invalid deeplink url");
            return;
        }
        Log.debug(LOG_EXTENSION, LOG_SOURCE, "processDeeplink: " + adjustDeeplink.getUrl());

        // Pass deep link to Adjust in order to potentially reattribute user.
        Adjust.processDeeplink(adjustDeeplink, context);
    }

    /**
     * Process the deeplink that has opened an app and potentially get a resolved link.
     *
     * @param adjustDeeplink Deeplink object to process
     * @param callback  Callback where either resolved or echoed deeplink will be sent.
     * @param context Application context
     */
    public static void processAndResolveDeeplink(AdjustDeeplink adjustDeeplink, Context context, OnDeeplinkResolvedListener callback) {
        if (!adjustDeeplink.isValid()) {
            Log.error(LOG_EXTENSION, LOG_SOURCE, "processAndResolveDeeplink: invalid deeplink url");
            return;
        }
        Log.debug(LOG_EXTENSION, LOG_SOURCE, "processAndResolveDeeplink: " + adjustDeeplink.getUrl());

        // Pass deep link to Adjust in order to potentially reattribute user and get a resolved link.
        Adjust.processAndResolveDeeplink(adjustDeeplink, context, callback);
    }

    /**
     * Method to add global callback parameter that will be sent with each session and event.
     *
     * @param key   Global callback parameter key
     * @param value Global callback parameter value
     */
    public static void addGlobalCallbackParameter(String key, String value) {
        Adjust.addGlobalCallbackParameter(key, value);
    }

    /**
     * Method to add global partner parameter that will be sent with each session and event.
     *
     * @param key   Global partner parameter key
     * @param value Global partner parameter value
     */
    public static void addGlobalPartnerParameter(String key, String value) {
        Adjust.addGlobalPartnerParameter(key, value);
    }

    /**
     * Method to remove global callback parameter from session and event packages.
     *
     * @param key Global callback parameter key
     */
    public static void removeGlobalCallbackParameter(String key) {
        Adjust.removeGlobalCallbackParameter(key);
    }

    /**
     * Method to remove global partner parameter from session and event packages.
     *
     * @param key Global partner parameter key
     */
    public static void removeGlobalPartnerParameter(String key) {
        Adjust.removeGlobalPartnerParameter(key);
    }

    /**
     * Method to remove all added global callback parameters.
     */
    public static void removeGlobalCallbackParameters() {
        Adjust.removeGlobalCallbackParameters();
    }

    /**
     * Called to remove all added global partner parameters.
     */
    public static void removeGlobalPartnerParameters() {
        Adjust.removeGlobalPartnerParameters();
    }

    /**
     * Method used to get Adjust Adobe Extension Config.
     * @return AdjustAdobeExtensionConfig config to initialize Adjust Sdk
     */
    public static AdjustAdobeExtensionConfig getAdjustAdobeExtensionConfig() {
        return adjustAdobeExtensionConfig;
    }
}
