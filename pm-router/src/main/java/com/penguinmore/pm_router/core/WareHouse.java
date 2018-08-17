package com.penguinmore.pm_router.core;

import com.penguinmore.pm_router.template.RouteTable;
import com.penguinmore.pm_router.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class WareHouse {

    public static final String PACKAGE_NAME = "com.penguinmore.router";

    public static final String DOT = ".";

    public static final String SUFFIX_NAME = "RouteTable";


    public static Map<String, Class<?>> mRouteTable = new HashMap<>();


    public static void loadRouteTables(String[] modules) {

        if (null == modules || modules.length == 0) {
            //TODO throw or log
            return;
        }

        for (String module : modules) {

            try {
                String routeTableName = PACKAGE_NAME + DOT + StringUtils.capitalize(module) + SUFFIX_NAME;

                Class<?> routeClass = Class.forName(routeTableName);

                RouteTable routeTable = (RouteTable) routeClass.newInstance();

                routeTable.handle(mRouteTable);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

    }


}
