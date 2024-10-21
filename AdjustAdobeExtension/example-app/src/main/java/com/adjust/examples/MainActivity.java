package com.adjust.examples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adjust.adobeextension.AdjustAdobeExtension;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustDeeplink;
import com.adobe.marketing.mobile.MobileCore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Uri data = intent.getData();
        AdjustDeeplink adjustDeeplink = new AdjustDeeplink(data);
        Adjust.processDeeplink(adjustDeeplink, getApplicationContext());
    }

    public void trackSimpleEvent(View view) {
        String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "g3mfiw");

        MobileCore.trackAction(action, contextData);
    }

    public void trackRevenueEvent(View view) {
        String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "a4fd35");
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_REVENUE, "0.01");
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_CURRENCY, "EUR");

        MobileCore.trackAction(action, contextData);
    }

    public void trackCallbackEvent(View view) {
        String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "34vgg9");
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_CALLBACK_PARAM_PREFIX + "key1", "value1");
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_CALLBACK_PARAM_PREFIX + "key2", "value2");

        MobileCore.trackAction(action, contextData);
    }

    public void trackPartnerEvent(View view) {
        String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_TRACK_EVENT;
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_TOKEN, "w788qs");
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_PARTNER_PARAM_PREFIX + "key1", "value1");
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_EVENT_PARTNER_PARAM_PREFIX + "key2", "value2");

        MobileCore.trackAction(action, contextData);
    }

    public void setPushToken(View view) {
        String action = AdjustAdobeExtension.ADOBE_ADJUST_ACTION_SET_PUSH_TOKEN;
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put(AdjustAdobeExtension.ADOBE_ADJUST_PUSH_TOKEN, "your_push_token");

        MobileCore.trackAction(action, contextData);
    }
}