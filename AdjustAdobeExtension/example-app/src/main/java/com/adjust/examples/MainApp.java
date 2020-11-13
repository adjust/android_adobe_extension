package com.adjust.examples;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.adjust.adobeextension.AdjustAdobeExtension;
import com.adjust.adobeextension.AdjustAdobeExtensionConfig;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustEventFailure;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.AdjustSessionFailure;
import com.adjust.sdk.AdjustSessionSuccess;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeeplinkResponseListener;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.OnSessionTrackingFailedListener;
import com.adjust.sdk.OnSessionTrackingSucceededListener;
import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Identity;
import com.adobe.marketing.mobile.InvalidInitException;
import com.adobe.marketing.mobile.Lifecycle;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;
import com.adobe.marketing.mobile.Signal;

public class MainApp extends Application {

    @Override
    public
    void onCreate() {
        super.onCreate();

        // set application object
        // important for activity lifecycle detection
        MobileCore.setApplication(this);

        // set log level
        // internally translates to Adjust SDK logging
        MobileCore.setLogLevel(LoggingMode.VERBOSE);

        // register Adobe core extensions
        // below are just sample extensions, not limited to these
        try {
            Identity.registerExtension();
            Signal.registerExtension();
            Lifecycle.registerExtension();
        } catch (InvalidInitException e) {
            e.printStackTrace();
        }

        // register the Adjust extension
        AdjustAdobeExtensionConfig config =
                new AdjustAdobeExtensionConfig(AdjustAdobeExtensionConfig.ENVIRONMENT_SANDBOX);

        // Optional callbacks
        // Set attribution delegate.
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                Log.d("example", "Attribution callback called!");
                Log.d("example", "Attribution: " + attribution.toString());
            }
        });

        // Evaluate deferred deep link to be launched.
        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
            @Override
            public boolean launchReceivedDeeplink(Uri deeplink) {
                Log.d("example", "Deferred deep link callback called!");
                Log.d("example", "Deep link URL: " + deeplink);

                return true;
            }
        });

        // register the Adjust SDK extension
        AdjustAdobeExtension.registerExtension(config);

        // once all the extensions are registered, start processing the events
        MobileCore.start(new AdobeCallback () {
            @Override
            public void call(Object o) {
                MobileCore.configureWithAppID("89645c501ce0/540de252943f/launch-f8d889dd15b6-development");
            }
        });
    }
}
