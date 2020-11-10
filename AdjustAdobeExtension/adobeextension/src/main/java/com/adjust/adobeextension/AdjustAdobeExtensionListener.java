package com.adjust.adobeextension;

import com.adobe.marketing.mobile.Event;
import com.adobe.marketing.mobile.ExtensionApi;
import com.adobe.marketing.mobile.ExtensionListener;

import java.util.Map;

import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_MODULE_CONFIGURATION;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.ADOBE_SHARED_STATE_OWNER;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_TYPE_ADOBE_GENERIC_TRACK;
import static com.adjust.adobeextension.AdjustAdobeExtensionConstants.EVENT_TYPE_ADOBE_HUB;

/**
 * Internal class to hear all events meant for Adobe Extension.
 * It filters configuration & generic track event and delegate those to Adjust extension.
 */
class AdjustAdobeExtensionListener
        extends ExtensionListener {

    protected AdjustAdobeExtensionListener(ExtensionApi extension, String type, String source) {
        super(extension, type, source);
    }

    @Override
    public void hear(Event event) {
        if (event == null) {
            return;
        }

        String eventType = event.getType();
        if (eventType == null) {
            return;
        }

        Map<String, Object> eventData = event.getEventData();
        if (eventData == null) {
            return;
        }

        final AdjustAdobeExtensionInternal adjustAdobeExtension = getParentExtension();
        if (adjustAdobeExtension == null) {
            return;
        }

        if (EVENT_TYPE_ADOBE_HUB.equalsIgnoreCase(eventType)) {
            Object adobeSharedStateOwner = eventData.get(ADOBE_SHARED_STATE_OWNER);

            if (adobeSharedStateOwner instanceof String
                    && ADOBE_MODULE_CONFIGURATION.equalsIgnoreCase((String)adobeSharedStateOwner))
            {
                adjustAdobeExtension.handleConfigurationEvent(event);
            }

        } else if (EVENT_TYPE_ADOBE_GENERIC_TRACK.equalsIgnoreCase(eventType)) {

            adjustAdobeExtension.handleGenericTrackEvent(event);
        }
    }

    @Override
    protected AdjustAdobeExtensionInternal getParentExtension() {
        return (AdjustAdobeExtensionInternal) super.getParentExtension();
    }
}
