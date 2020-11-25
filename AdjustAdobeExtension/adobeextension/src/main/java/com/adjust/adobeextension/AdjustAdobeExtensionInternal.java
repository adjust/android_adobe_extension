package com.adjust.adobeextension;

import com.adobe.marketing.mobile.Event;
import com.adobe.marketing.mobile.Extension;
import com.adobe.marketing.mobile.ExtensionApi;
import com.adobe.marketing.mobile.ExtensionError;
import com.adobe.marketing.mobile.ExtensionErrorCallback;
import com.adobe.marketing.mobile.ExtensionUnexpectedError;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_APP_TOKEN_KEY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_TRACK_ATTRIBUTION_KEY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_MODULE_CONFIGURATION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_CONTEXT_DATA_KEY;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_SOURCE_ADOBE_REQUEST_CONTENT;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_SOURCE_ADOBE_SHARED_STATE;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_TYPE_ADOBE_GENERIC_TRACK;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_TYPE_ADOBE_HUB;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EXTENSION_NAME;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EXTENSION_VERSION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_TAG;

/**
 * Internal class for Adjust Adobe Extension implementation.
 * It registers and handles configuration & generic track event.
 * It delegates calls to Adjust Sdk using Api handler
 */
class AdjustAdobeExtensionInternal extends Extension {
    private final ConcurrentLinkedQueue<Event> eventQueue;
    private final ExecutorService executorService;
    private final AdjustSdkApiHandler adjustSdkApiHandler;

    protected AdjustAdobeExtensionInternal(final ExtensionApi extensionApi) {
        super(extensionApi);

        eventQueue = new ConcurrentLinkedQueue<>();
        adjustSdkApiHandler = AdjustSdkApiHandler.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        registerListenerForConfigurationEvent(extensionApi);
        registerListenerForGenericTrackEvent(extensionApi);
    }

    @Override
    protected String getName() {
        return EXTENSION_NAME;
    }

    @Override
    protected String getVersion() {
        return String.format("%s@%s", EXTENSION_VERSION, adjustSdkApiHandler.getVersion());
    }

    @Override
    protected void onUnexpectedError(ExtensionUnexpectedError extensionUnexpectedError) {
        super.onUnexpectedError(extensionUnexpectedError);

        MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                       "ExtensionUnexpectedError"
                               + extensionUnexpectedError != null ?
                                    ": " + extensionUnexpectedError.getMessage()
                                    : " with null error");
    }

    protected void handleConfigurationEvent(final Event event) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handleConfigurationEventAsync(event);
            }
        });
    }

    protected void handleGenericTrackEvent(final Event event) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handleGenericTrackEventAsync(event);
            }
        });
    }

    // internal methods
    private void registerListenerForConfigurationEvent(ExtensionApi extensionApi) {
        ExtensionErrorCallback<ExtensionError>
                errorCallback = new ExtensionErrorCallback<ExtensionError>() {
            @Override
            public void error(final ExtensionError extensionError) {
                MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                               "Failed to register listener for config update event, error : " +
                               extensionError.getErrorName());
            }
        };

        extensionApi.registerEventListener(
                EVENT_TYPE_ADOBE_HUB,
                EVENT_SOURCE_ADOBE_SHARED_STATE,
                AdjustAdobeExtensionListener.class, errorCallback);
    }

    private void registerListenerForGenericTrackEvent(ExtensionApi extensionApi) {
        ExtensionErrorCallback<ExtensionError>
                errorCallback = new ExtensionErrorCallback<ExtensionError>() {
            @Override
            public void error(final ExtensionError extensionError) {
                MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                               "Failed to register listener for generic track event, error : " +
                               extensionError.getErrorName());
            }
        };

        extensionApi.registerEventListener(
                EVENT_TYPE_ADOBE_GENERIC_TRACK,
                EVENT_SOURCE_ADOBE_REQUEST_CONTENT,
                AdjustAdobeExtensionListener.class, errorCallback);
    }

    /**
     * Handles configuration event.  It checks for params to initialise Adjust Sdk (if not already)
     * Also calls to process generic track if there are any queued up
     */
    private void handleConfigurationEventAsync(Event event) {
        ExtensionErrorCallback<ExtensionError>
                errorCallback = new ExtensionErrorCallback<ExtensionError>() {
            @Override
            public void error(final ExtensionError extensionError) {
                MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                               "Failed to retrieve the shared state, error : " +
                               extensionError.getErrorName());
            }
        };

        Map<String, Object> sharedEventState = getApi().getSharedEventState(
                ADOBE_MODULE_CONFIGURATION,
                event,
                errorCallback);

        if (sharedEventState == null) {
            return;
        }

        Object appTokenObject = sharedEventState.get(ADJUST_APP_TOKEN_KEY);
        Object shouldTrackAttributionObject = sharedEventState.get(ADJUST_TRACK_ATTRIBUTION_KEY);

        if (!(appTokenObject instanceof String)
                || !(shouldTrackAttributionObject instanceof Boolean))
        {
            return;
        }

        String appToken = (String)appTokenObject;
        Boolean shouldTrackAttribution = (Boolean)shouldTrackAttributionObject;

        adjustSdkApiHandler.initSdk(appToken, shouldTrackAttribution.booleanValue());

        // process events arrived before initialisation
        processQueuedEvents();
    }

    /**
     * Handles generic track event.
     * It adds the event into event queue and calls to process them.
     */
    private void handleGenericTrackEventAsync(Event event) {
        eventQueue.add(event);

        processQueuedEvents();
    }

    /**
     * Process queued up generic track event.
     * It iterates through the queue and pass eligible event to Adjust Sdk for tracking
     */
    private void processQueuedEvents() {
        if (!adjustSdkApiHandler.isSdkInitialised()) {
            return;
        }

        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();

            if (event != null) {
                Map<String, Object> eventData = event.getEventData();
                if (eventData == null) {
                    continue;
                }
                Object contextDataObject = eventData.get(EVENT_CONTEXT_DATA_KEY);
                if (!(contextDataObject instanceof Map)) {
                    continue;
                }
                Map<String, String> contextData = (Map<String, String>)contextDataObject;

                adjustSdkApiHandler.trackEvent(contextData);
            }
        }
    }
}
