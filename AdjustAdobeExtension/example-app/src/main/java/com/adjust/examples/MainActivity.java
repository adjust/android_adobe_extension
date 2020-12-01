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
        String action = "Track simple event";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.eventToken", "g3mfiw");

        MobileCore.trackState(action, contextData);
    }

    public void trackRevenueEvent(View view) {
        String action = "Track revenue event";
        Map<String, String> contextData= new HashMap<String, String>();
        contextData.put("adj.eventToken", "a4fd35");
        contextData.put("adj.revenue", "0.01");
        contextData.put("adj.currency", "EUR");

        MobileCore.trackAction(action, contextData);
    }
}