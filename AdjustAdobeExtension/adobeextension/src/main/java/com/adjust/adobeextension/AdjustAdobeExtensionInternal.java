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
        adjustSdkApiHandler = new AdjustSdkApiHandler();
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
        return adjustSdkApiHandler.getVersion();
    }

    @Override
    protected void onUnexpectedError(ExtensionUnexpectedError extensionUnexpectedError) {
        super.onUnexpectedError(extensionUnexpectedError);

        MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                       "ExtensionUnexpectedError : " + extensionUnexpectedError.getMessage());
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

        if (!sharedEventState.containsKey(ADJUST_APP_TOKEN_KEY) ||
            !sharedEventState.containsKey(ADJUST_TRACK_ATTRIBUTION_KEY)) {
            return;
        }

        String appToken = (String)sharedEventState.get(ADJUST_APP_TOKEN_KEY);
        Boolean trackAttributionObject = (Boolean)sharedEventState.get(ADJUST_TRACK_ATTRIBUTION_KEY);

        boolean shouldTrackAttribution = trackAttributionObject != null && trackAttributionObject;

        adjustSdkApiHandler.initSdk(appToken, shouldTrackAttribution);

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
            Event event = eventQueue.peek();

            if (event != null) {
                Map<String, Object> eventData = event.getEventData();
                Object contextDataObject = eventData.get(EVENT_CONTEXT_DATA_KEY);
                if ((contextDataObject instanceof Map)) {
                    Map<String, String> contextData = (Map<String, String>)contextDataObject;

                    adjustSdkApiHandler.trackEvent(contextData);
                }
            }

            eventQueue.poll();
        }
    }

            }
        }
    }
}
