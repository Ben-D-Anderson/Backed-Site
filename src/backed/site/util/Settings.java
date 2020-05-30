package backed.site.util;

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
                    json.get("fileStorageLocation"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Settings getInstance() {
        if (instance == null) instance = new Settings();
        return instance;
    }

    public class SettingsConfiguration {

        private Object websiteHost, fileStorageLocation;
        private Map<String, Object> mysql;
        private Map<String, Object> mailing;

        public SettingsConfiguration(Map<String, Object> mysql, Map<String, Object> mailing, Object websiteHost, Object fileStorageLocation) {
            this.mysql = mysql;
            this.mailing = mailing;
            this.websiteHost = websiteHost;
            this.fileStorageLocation = fileStorageLocation;
        }

        public Object getWebsiteHost() {
            return websiteHost;
        }

        public Object getFileStorageLocation() {
            return fileStorageLocation;
        }

        public Map<String, Object> getMysql() {
            return mysql;
        }

        public void setMysql(Map<String, Object> mysql) {
            this.mysql = mysql;
        }

        public Map<String, Object> getMailing() {
            return mailing;
        }

    }

}
