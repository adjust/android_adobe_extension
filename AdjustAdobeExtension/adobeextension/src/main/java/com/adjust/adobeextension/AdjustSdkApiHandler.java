package com.adjust.adobeextension;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_ACTION_SET_PUSH_TOKEN;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_EVENT_CALLBACK_PARAM;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_EVENT_CURRENCY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_EVENT_PARTNER_PARAM;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_EVENT_REVENUE;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_EVENT_TOKEN;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_PUSH_TOKEN_PARAM;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_EXTENSION;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import com.adobe.marketing.mobile.services.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adobe.marketing.mobile.MobileCore;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal class to interface with Adjust Sdk.
 * It filters configuration & generic track event and delegate those to Adjust extension.
 */
class AdjustSdkApiHandler {
    private static final String LOG_SOURCE = AdjustSdkApiHandler.class.getSimpleName();
    /**
     * Singleton instance of AdjustSdkApiHandler
     */
    private static volatile AdjustSdkApiHandler instance = null;

    /**
     * Flag to indicate if Adjust Sdk has been initialized yet
     */
    private static boolean sdkInitialised = false;

    /**
     * Toggle flag to indicate whether Android Activity has been resumed or paused
     */
    private static boolean activityResumed = false;

    /**
     * Application instance
     */
    private static Application application;

    private AdjustSdkApiHandler() {}

    protected static AdjustSdkApiHandler getInstance() {
        if (instance == null) {
            synchronized (AdjustSdkApiHandler.class) {
                if (instance == null) {
                    instance = new AdjustSdkApiHandler();
                }
            }
        }
        return instance;
    }

    /**
     * Method to init Adjust Sdk for the given app token
     * @param appToken
     * @param shouldTrackAttribution
     */
    protected void initSdk(final String appToken, final boolean shouldTrackAttribution) {
        if (sdkInitialised) {
            Log.warning(LOG_EXTENSION, LOG_SOURCE,"Cannot initialise SDK, already initialised");
            return;
        }

        Application application = MobileCore.getApplication();
        if (application == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE,"Cannot initialise SDK, application object is null");
            return;
        }

