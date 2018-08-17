package com.penguinmore.pm_router.template;

import java.util.Map;

public interface RouteTable {
    /**
     * handle the routeMap data
     *
     * @param map
     */
    void handle(Map<String, Class<?>> map);
}
