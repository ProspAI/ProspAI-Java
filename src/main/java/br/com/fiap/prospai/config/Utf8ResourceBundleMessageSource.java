package br.com.fiap.prospai.config;

import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.Locale;
import java.util.ResourceBundle;

public class Utf8ResourceBundleMessageSource extends ResourceBundleMessageSource {
    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) {
        return ResourceBundle.getBundle(basename, locale, new UTF8Control());
    }
}
