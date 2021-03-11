package com.terraboxstudios.backed.site.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class Settings {

    private static Settings instance;

    public SettingsConfiguration getConfig() {
        return config;
    }

    private SettingsConfiguration config;

    public Settings() {
        Yaml yaml = new Yaml();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("settings.yaml")) {
            Object obj = yaml.load(in);
            JsonObject json = new Gson().toJsonTree(obj).getAsJsonObject();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            config = new SettingsConfiguration(new Gson().fromJson(json.get("mysql").toString(), type),
                    new Gson().fromJson(json.get("mailing").toString(), type),
                    json.get("websiteHost"),
                    json.get("fileStorageLocation"),
                    json.get("maxFileSizeInKB"),
                    json.get("maxStorageSizePerUserInKB"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Settings getInstance() {
        if (instance == null) instance = new Settings();
        return instance;
    }

    public static class SettingsConfiguration {

        private final Object websiteHost, fileStorageLocation, maxFileSizeInKB, maxStorageSizePerUserInKB;
        private final Map<String, Object> mysql, mailing;

        public SettingsConfiguration(Map<String, Object> mysql, Map<String, Object> mailing, Object websiteHost, Object fileStorageLocation, Object maxFileSizeInKB, Object maxStorageSizePerUserInKB) {
            this.mysql = mysql;
            this.mailing = mailing;
            this.websiteHost = websiteHost;
            this.fileStorageLocation = fileStorageLocation;
            this.maxFileSizeInKB = maxFileSizeInKB;
            this.maxStorageSizePerUserInKB = maxStorageSizePerUserInKB;
        }

        public Object getWebsiteHost() {
            return websiteHost;
        }

        public Object getMaxFileSizeInKB() {
            return maxFileSizeInKB;
        }

        public Object getFileStorageLocation() {
            return fileStorageLocation;
        }

        public Object getMaxStorageSizePerUserInKB() {
            return maxStorageSizePerUserInKB;
        }

        public Map<String, Object> getMysql() {
            return mysql;
        }

        public Map<String, Object> getMailing() {
            return mailing;
        }

    }

}