        if (appToken == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE,"Cannot initialise SDK, appToken is null or empty");
        }

        AdjustAdobeExtensionConfig adjustAdobeExtensionConfig =
                AdjustAdobeExtension.getAdjustAdobeExtensionConfig();
        if (adjustAdobeExtensionConfig == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE,"Cannot initialise SDK, adjust extension config is null");
        }

        AdjustConfig adjustConfig = getAdjustConfig(application.getApplicationContext(),
                appToken, shouldTrackAttribution, adjustAdobeExtensionConfig);
        adjustConfig.setSdkPrefix(AdjustAdobeExtensionConstants.EXTENSION_VERSION);
        Adjust.onCreate(adjustConfig);

        // there might be a moment when activity is already resumed before Sdk initialization
        if (activityResumed) {
            Adjust.onResume();
        }

        sdkInitialised = true;
    }

    /**
     * Method to process events for Adjust
     * @param action action to take for the event
     * @param contextData map with event params as key-value
     */
    protected void processEvent(final String action, final Map<String, String> contextData) {
        if (action != null && action.equalsIgnoreCase(ADJUST_ACTION_SET_PUSH_TOKEN)) {
            setPushToken(contextData);
        } else {
            trackEvent(contextData);
        }
    }

    /**
     * Method to set user's push notifications token
     * @param contextData map with params as key-value
     */
    protected void setPushToken(final Map<String, String> contextData) {
        if (contextData == null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot set push token, contextData is null");
            return;
        }

        String pushToken = contextData.get(ADJUST_KEY_PUSH_TOKEN_PARAM);
        if (pushToken == null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot set, push token is null");
            return;
        }

        Adjust.setPushToken(pushToken, application.getApplicationContext());
    }

    /**
     * Method to track Adjust events
     * @param contextData map with event params as key-value
     */
    protected void trackEvent(final Map<String, String> contextData) {
        if (contextData == null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot track event, contextData is null");
            return;
        }

        String eventToken = contextData.get(ADJUST_KEY_EVENT_TOKEN);
        if (eventToken == null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot track event, eventToken is null");
            return;
        }

        AdjustEvent adjustEvent = new AdjustEvent(eventToken);

        Double revenue = null;
        try {
            revenue = Double.parseDouble(contextData.get(ADJUST_KEY_EVENT_REVENUE));
        } catch (Exception ignored) {
        }

        String currency = contextData.get(ADJUST_KEY_EVENT_CURRENCY);

        if (revenue != null && currency != null) {
            adjustEvent.setRevenue(revenue, currency);
        }

        for (String key: contextData.keySet()) {
            if (key.startsWith(ADJUST_KEY_EVENT_CALLBACK_PARAM)) {
                String callbackParamKey = key.substring(ADJUST_KEY_EVENT_CALLBACK_PARAM.length());
                String callbackParamValue = contextData.get(key);
                adjustEvent.addCallbackParameter(callbackParamKey, callbackParamValue);
            } else if (key.startsWith(ADJUST_KEY_EVENT_PARTNER_PARAM)) {
                String callbackParamKey = key.substring(ADJUST_KEY_EVENT_PARTNER_PARAM.length());
                String callbackParamValue = contextData.get(key);
                adjustEvent.addPartnerParameter(callbackParamKey, callbackParamValue);
            }
        }

        Adjust.trackEvent(adjustEvent);
    }

    /**
     *  Indicates Adjust Sdk has been initialized or not
     */
    protected boolean isSdkInitialised() {
        return sdkInitialised;
    }

    /**
     * This returns Adjust sdk version
     * @return String Adjust sdk version
     */
    protected String getVersion() {
        return Adjust.getSdkVersion();
    }

    /**
     * This registers AdjustLifecycleCallbacks to activity lifecycle callbacks
     * It allows tracking of activity lifecycle states
     */
    protected boolean registerActivityLifecycleCallbacks(final Context context) {
        if (application != null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot register activity lifecycle callbacks more than once");
            return false;
        }

        if (context == null) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot register activity lifecycle callbacks without context");
            return false;
        }

        final Context applicationContext = context.getApplicationContext();

        if (!(applicationContext instanceof Application)) {
            Log.debug(LOG_EXTENSION, LOG_SOURCE,"Cannot register activity lifecycle callbacks "
                           + "without application context as Application");
            return false;
        }

        Log.debug(LOG_EXTENSION, LOG_SOURCE,"Registering activity lifecycle callbacks");


        application = (Application) applicationContext;
        application.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

        return true;
    }

    /**
     * This internal method maps configuration received from client and Adobe server
     * and converts it all into AdjustConfig for Adjust Sdk initialization.
     * It tracks action on Adjust Attribution and further delegate it to callback listener.
     */
    private AdjustConfig getAdjustConfig(
            final Context appContext,
            final String appToken,
            final boolean shouldTrackAttribution,
            final AdjustAdobeExtensionConfig adjustAdobeExtensionConfig)
    {
        String environment = AdjustAdobeExtension.getAdjustAdobeExtensionConfig().getEnvironment();
        AdjustConfig adjustConfig = new AdjustConfig(appContext, appToken, environment);

        switch (MobileCore.getLogLevel()) {
            case ERROR:
                adjustConfig.setLogLevel(LogLevel.ERROR);
                break;
            case WARNING:
                adjustConfig.setLogLevel(LogLevel.WARN);
                break;
            case DEBUG:
                adjustConfig.setLogLevel(LogLevel.DEBUG);
                break;
            case VERBOSE:
                adjustConfig.setLogLevel(LogLevel.VERBOSE);
                break;
        }

        adjustConfig.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public
            void onAttributionChanged(final AdjustAttribution attribution) {
                if (shouldTrackAttribution) {
                    Map<String, String> contextData = new HashMap<String, String>();
                    if (attribution.network != null) {
                        contextData.put("Adjust Network" , attribution.network);
                    }

                    if (attribution.campaign != null) {
                        contextData.put("Adjust Campaign" , attribution.campaign);
                    }

                    if (attribution.adgroup != null) {
                        contextData.put("Adjust AdGroup" , attribution.adgroup);
                    }

                    if (attribution.creative != null) {
                        contextData.put("Adjust Creative" , attribution.creative);
                    }

                    MobileCore.trackAction("Adjust Campaign Data Received", contextData);
                }

                OnAttributionChangedListener onAttributionChangedListener =
                        adjustAdobeExtensionConfig.getOnAttributionChangedListener();
                if (onAttributionChangedListener != null) {
                    onAttributionChangedListener.onAttributionChanged(attribution);
                }
            }
        });

        adjustConfig.setOnDeeplinkResponseListener(
                adjustAdobeExtensionConfig.getOnDeeplinkResponseListener());

        return adjustConfig;
    }

    private static final class AdjustLifecycleCallbacks
            implements Application.ActivityLifecycleCallbacks
    {
        @Override
        public void onActivityResumed(final Activity activity) {
            activityResumed = true;

            // there might be a moment when Sdk is not initialized while Activity resumed
            if (sdkInitialised) {
                Adjust.onResume();
            }
        }

        @Override
        public void onActivityPaused(final Activity activity) {
            activityResumed = false;

            // there might be a moment when Sdk is not initialized while Activity paused
            if (sdkInitialised) {
                Adjust.onPause();
            }
        }

        @Override
        public void onActivityStopped(final Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {}

        @Override
        public void onActivityDestroyed(final Activity activity) {}

        @Override
        public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(final Activity activity) {}
    }
}
