package com.penguinmore.adapterannotation;

import android.app.Application;

import com.penguinmore.pm_router.core.Config;
import com.penguinmore.pm_router.core.Router;
import com.penguinmore.pmannotation.Route;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.getInstance()
                .init(new Config.Builder().registerModules("app", "othermodule","kotlinmodule").build());
    }
}
