package com.penguinmore.pm_router.core;

import android.content.Context;

public interface IRouter {

    Mail build(String path);
    void navigation(Context context,Mail mail);

}
