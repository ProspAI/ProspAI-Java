package br.com.fiap.prospai.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class UTF8Control extends Control {
    @Override
    public ResourceBundle newBundle
            (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");

        try (InputStream is = loader.getResourceAsStream(resourceName);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            return new PropertyResourceBundle(isr);
        }
    }
}
