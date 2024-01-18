package com.adjust.adobeextension;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_APP_TOKEN;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADJUST_KEY_TRACK_ATTRIBUTION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_MODULE_CONFIGURATION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_KEY_ACTION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_KEY_CONTEXT_DATA;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EXTENSION_NAME;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EXTENSION_VERSION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.adobe.marketing.mobile.Event;
import com.adobe.marketing.mobile.EventSource;
import com.adobe.marketing.mobile.EventType;
import com.adobe.marketing.mobile.Extension;
import com.adobe.marketing.mobile.ExtensionApi;
import com.adobe.marketing.mobile.SharedStateResolution;
import com.adobe.marketing.mobile.SharedStateResult;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @NonNull
    @Override
    protected String getName() {
        return EXTENSION_NAME;
    }

    @Override
    protected String getVersion() {
        return String.format("%s@%s", EXTENSION_VERSION, adjustSdkApiHandler.getVersion());
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
        extensionApi.registerEventListener(EventType.HUB,
                EventSource.SHARED_STATE, new AdjustAdobeExtensionListener(this));
    }

    private void registerListenerForGenericTrackEvent(final ExtensionApi extensionApi) {
        extensionApi.registerEventListener(EventType.GENERIC_TRACK,
                EventSource.REQUEST_CONTENT, new AdjustAdobeExtensionListener(this));
    }

    /**
     * Handles configuration event.  It checks for params to initialise Adjust Sdk (if not already)
     * Also calls to process generic track if there are any queued up
     */
    private void handleConfigurationEventAsync(final Event event) {

        SharedStateResult sharedStateResult = getApi().getSharedState(
                ADOBE_MODULE_CONFIGURATION,
                event,
                false,
                SharedStateResolution.ANY);

        if (sharedStateResult == null) {
            Log.e(LOG_TAG, "Failed to handle configuration event, sharedStateResult is null");
            return;
        }

        Map<String, Object> sharedStateResultMap = sharedStateResult.getValue();

        if (sharedStateResultMap == null) {
            Log.e(LOG_TAG, "Failed to handle configuration event, sharedEventState is null");
            return;
        }

        Object appTokenObject = sharedStateResultMap.get(ADJUST_KEY_APP_TOKEN);
        Object shouldTrackAttributionObject = sharedStateResultMap.get(ADJUST_KEY_TRACK_ATTRIBUTION);

        if (!(appTokenObject instanceof String)
                || !(shouldTrackAttributionObject instanceof Boolean))
        {
            Log.e(LOG_TAG, "Failed to handle configuration event, "
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
