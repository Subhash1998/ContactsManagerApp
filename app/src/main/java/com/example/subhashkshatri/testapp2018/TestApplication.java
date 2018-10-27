package com.example.subhashkshatri.testapp2018;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class TestApplication extends Application
{
    public static final String APPLICATION_ID = "5189B703-E2A4-0C45-FFAA-196FAAD7B300";
    public static final String API_KEY = "96181E89-CAC1-C783-FF63-3B6062775D00";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Contact> contacts;

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );
    }
}
