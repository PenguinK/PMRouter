package com.penguinmore.pm_router.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Postman implements IRouter {


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
        intent.putExtra("params", mail.getBundle());



        context.startActivity(intent);
    }
}
