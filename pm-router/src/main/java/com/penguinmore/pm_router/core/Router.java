package com.penguinmore.pm_router.core;

import android.content.Context;

public class Router {
    private static Router instance = null;

    private Router() {

    }

    public static Router getInstance() {

        if (null == instance) {
            synchronized (Router.class) {
                if (null == instance) {
                    instance = new Router();
                }
            }
        }
        return instance;
    }

    public void init(Config config) {

        WareHouse.loadRouteTables(config.modules);

    }

    public void navigation(Context context,Mail mail){
        Postman.getInstance().navigation(context, mail);
    }

    public Mail build(String path){
        return Postman.getInstance().build(path);
    }




}
