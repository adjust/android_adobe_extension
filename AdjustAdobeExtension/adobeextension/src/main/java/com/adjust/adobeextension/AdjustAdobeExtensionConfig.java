package com.adjust.adobeextension;

import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeferredDeeplinkResponseListener;

/**
 * Class for Adjust Adobe Extension Config.
 * Use this config class while registering Adjust Adobe Extension into your app.
 * See the README for details.
 */
public class AdjustAdobeExtensionConfig {
    /**
     * Environment when sdk to send data for testing.
     */
    public static final String ENVIRONMENT_SANDBOX = AdjustConfig.ENVIRONMENT_SANDBOX;

    /**
     * Environment when sdk to send data while in production.
     */
    public static final String ENVIRONMENT_PRODUCTION = AdjustConfig.ENVIRONMENT_PRODUCTION;

    /**
     * Environment where sdk should send data to.
     */
    private final String environment;

    /**
     * An external identifier for a device or user.
     */
    private String externalDeviceId;

    /**
     * Callback to listen for attribution change.
     */
    private OnAttributionChangedListener onAttributionChangedListener;

    /**
     * Callback to listen for deeplink response.
     */
    private OnDeferredDeeplinkResponseListener onDeferredDeeplinkResponseListener;

    /**
     * Primary constructor.
     */
    public AdjustAdobeExtensionConfig(final String environment) {
        this.environment = environment;
    }

    /**
     * Method to set an external identifier for a device or user.
     */
    public void setExternalDeviceId(String externalDeviceId) {
        this.externalDeviceId = externalDeviceId;
    }

    /**
     * Method to set callback for attribution change.
     */
    public void setOnAttributionChangedListener(
            final OnAttributionChangedListener onAttributionChangedListener)
    {
        this.onAttributionChangedListener = onAttributionChangedListener;
    }

    /**
     * Method to set callback for deeplink response.
     */
    public void setOnDeferredDeeplinkResponseListener(
            final OnDeferredDeeplinkResponseListener onDeferredDeeplinkResponseListener)
    {
        this.onDeferredDeeplinkResponseListener = onDeferredDeeplinkResponseListener;
    }

    /**
     * Method to get environment where should should send data to.
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Method to get the external identifier set for a device or user.
     */
    public String getExternalDeviceId() {
        return externalDeviceId;
    }

    /**
     * Method to get attribution change callback listener
     */
    public OnAttributionChangedListener getOnAttributionChangedListener() {
        return onAttributionChangedListener;
    }

    /**
     * Method to get deeplink response callback listener
     */
    public OnDeferredDeeplinkResponseListener getOnDeferredDeeplinkResponseListener() {
        return onDeferredDeeplinkResponseListener;
    }
}
