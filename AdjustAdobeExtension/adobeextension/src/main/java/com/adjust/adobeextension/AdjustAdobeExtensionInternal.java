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

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_APP_TOKEN;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_TRACK_ATTRIBUTION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_MODULE_CONFIGURATION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_KEY_CONTEXT_DATA;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_SOURCE_ADOBE_REQUEST_CONTENT;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_SOURCE_ADOBE_SHARED_STATE;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_KEY_ACTION;
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
class AdjustAdobeExtensionInternal
        extends Extension
{
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
    protected void onUnexpectedError(final ExtensionUnexpectedError extensionUnexpectedError) {
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
    private void registerListenerForConfigurationEvent(final ExtensionApi extensionApi) {
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

    private void registerListenerForGenericTrackEvent(final ExtensionApi extensionApi) {
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
    private void handleConfigurationEventAsync(final Event event) {
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
            MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                           "Failed to handle configuration event, "
                           + "sharedEventState is null");
            return;
        }

        Object appTokenObject = sharedEventState.get(ADJUST_KEY_APP_TOKEN);
        Object shouldTrackAttributionObject = sharedEventState.get(ADJUST_KEY_TRACK_ATTRIBUTION);

        if (!(appTokenObject instanceof String)
                || !(shouldTrackAttributionObject instanceof Boolean))
        {
            MobileCore.log(LoggingMode.ERROR, LOG_TAG,
                           "Failed to handle configuration event, "
                           + "appToken or shouldTrackAttribution are not instance of correct type");
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
    private void handleGenericTrackEventAsync(final Event event) {
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

                Object actionObject = eventData.get(EVENT_KEY_ACTION);
                String action = null;
                if (actionObject instanceof String) {
                    action = (String) actionObject;
                }

                Object contextDataObject = eventData.get(EVENT_KEY_CONTEXT_DATA);
                Map<String, String> contextData = null;

                if (contextDataObject instanceof Map) {
                    contextData = (Map<String, String>)contextDataObject;
                }

                if (action == null && contextData == null) {
                    continue;
                }

                adjustSdkApiHandler.processEvent(action, contextData);
            }
        }
    }
}
