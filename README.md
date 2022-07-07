# Adjust Extension for Adobe Experience SDK

This is the Android Adobe Mobile Extension of Adjust™. You can read more about Adjust™ at [adjust.com].

## Table of contents

### Quick start

   * [Example app](#qs-example-app)
   * [Getting started](#qs-getting-started)
      * [Add the Extension to your project](#qs-add-extension)
      * [Add Google Play Services](#qs-gps)
      * [Add permissions](#qs-permissions)
      * [Proguard settings](#qs-proguard)
      * [Install referrer](#qs-install-referrer)
         * [Google Play Referrer API](#qs-gpr-api)
   * [Integrate the Adjust SDK Extension into your app](#qs-integrate-extension)
      * [Basic setup](#qs-basic-setup)
      * [Session tracking](#qs-session-tracking)
      * [Attribution](#qs-attribution)
      * [Adjust logging](#qs-adjust-logging)
      * [Build your app](#qs-build-the-app)

### Event tracking

   * [Track event](#et-tracking)
   * [Track revenue](#et-revenue)

### Custom parameters

   * [Custom parameters overview](#cp)
   * [Event parameters](#cp-event-parameters)
      * [Event callback parameters](#cp-event-callback-parameters)
      * [Event partner parameters](#cp-event-partner-parameters)

### Additional features

   * [Attribution callback](#af-attribution-callback)
   * [Deferred deep linking callback](#af-deferred-deep-linking-callback)
   * [Push token (uninstall tracking)](#af-push-token)


## Quick start

### <a id="qs-example-app"></a>Example app

There is an Android example app inside the [example-app][example-app] directory. You can open the Android project to see the example on how the Adjust Adobe Extension can be integrated.

### <a id="qs-getting-started"></a>Getting started

These are the minimum required steps to integrate the Adjust Extension in your Android app. We assume that you are using Android Studio for your Android development. The minimum supported Android API level for the Adjust Extension for Adobe Experience SDK integration is **14 (Ice Cream Sandwich)**.

### <a id="qs-add-extension"></a>Add the Extension to your project

If you are using [`Maven`][maven], add the following to your `build.gradle` file:

```gradle
implementation 'com.adjust.adobeextension:adobeextension:1.1.0'
implementation 'com.adjust.sdk:adjust-android:4.29.1'
implementation 'com.android.installreferrer:installreferrer:2.2'
```

### <a id="qs-gps"></a>Add Google Play Services

Apps in the Google Play Store must use the [Google Advertising ID][google-ad-id] to uniquely identify devices. To enable the Google Advertising ID for our SDK, you must integrate [Google Play Services][google-play-services]. If you haven't done this yet, please add dependency to the Google Play Services library by adding the following dependecy to your `dependencies` block of app's `build.gradle` file:

```gradle
implementation 'com.google.android.gms:play-services-ads-identifier:18.0.1'
```

**Note**: The Adjust Extension is not tied to any specific version of the `play-services-ads-identifier` part of the Google Play Services library. You can use the latest version of the library, or any other version you need.

### <a id="qs-permissions"></a>Add permissions

The Adjust Extension requires the following permissions. Please add them to your `AndroidManifest.xml` file if they are not already present:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

#### <a id="gps-adid-permission"></a>Add permission to gather Google advertising ID

If you are targeting Android 12 and above (API level 31), you need to add the `com.google.android.gms.AD_ID` permission to read the device's advertising ID. Add the following line to your `AndroidManifest.xml` to enable the permission.

```xml
<uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
```

For more information, see [Google's `AdvertisingIdClient.Info` documentation](https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/AdvertisingIdClient.Info#public-string-getid).

### <a id="qs-proguard"></a>Proguard settings

If you are using Proguard, add these lines to your Proguard file:

```
-keep class com.adjust.sdk.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.** { *; }
```

If you are **not publishing your app in the Google Play Store**, use the following package rules:

```
-keep class com.adjust.sdk.** { *; }
```

### <a id="qs-install-referrer"></a>Install referrer

In order to correctly attribute an app install to its source, Adjust needs information about the **install referrer**. We can achieve this by using the **Google Play Referrer API** for apps on Google Play Store and by using the **Huawei Referrer API** for apps on Huawei Mobile Store.

#### <a id="qs-gpr-api"></a>Google Play Referrer API

In order to support the Google Play Referrer API in your app, please make sure that you have followed our chapter on [adding the Extension to your project](#qs-add-extension) correctly and that you have following line added to your `build.gradle` file:

```
implementation 'com.android.installreferrer:installreferrer:2.2'
```

Please follow the directions for your [Proguard settings](#qs-proguard) carefully. Confirm that you have added all the rules mentioned in it, especially the one needed for this feature:

```
-keep public class com.android.installreferrer.** { *; }
```

### <a id="qs-integrate-extension"></a>Integrate the Adjust Extension into your app

First, we'll set up basic session tracking.

### <a id="qs-basic-setup"></a>Basic setup

We recommend using a global Android [Application][android-application] class to register the Extension. If you don't have one in your app, follow these steps:

- Create a class that extends the `Application`.
- Open the `AndroidManifest.xml` file of your app and locate the `<application>` element.
- Add the attribute `android:name` and set it to the name of your new application class.

In our example app, we use an `Application` class named `MainApp`. Therefore, we configure the manifest file as:
```xml
 <application
   android:name=".MainApp"
   <!-- ... -->
 </application>
```

- In your `Application` class, find or create the `onCreate` method. Add the following code to register the Adjust Extension:

```java
import com.adjust.adobeextension.AdjustAdobeExtension;
import com.adjust.adobeextension.AdjustAdobeExtensionConfig;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;


public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // basic Adobe extension setup
        MobileCore.setApplication(this);
        MobileCore.setLogLevel(LoggingMode.VERBOSE);

        // register Adjust Adobe extension
        String environment = AdjustAdobeExtensionConfig.ENVIRONMENT_SANDBOX;
        AdjustAdobeExtensionConfig config = new AdjustAdobeExtensionConfig(environment);
        AdjustAdobeExtension.registerExtension(config);

        // start Adobe core
        MobileCore.start(new AdobeCallback () {
        @Override
        public void call(Object o) {
            MobileCore.configureWithAppID("your_adobe_app_id");
        }
    });
    }
}
```

Replace `{your_adobe_app_id}` with your app id from Adobe Launch.

Next, you must set the `environment` to either sandbox or production mode:

```java
String environment = AdjustAdobeExtensionConfig.ENVIRONMENT_SANDBOX;
String environment = AdjustAdobeExtensionConfig.ENVIRONMENT_PRODUCTION;
```

**Important:** Set the value to `AdjustAdobeExtensionConfig.ENVIRONMENT_SANDBOX` if (and only if) you or someone else is testing your app. Make sure to set the environment to `AdjustAdobeExtensionConfig.ENVIRONMENT_PRODUCTION` before you publish the app. Set it back to `AdjustAdobeExtensionConfig.ENVIRONMENT_SANDBOX` if you start developing and testing it again.

We use this environment to distinguish between real traffic and test traffic from test devices. Keeping the environment updated according to your current status is very important!


### <a id="qs-session-tracking"></a>Session tracking

Adjust SDK can track sessions in your app based on Activity lifecycle.

### <a id="qs-attribution"></a>Attribution

The option to share attribution data with Adobe is in the Launch dashboard under the extensions configuration and is on by default. Adjust tracks the action name `Adjust Campaign Data Received` with the following attribution information from Adjust:

* `Adjust Network`
* `Adjust Campaign`
* `Adjust AdGroup`
* `Adjust Creative`

### <a id="qs-adjust-logging"></a>Adjust logging

You can increase or decrease the amount of logs that you see during testing by calling `setLogLevel` on Adobe Mobile Core instance with one of the following parameters:

```java
MobileCore.setLogLevel(LogLevel.VERBOSE); // enable all logs
MobileCore.setLogLevel(LogLevel.DEBUG); // disable verbose logs
MobileCore.setLogLevel(LogLevel.WARNING); // disable debug logs
MobileCore.setLogLevel(LogLevel.ERROR); // disable warning logs
```

### <a id="qs-build-the-app"></a>Build your app

Build and run your Android app. In your `LogCat` viewer, set the filter `tag:Adjust` to hide all other logs. After your app has launched you should see the following Adjust log: `Install tracked`.

## Event tracking

### <a id="et-tracking"></a>Track event

You can use Adobe `MobileCore.trackAction` API for [`event tracking`][event-tracking]. Suppose you want to track every tap on a button. To do so, you'll create a new event token in your [dashboard]. Let's say that the event token is `abc123`. In your button's `onClick` method, add the following lines to track the click:

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "abc123");

MobileCore.trackAction(action, contextData);
```

**Note**: The key used for eventToken is prefixed with **adj.** 

### <a id="et-revenue"></a>Track revenue

If your users can generate revenue by tapping on advertisements or making in-app purchases, you can track those revenues too with events. Let's say a tap is worth one Euro cent. You can track the revenue event like this:

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "abc123");
contextData.put("adj.revenue", "0.01");
contextData.put("adj.currency", "EUR");

MobileCore.trackAction(action, contextData);
```

**Note**: The key used are prefixed with **adj.** 

## Custom parameters

### <a id="cp"></a>Custom parameters overview

In addition to the data points the Adjust SDK collects by default, you can use the extension to track and add as many custom values as you need (user IDs, product IDs, etc.) to the event or session. Custom parameters are only available as raw data and will **not** appear in your Adjust dashboard.

You should use **callback parameters** for the values you collect for your own internal use, and **partner parameters** for those you share with external partners. If a value (e.g. product ID) is tracked both for internal use and external partner use, we recommend you track it with both callback and partner parameters.


### <a id="cp-event-parameters"></a>Event parameters

### <a id="cp-event-callback-parameters"></a>Event callback parameters

You can register a callback URL for your events in your [dashboard]. We will send a GET request to that URL whenever the event is tracked. You can add callback parameters to that event by adding them as key value pair to the context data map before tracking it. We will then append these parameters to your callback URL.

For example, if you've registered the URL `http://www.example.com/callback`, then you would track an event like this:

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "abc123");
contextData.put("adj.event.callback.key1", "value1");
contextData.put("adj.event.callback.key2", "value2");

MobileCore.trackAction(action, contextData);
```

In this case we would track the event and send a request to:

```
http://www.example.com/callback?key1=value1&key2=value2
```

Adjust supports a variety of placeholders, for example `{gps_adid}`, which can be used as parameter values. In the resulting callback, we would replace the placeholder (in this case)  with the Google Play Services ID of the current device. Please note that we don't store any of your custom parameters. We **only** append them to your callbacks. If you haven't registered a callback for an event, we will not even read these parameters.

You can read more about URL callbacks (including a full list of available values) in our [callbacks guide][callbacks-guide].

### <a id="cp-event-partner-parameters"></a>Event partner parameters

When your parameters are activated in the Adjust dashboard, you have the option to transmit them to your network partners.

This works similarly to the callback parameters mentioned above;

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "abc123");
contextData.put("adj.event.partner.key1", "value1");
contextData.put("adj.event.partner.key2", "value2");

MobileCore.trackAction(action, contextData);
```

You can read more about special partners and these integrations in our [guide to special partners][special-partners].

## Additional features

Once you have integrated the Adjust Extension for Adobe Experience SDK into your project, you can take advantage of the following features:

### <a id="af-attribution-callback"></a>Attribution callback

You can register a listener to be notified of tracker attribution changes. Due to the different sources we consider for attribution, we cannot provide this information synchronously.

Please see our [attribution data policies][attribution-data] for more information.

With the extension config instance, add the attribution callback before you start the SDK:

```java
AdjustAdobeExtensionConfig config = new AdjustAdobeExtensionConfig(environment);

config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
    @Override
    public void onAttributionChanged(AdjustAttribution attribution) {}
});

AdjustAdobeExtension.registerExtension(config);
```

The listener function is called after the SDK receives the final attribution data. Within the listener function, you'll have access to the `attribution` parameter. 

### <a id="af-deferred-deep-linking-callback"></a>Deferred deep linking callback

The Adjust SDK opens the deferred deep link by default. There is no extra configuration needed. But if you wish to control whether the Adjust SDK will open the deferred deep link or not, you can do it with a callback method in the config object.

With the extension config instance, add the deferred deep linking callback before you start the SDK:

```java
AdjustAdobeExtensionConfig config = new AdjustAdobeExtensionConfig(environment);

config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
    @Override
    public boolean launchReceivedDeeplink(Uri deeplink) {
        // ...
        if (shouldAdjustSdkLaunchTheDeeplink(deeplink)) {
            return true;
        } else {
            return false;
        }
    }
});

AdjustAdobeExtension.registerExtension(config);
```

After the Adjust SDK receives the deep link information from our backend, the SDK will deliver you its content via the listener and expect the boolean return value from you. This return value represents your decision on whether or not the Adjust SDK should launch the activity to which you have assigned the scheme name from the deeplink.

### <a id="af-push-token"></a>Push token (uninstall tracking)

Push tokens are used for Audience Builder and client callbacks; they are also required for uninstall and reinstall tracking.

To send us the push notification token, add the following call to Adjust once you have obtained your token (or whenever its value changes):

```java
String action = "adj.setPushToken";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.pushToken", "your_push_token");

MobileCore.trackAction(action, contextData);
```

[dashboard]:  http://adjust.com
[adjust.com]: http://adjust.com

[example-app]:       AdjustAdobeExtension/example-app

[maven]:                          http://maven.org
[google-ad-id]:                   https://support.google.com/googleplay/android-developer/answer/6048248?hl=en
[event-tracking]:                 https://docs.adjust.com/en/event-tracking
[callbacks-guide]:                https://docs.adjust.com/en/callbacks
[special-partners]:               https://docs.adjust.com/en/special-partners
[attribution-data]:               https://github.com/adjust/sdks/blob/master/doc/attribution-data.md
[android-application]:            http://developer.android.com/reference/android/app/Application.html
[google-play-services]:           http://developer.android.com/google/play-services/setup.html

