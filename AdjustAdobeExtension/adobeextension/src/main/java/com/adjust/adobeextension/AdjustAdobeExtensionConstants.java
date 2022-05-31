package com.adjust.adobeextension;

class AdjustAdobeExtensionConstants {
    static final String LOG_TAG = "AdjustAdobeExtension";
    static final String EXTENSION_NAME = "com.adjust.adobeextension";
    static final String EXTENSION_VERSION = "adobe_ext1.0.2";

    static final String ADOBE_MODULE_CONFIGURATION = "com.adobe.module.configuration";
    static final String ADOBE_SHARED_STATE_OWNER = "stateowner";

    static final String EVENT_TYPE_ADOBE_HUB = "com.adobe.eventType.hub";
    static final String EVENT_TYPE_ADOBE_GENERIC_TRACK = "com.adobe.eventType.generic.track";
    static final String EVENT_SOURCE_ADOBE_SHARED_STATE = "com.adobe.eventSource.sharedState";
    static final String EVENT_SOURCE_ADOBE_REQUEST_CONTENT = "com.adobe.eventSource.requestContent";
    static final String EVENT_KEY_ACTION = "action";
    static final String EVENT_KEY_CONTEXT_DATA = "contextdata";

    static final String ADJUST_KEY_APP_TOKEN = "adjustAppToken";
    static final String ADJUST_KEY_TRACK_ATTRIBUTION = "adjustTrackAttribution";

    // event tracking
    static final String ADJUST_KEY_EVENT_TOKEN = "adj.eventToken";
    static final String ADJUST_KEY_EVENT_CURRENCY = "adj.currency";
    static final String ADJUST_KEY_EVENT_REVENUE = "adj.revenue";
    static final String ADJUST_KEY_EVENT_CALLBACK_PARAM = "adj.event.callback.";
    static final String ADJUST_KEY_EVENT_PARTNER_PARAM = "adj.event.partner.";

    // push token tracking
    static final String ADJUST_ACTION_SET_PUSH_TOKEN = "adj.setPushToken";
    static final String ADJUST_KEY_PUSH_TOKEN_PARAM = "adj.pushToken";
}
