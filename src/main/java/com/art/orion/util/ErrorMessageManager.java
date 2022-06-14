package com.art.orion.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ErrorMessageManager {
    protected static final String BUNDLE_NAME = "message";

    private ErrorMessageManager() {
    }

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getMessage(String key) {
        return resourceBundle.getString(key);
    }

    public static void setLocale(String language) {
        Locale.setDefault(new Locale(language));
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(language));
    }
}
