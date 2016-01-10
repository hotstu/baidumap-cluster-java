package com.dzbkq.myapplication;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

/**
 */
public class MyApp extends Application {
    private static Context instance;
    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        instance = this;
    }

    public static Context getInstance() {
        return instance;
    }
}
