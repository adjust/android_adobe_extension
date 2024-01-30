package com.adjust.adobeextension;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_MODULE_CONFIGURATION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_SHARED_STATE_OWNER;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.LOG_EXTENSION;

import com.adobe.marketing.mobile.services.Log;

import com.adobe.marketing.mobile.Event;
import com.adobe.marketing.mobile.EventType;
import com.adobe.marketing.mobile.ExtensionEventListener;

import java.util.Map;

/**
 * Internal class to hear all events meant for Adobe Extension.
 * It filters configuration & generic track event and delegate those to Adjust extension.
 */
class AdjustAdobeExtensionListener
        implements ExtensionEventListener {

    private static final String LOG_SOURCE = AdjustAdobeExtensionListener.class.getSimpleName();

    AdjustAdobeExtensionInternal adjustAdobeExtension;

    public AdjustAdobeExtensionListener(AdjustAdobeExtensionInternal extension) {
        adjustAdobeExtension = extension;
    }

    @Override
    public void hear(final Event event) {
        if (event == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE,"Failed to hear event in listener, event is null");
            return;
        }

        String eventType = event.getType();
        if (eventType == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE,"Failed to hear event in listener, eventType is null");
            return;
        }

        Map<String, Object> eventData = event.getEventData();
        if (eventData == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE, "Failed to hear event in listener, eventData is null");
            return;
        }

        if (adjustAdobeExtension == null) {
            Log.error(LOG_EXTENSION, LOG_SOURCE,"Failed to hear event in listener, adjustAdobeExtension is null");
            return;
        }

        if (EventType.HUB.equalsIgnoreCase(eventType)) {
            Object adobeSharedStateOwner = eventData.get(ADOBE_SHARED_STATE_OWNER);

            if (adobeSharedStateOwner instanceof String
                    && ADOBE_MODULE_CONFIGURATION.equalsIgnoreCase((String)adobeSharedStateOwner))
            {
                adjustAdobeExtension.handleConfigurationEvent(event);
            }

        } else if (EventType.GENERIC_TRACK.equalsIgnoreCase(eventType)) {
            adjustAdobeExtension.handleGenericTrackEvent(event);
        }
    }
}
