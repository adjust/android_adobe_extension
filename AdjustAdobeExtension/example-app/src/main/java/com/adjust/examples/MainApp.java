package com.adjust.examples;

import android.app.Application;

import com.adjust.adobeextension.AdjustAdobeExtension;
import com.adjust.adobeextension.AdjustAdobeExtensionConfig;
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
