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
     * Callback to listen when event tracking succeeded.
     */
    private OnEventTrackingSucceededListener onEventTrackingSucceededListener;

    /**
     * Callback to listen when event tracking failed.
     */
    private OnEventTrackingFailedListener onEventTrackingFailedListener;

    /**
     * Callback to listen when session tracking succeeded.
     */
    private OnSessionTrackingSucceededListener onSessionTrackingSucceededListener;

    /**
     * Callback to listen when session tracking failed.
     */
    private OnSessionTrackingFailedListener onSessionTrackingFailedListener;

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
    public void setOnAttributionChangedListener(OnAttributionChangedListener onAttributionChangedListener) {
        this.onAttributionChangedListener = onAttributionChangedListener;
    }

    /**
     * Method to set callback for event tracking success.
     */
    public void setOnEventTrackingSucceededListener(OnEventTrackingSucceededListener onEventTrackingSucceededListener) {
        this.onEventTrackingSucceededListener = onEventTrackingSucceededListener;
    }

    /**
     * Method to set callback for event tracking failure.
     */
    public void setOnEventTrackingFailedListener(OnEventTrackingFailedListener onEventTrackingFailedListener) {
        this.onEventTrackingFailedListener = onEventTrackingFailedListener;
    }

    /**
     * Method to set callback for session tracking success.
     */
    public void setOnSessionTrackingSucceededListener(OnSessionTrackingSucceededListener onSessionTrackingSucceededListener) {
        this.onSessionTrackingSucceededListener = onSessionTrackingSucceededListener;
    }

    /**
     * Method to set callback for session tracking failure.
     */
    public void setOnSessionTrackingFailedListener(OnSessionTrackingFailedListener onSessionTrackingFailedListener) {
        this.onSessionTrackingFailedListener = onSessionTrackingFailedListener;
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
     * Method to get event tracking success callback listener
     */
    public OnEventTrackingSucceededListener getOnEventTrackingSucceededListener() {
        return onEventTrackingSucceededListener;
    }

    /**
     * Method to get event tracking failure callback listener
     */
    public OnEventTrackingFailedListener getOnEventTrackingFailedListener() {
        return onEventTrackingFailedListener;
    }

    /**
     * Method to get session tracking success callback listener
     */
    public OnSessionTrackingSucceededListener getOnSessionTrackingSucceededListener() {
        return onSessionTrackingSucceededListener;
    }

    /**
     * Method to get session tracking failure callback listener
     */
    public OnSessionTrackingFailedListener getOnSessionTrackingFailedListener() {
        return onSessionTrackingFailedListener;
    }

    /**
     * Method to get deeplink response callback listener
     */
    public OnDeeplinkResponseListener getOnDeeplinkResponseListener() {
        return onDeeplinkResponseListener;
    }
}
