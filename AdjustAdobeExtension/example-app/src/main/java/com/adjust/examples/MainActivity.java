package com.adjust.examples;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adobe.marketing.mobile.MobileCore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void trackSimpleEvent(View view) {
        String action = "adj.trackEvent";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.eventToken", "g3mfiw");

        MobileCore.trackState(action, contextData);
    }

    public void trackRevenueEvent(View view) {
        String action = "adj.trackEvent";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.eventToken", "a4fd35");
        contextData.put("adj.revenue", "0.01");
        contextData.put("adj.currency", "EUR");

        MobileCore.trackAction(action, contextData);
    }

    public void trackCallbackEvent(View view) {
        String action = "adj.trackEvent";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.eventToken", "34vgg9");
        contextData.put("adj.event.callback.key1", "value1");
        contextData.put("adj.event.callback.key2", "value2");

        MobileCore.trackAction(action, contextData);
    }

    public void trackPartnerEvent(View view) {
        String action = "adj.trackEvent";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.eventToken", "w788qs");
        contextData.put("adj.event.partner.key1", "value1");
        contextData.put("adj.event.partner.key2", "value2");

        MobileCore.trackAction(action, contextData);
    }

    public void setPushToken(View view) {
        String action = "adj.setPushToken";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.pushToken", "your_push_token");

        MobileCore.trackAction(action, contextData);

        MobileCore.setPushIdentifier("your_push_token");
    }
}