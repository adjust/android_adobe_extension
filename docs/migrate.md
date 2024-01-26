## Migrate Adjust Android Extension for Adobe Experience Platform SDK from v1.1.0 to v2.0.0

### Migration procedure

Go to your app's `build.gradle` and update the dependencies as below:

```
implementation 'com.adjust.adobeextension:adobeextension:2.0.0
implementation 'com.adobe.marketing.mobile:core:2.6.1'
```

In case your app's minimum SDK version is below API 19, update it to 19 in `build.gradle` as below: 

```
minSdkVersion 19
```

### SDK initialization

We have changed how you configure and register the Adjust Extension. The following steps should now be 
taken to configure the Adjust Extension:

1. Create an instance of an `AdjustAdobeExtensionConfig` config object with environment.
2. Optionally, you can call methods of the `AdjustAdobeExtensionConfig` object to specify available 
   options same as earlier.
3. Set the `AdjustAdobeExtensionConfig` config object to the Adjust Adobe Extension by invoking
   `AdjustAdobeExtensionConfig.setConfiguration` with the config object.
4. Register Adjust Adobe Extention with Adobe's Mobile Core SDK by invoking 
   `MobileCore.registerExtension` method.

Here is an example of how the setup might look before and after the migration:

##### Before

You needed to edit app's `Application` class and to replace the code as below.

```java
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
```

##### After

```java
// configure Adjust Adobe extension
try {
    MobileCore.configureWithAppID("your_adobe_app_id");
    String environment = AdjustAdobeExtensionConfig.ENVIRONMENT_SANDBOX;
    AdjustAdobeExtensionConfig config = new AdjustAdobeExtensionConfig(environment);

    // register the Adjust SDK extension
    AdjustAdobeExtension.setConfiguration(config);
} catch (Exception e) {
    Log.e("example", "Exception while configuration: " + e.getMessage());
}

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
```

### Event tracking

We also introduced proper constants to use.  Again, an example of how it might look like before and after:

##### Before

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "{EventToken}");

MobileCore.trackAction(action, contextData);
```

##### After

```java
String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
Map<String, String> contextData= new HashMap<String, String>();
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "g3mfiw");

MobileCore.trackAction(action, contextData);
```

### Revenue tracking

We also introduced proper constants to use.  Again, an example of how it might look like before and after:

##### Before

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "{EventToken}");
contextData.put("adj.revenue", "{RevenueAmount}");
contextData.put("adj.currency", "{RevenueCurrency}");

MobileCore.trackAction(action, contextData);
```

##### After

```java
String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
Map<String, String> contextData= new HashMap<String, String>();
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "{EventToken}");
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_REVENUE, "{RevenueAmount}");
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_CURRENCY, "{RevenueCurrency}");

MobileCore.trackAction(action, contextData);
```

### Event callback parameters

We also introduced proper constants to use.  Again, an example of how it might look like before and after:

##### Before

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "{EventToken}");
contextData.put("adj.event.callback.key1", "value1");
contextData.put("adj.event.callback.key2", "value2");

MobileCore.trackAction(action, contextData);
```

##### After

```java
String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
Map<String, String> contextData= new HashMap<String, String>();
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "{EventToken}");
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_CALLBACK_PARAM_PREFIX + "key1", "value1");
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_CALLBACK_PARAM_PREFIX + "key2", "value2");

MobileCore.trackAction(action, contextData);
```

### Event partner parameters

We also introduced proper constants to use.  Again, an example of how it might look like before and after:

##### Before

```java
String action = "adj.trackEvent";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.eventToken", "{EventToken}");
contextData.put("adj.event.partner.key1", "value1");
contextData.put("adj.event.partner.key2", "value2");

MobileCore.trackAction(action, contextData);
```

##### After

```java
String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
Map<String, String> contextData= new HashMap<String, String>();
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "{EventToken}");
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_PARTNER_PARAM_PREFIX + "key1", "value1");
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_PARTNER_PARAM_PREFIX + "key2", "value2");

MobileCore.trackAction(action, contextData);
```

### Push token (uninstall tracking)

We also introduced proper constants to use.  Again, an example of how it might look like before and after:

##### Before

```java
String action = "adj.setPushToken";
Map<String, String> contextData= new HashMap<String, String>();
contextData.put("adj.pushToken", "{PushToken}");

MobileCore.trackAction(action, contextData);
```

##### After

```java
String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_SET_PUSH_TOKEN;
Map<String, String> contextData= new HashMap<String, String>();
contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_PUSH_TOKEN, "{PushToken}");

MobileCore.trackAction(action, contextData);
```