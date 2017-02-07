package resources;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {

    private static final String BASE_NAME = "resources.equals";

    private static ResourceBundle i18nBundle =
        ResourceBundle.getBundle(BASE_NAME);

    public static void setLocale(Locale locale) {
        i18nBundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }

    public static ResourceBundle getResourceBundle() {
        return i18nBundle;
    }

    public static String getString(String key) {
        try {
            return i18nBundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}