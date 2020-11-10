package com.adjust.adobeextension;

import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeeplinkResponseListener;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.OnSessionTrackingFailedListener;
import com.adjust.sdk.OnSessionTrackingSucceededListener;

/**
 * Class for Adjust Adobe Extension Config.
 * Use this config class while registering Adjust Adobe Extension into your app.
 * See the README for details.
 */
public class AdjustAdobeExtensionConfig {

    /**
     * Environment when sdk to send data for testing.
     */
    public static final String ENVIRONMENT_SANDBOX = "sandbox";

    /**
     * Environment when sdk to send data while in production.
     */
    public static final String ENVIRONMENT_PRODUCTION = "production";

    /**
     * Environment where sdk should send data to.
     */
    private final String environment;

    /**
     * Callback to listen for attribution change.
     */
    private OnAttributionChangedListener onAttributionChangedListener;

    /**
     * Callback to listen for deeplink response.
     */
    private OnDeeplinkResponseListener onDeeplinkResponseListener;

    /**
     * Primary constructor.
     */
    public AdjustAdobeExtensionConfig(String environment) {
        this.environment = environment;
    }

    /**
     * Method to set callback for attribution change.
     */
    public void setOnAttributionChangedListener(
            OnAttributionChangedListener onAttributionChangedListener)
    {
        this.onAttributionChangedListener = onAttributionChangedListener;
    }

    /**
     * Method to set callback for deeplink response.
     */
    public void setOnDeeplinkResponseListener(OnDeeplinkResponseListener onDeeplinkResponseListener) {
        this.onDeeplinkResponseListener = onDeeplinkResponseListener;
    }

    /**
     * Method to get environment where should should send data to.
     */
    public String getEnvironment() {
        return environment;
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
    public OnDeeplinkResponseListener getOnDeeplinkResponseListener() {
        return onDeeplinkResponseListener;
    }
}
