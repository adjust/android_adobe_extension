package com.adjust.adobeextension;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.Constants;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;

import java.util.HashMap;
import java.util.Map;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_EVENT_CURRENCY_KEY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_EVENT_REVENUE_KEY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_EVENT_TOKEN_KEY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_TAG;

/**
 * Internal class to interface with Adjust Sdk.
 * It filters configuration & generic track event and delegate those to Adjust extension.
 */
class AdjustSdkApiHandler {

    /**
     * Flag to indicate if Adjust Sdk has been initialized yet
     */
    private static boolean sdkInitialised;

    /**
     * Toggle flag to indicate whether Android Activity has been resumed or paused
     */
    private static boolean activityResumed;

    public AdjustSdkApiHandler() {
        sdkInitialised = false;
        activityResumed = false;

        // register to activity life cycle callbacks, to keep track of Activity state
        registerActivityLifecycleCallbacks();
    }

    /**
     * Method to init Adjust Sdk for the given app token
     * @param appToken
     * @param shouldTrackAttribution
     */
    protected void initSdk(final String appToken, final boolean shouldTrackAttribution) {
        if (sdkInitialised) {
            MobileCore.log(LoggingMode.WARNING, LOG_TAG,
                    "Cannot initialise SDK, already initialised");
            return;
        }

        Application application = MobileCore.getApplication();
        if (application == null) {
            MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                           "Cannot initialise SDK, application object is null");
            return;
        }

        if (appToken == null) {
            MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                           "Cannot initialise SDK, appToken is null or empty");
        }

        AdjustAdobeExtensionConfig adjustAdobeExtensionConfig =
                AdjustAdobeExtension.getAdjustAdobeExtensionConfig();
        if (adjustAdobeExtensionConfig == null) {
            MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                           "Cannot initialise SDK, adjust extension config is null");
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
     * Method to track Adjust events
     * @param contextData map with event params as key-value
     */
    protected void trackEvent(final Map<String, String> contextData) {
        if (contextData == null) {
            return;
        }

        String eventToken = contextData.get(ADJUST_EVENT_TOKEN_KEY);
        if (eventToken == null) {
            return;
        }

        AdjustEvent adjustEvent = new AdjustEvent(eventToken);

        Double revenue = null;
        try {
            revenue = Double.parseDouble(contextData.get(ADJUST_EVENT_REVENUE_KEY));
        } catch (Exception ignored) {
        }

        String currency = contextData.get(ADJUST_EVENT_CURRENCY_KEY);

        if (revenue != null && currency != null) {
            adjustEvent.setRevenue(revenue, currency);
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

    // internal methods
    private void registerActivityLifecycleCallbacks() {
        Application application = MobileCore.getApplication();
        if (application != null) {
            application.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        }
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
            final AdjustAdobeExtensionConfig adjustAdobeExtensionConfig) {

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
            void onAttributionChanged(AdjustAttribution attribution) {
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

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            activityResumed = true;

            // there might be a moment when Sdk is not initialized while Activity resumed
            if (sdkInitialised) {
                Adjust.onResume();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            activityResumed = false;

            // there might be a moment when Sdk is not initialized while Activity paused
            if (sdkInitialised) {
                Adjust.onPause();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(Activity activity) {}
    }

}
