package com.penguinmore.pm_router.core;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.penguinmore.pm_router.template.InjectionParam;

import java.util.HashMap;
import java.util.Map;

public class Postman implements IRouter {
    // injector's name -> injector
    private static Map<String, Class<InjectionParam>> injectors = new HashMap<>();
    public static final String PARAM_INJECT = "$$InjectParam";

    private Postman() {

    }

    private static Postman instance = null;


    public static Postman getInstance() {

        if (null == instance) {
            synchronized (Postman.class) {
                if (null == instance)
                    instance = new Postman();
            }
        }

        return instance;
    }

    @Override
    public Mail build(String path) {

        Mail mail = new Mail();
        mail.setUri(Uri.parse(path));

        return mail;
    }

    @Override
    public void navigation(Context context, Mail mail) {
        Class<?> desClass = WareHouse.mRouteTable.get(mail.getUri().toString());
        Intent intent = new Intent();
        intent.setClass(context, desClass);

        //Set Flags
        if (-1 != mail.getFlags()) {
            intent.setFlags(mail.getFlags());
        }

        //Set Bundle
        intent.putExtras(mail.getBundle());


        context.startActivity(intent);
    }

    public void inject(Object object){
        if (object instanceof Activity ) {
            String key = object.getClass().getCanonicalName();
            Class<InjectionParam> clz;
            if (!injectors.containsKey(key)) {
                try {
                    //noinspection unchecked
                    clz = (Class<InjectionParam>) Class.forName(key + PARAM_INJECT);
                    injectors.put(key, clz);
                } catch (ClassNotFoundException e) {
                    return;
                }
            } else {
                clz = injectors.get(key);
            }
            try {
                InjectionParam injector = clz.newInstance();
                injector.inject(object);
            } catch (Exception e) {
            }
        } else {
        }
    }
}
