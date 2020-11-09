# Adjust SDK Extension

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
      * [Adjust logging](#qs-adjust-logging)
      * [Build your app](#qs-build-the-app)

### Event tracking

   * [Track event](#et-tracking)
   * [Track revenue](#et-revenue)

### Additional features

   * [Attribution callback](#af-attribution-callback)
   * [Session and event callbacks](#af-session-event-callbacks)


## Quick start

### <a id="qs-example-app"></a>Example app

There is an Android example app inside the [example-app][example-java] directory. You can open the Android project to see the example on how the Adjust Adobe Extension can be integrated.

### <a id="qs-getting-started"></a>Getting started

These are the minimum required steps to integrate the Adjust SDK Extension in your Android app. We assume that you are using Android Studio for your Android development. The minimum supported Android API level for the Adjust Adobe Extension integration is **14 (Ice Cream Sandwich)**.

### <a id="qs-add-extension"></a>Add the Extension to your project

If you are using [`Maven`][maven], add the following to your `build.gradle` file:

```gradle
implementation 'com.adjust.adobeextension:adjust-android-adobe-extension:1.0.0'
implementation 'com.adjust.sdk:adjust-android:4.24.1'
implementation 'com.android.installreferrer:installreferrer:2.1'
```

### <a id="qs-gps"></a>Add Google Play Services

Apps in the Google Play Store must use the [Google Advertising ID][google-ad-id] to uniquely identify devices. To enable the Google Advertising ID for our SDK, you must integrate [Google Play Services][google-play-services]. If you haven't done this yet, please add dependency to the Google Play Services library by adding the following dependecy to your `dependencies` block of app's `build.gradle` file:

```gradle
implementation 'com.google.android.gms:play-services-ads-identifier:17.0.0'
```

**Note**: The Adjust SDK Extension is not tied to any specific version of the `play-services-analytics` part of the Google Play Services library. You can use the latest version of the library, or any other version you need.

### <a id="qs-permissions"></a>Add permissions

The Adjust SDK Extension requires the following permissions. Please add them to your `AndroidManifest.xml` file if they are not already present:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

If you are **not targeting the Google Play Store**, you must also add the following permission:

```xml
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```

### <a id="qs-proguard"></a>Proguard settings

If you are using Proguard, add these lines to your Proguard file:

```
-keep class com.adjust.adobeextension.** { *; }
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
-keep class com.adjust.adobeextension.** { *; }
-keep class com.adjust.sdk.** { *; }
```

### <a id="qs-install-referrer"></a>Install referrer

In order to correctly attribute an app install to its source, Adjust needs information about the **install referrer**. We can achieve this by using the **Google Play Referrer API** for apps on Google Play Store and by using the **Huawei Referrer API** for apps on Huawei Mobile Store.

#### <a id="qs-gpr-api"></a>Google Play Referrer API

In order to support the Google Play Referrer API in your app, please make sure that you have followed our chapter on [adding the Extension to your project](#qs-add-extension) correctly and that you have following line added to your `build.gradle` file:

```
implementation 'com.android.installreferrer:installreferrer:2.1'
```

Please follow the directions for your [Proguard settings](#qs-proguard) carefully. Confirm that you have added all the rules mentioned in it, especially the one needed for this feature:

```
-keep public class com.android.installreferrer.** { *; }
```

### <a id="qs-integrate-extension"></a>Integrate the Adjust SDK Extension into your app

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

In `onCreate` method of your `Application` class, set the application object to Adobe `MobileCore.setApplication` API before registering the Adjust Extension:


```java
import com.adobe.marketing.mobile.MobileCore;


public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // set application object
        MobileCore.setApplication(this);

        // register Adjust Adobe extension
    }
}
```

**Note**: This step is **very important**. Please **make sure that you implement this step in your app**. Completing this step ensures that the Adjust SDK can properly track sessions in your app.

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
String action = "Track button click";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "abc123");

MobileCore.trackAction(action, contextData);
```

**Note**: The key used for eventToken is prefixed with **adj.** 

### <a id="et-revenue"></a>Track revenue

If your users can generate revenue by tapping on advertisements or making in-app purchases, you can track those revenues too with events. Let's say a tap is worth one Euro cent. You can track the revenue event like this:

```java
String action = "Track revenue event";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "abc123");
contextData.put("adj.revenue", "0.01");
contextData.put("adj.currency", "EUR");
```

**Note**: The key used are prefixed with **adj.** 


## Additional features

Once you have integrated the Adjust Adobe Extension into your project, you can take advantage of the following features:

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

The listener function is called after the SDK receives the final attribution data. Within the listener function, you'll have access to the `attribution` parameter. Here is a quick summary of its properties:

- `trackerToken` the tracker token string of the current attribution.
- `trackerName` the tracker name string of the current attribution.
- `network` the network grouping level string of the current attribution.
- `campaign` the campaign grouping level string of the current attribution.
- `adgroup` the ad group grouping level string of the current attribution.
- `creative` the creative grouping level string of the current attribution.
- `clickLabel` the click label string of the current attribution.
- `adid` the Adjust device identifier string.

### <a id="af-session-event-callbacks"></a>Session and event callbacks

You can register a listener to be notified when events or sessions are tracked. There are four listeners: one for tracking successful events, one for tracking failed events, one for tracking successful sessions, and one for tracking failed sessions. Add as many listeners as you need after creating the config object like so:

```java
AdjustAdobeExtensionConfig config = new AdjustAdobeExtensionConfig(environment);

// Set event success tracking delegate.
config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
    @Override
    public void onFinishedEventTrackingSucceeded(AdjustEventSuccess eventSuccessResponseData) {
        // ...
    }
});

// Set event failure tracking delegate.
config.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener() {
    @Override
    public void onFinishedEventTrackingFailed(AdjustEventFailure eventFailureResponseData) {
        // ...
    }
});

// Set session success tracking delegate.
config.setOnSessionTrackingSucceededListener(new OnSessionTrackingSucceededListener() {
    @Override
    public void onFinishedSessionTrackingSucceeded(AdjustSessionSuccess sessionSuccessResponseData) {
        // ...
    }
});

// Set session failure tracking delegate.
config.setOnSessionTrackingFailedListener(new OnSessionTrackingFailedListener() {
    @Override
    public void onFinishedSessionTrackingFailed(AdjustSessionFailure sessionFailureResponseData) {
        // ...
    }
});

AdjustAdobeExtension.registerExtension(config);
```

Here is a quick summary of the success session response data object fields:

- `message` message string from the server (or the error logged by the SDK).
- `timestamp` timestamp string from the server.
- `adid` a unique string device identifier provided by Adjust.
- `jsonResponse` the JSON object with the reponse from the server.

Both event response data objects contain:

- `eventToken` the event token string, if the package tracked was an event.
- `callbackId` the custom defined [callback ID](#cp-event-callback-id) string set on the event object.

And both event and session failed objects also contain:

- `willRetry` boolean which indicates whether there will be a later attempt to resend the package.

[dashboard]:  http://adjust.com
[adjust.com]: http://adjust.com

[example-java]:       AdjustAdobeExtension/example-app-java

[maven]:                          http://maven.org
[google-ad-id]:                   https://support.google.com/googleplay/android-developer/answer/6048248?hl=en
[event-tracking]:                 https://docs.adjust.com/en/event-tracking
[attribution-data]:               https://github.com/adjust/sdks/blob/master/doc/attribution-data.md
[android-application]:            http://developer.android.com/reference/android/app/Application.html
[google-play-services]:           http://developer.android.com/google/play-services/setup.html

