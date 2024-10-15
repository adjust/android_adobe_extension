package com.adjust.examples;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.adjust.adobeextension.AdjustAdobeExtension;
import com.adjust.adobeextension.AdjustAdobeExtensionConfig;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeferredDeeplinkResponseListener;
import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Extension;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;

import java.util.Arrays;
import java.util.List;

public class MainApp extends Application {

    @Override
    public
    void onCreate() {
        super.onCreate();

        // set application object
        MobileCore.setApplication(this);

        // set log level
        // internally translates to Adjust SDK logging
        MobileCore.setLogLevel(LoggingMode.VERBOSE);

        // configure Adjust Adobe Extension
        configureAdjustAdobeExtension();

        // register extensions
        try {
            List<Class<? extends Extension>> extensions = Arrays.asList(AdjustAdobeExtension.EXTENSION);
            MobileCore.registerExtensions(extensions, new AdobeCallback<Object>() {
                @Override
                public void call(Object o) {
                    Log.d("example", "Adjust Adobe Extension SDK initialized");
                }
            });
        } catch (Exception e) {
            Log.e("example", "Exception while registering Extension: " + e.getMessage());
        }

    }

    private void configureAdjustAdobeExtension() {
        try {
            MobileCore.configureWithAppID("89645c501ce0/540de252943f/launch-f8d889dd15b6-development");

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
            config.setOnDeferredDeeplinkResponseListener(new OnDeferredDeeplinkResponseListener() {
                @Override
                public boolean launchReceivedDeeplink(Uri uri) {
                    Log.d("example", "Deferred deep link callback called!");
                    Log.d("example", "Deep link URL: " + uri);

                    return true;
                }
            });

            // register the Adjust SDK extension
            AdjustAdobeExtension.setConfiguration(config);
        } catch (Exception e) {

        }

        // Add global callback parameters.
        AdjustAdobeExtension.addGlobalCallbackParameter("gc_foo", "gc_bar");
        AdjustAdobeExtension.addGlobalCallbackParameter("gc_key", "gc_value");

        // Add global partner parameters.
        AdjustAdobeExtension.addGlobalPartnerParameter("gp_foo", "gp_bar");
        AdjustAdobeExtension.addGlobalPartnerParameter("gp_key", "gp_value");
    }
}
