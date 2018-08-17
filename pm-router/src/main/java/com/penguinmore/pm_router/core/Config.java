package com.penguinmore.pm_router.core;

public final class Config {


    String[] modules;

    private Config() {

    }


    public static class Builder {

        String[] modules;

        public Builder registerModules(String ...modules) {
            this.modules = modules;
            return this;
        }

        public Config build() {
            if (null == modules || modules.length == 0) {
                throw new RuntimeException("You should register modules before build");
            }
            Config config = new Config();
            config.modules = this.modules;
            return config;
        }


    }

}
