package net.Phoenix.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigHandler {

    public static Properties config = new Properties();

    public static void init() {
        // Trying to load config
        try {
            // Attempt load
            config.load(new FileInputStream("./config.properties"));
        } catch (FileNotFoundException exception) {
            // Check if the config file exists else stop running code
            System.out.println("Please re-download config.properties from the github and put it in the same directory as this jar");
            System.exit(1);
        } catch (IOException e) {
            // ConfigHandler errors that are not handled
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean getConfigBool(String name) {
        return Boolean.parseBoolean(config.getProperty(name));
    }

    public static String getConfigString(String name) {
        return config.getProperty(name);
    }

    public static long getConfigLong(String name) {
        return Long.parseLong(config.getProperty(name));
    }

    public static void toggleFeature(String name, boolean enabled) {
        config.setProperty(name, String.valueOf(enabled));
    }

    public static void setConfigProperty(String name, String value) {
        config.setProperty(name, value);
    }

}
