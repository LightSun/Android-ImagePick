package com.heaven7.android.pick.app;

import androidx.multidex.MultiDexApplication;

import common.network.HttpMethods;

public class TestApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpMethods.initialize(this);
    }
}
