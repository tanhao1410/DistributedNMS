package com.tanhao.collection.utils;

import java.util.ResourceBundle;

public class PropertiesUtil {

    private static ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("env");
    }

    public static String getPropertiesByName(String name) {
        return resourceBundle.getString(name);
    }

}
