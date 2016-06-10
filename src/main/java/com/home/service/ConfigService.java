package com.home.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service
public class ConfigService {

    public static final String CONFIG_FILE = "config.init";

    public void save(Properties properties) {
        BufferedOutputStream configFileOutputStream;
        try {
            configFileOutputStream = new BufferedOutputStream(new FileOutputStream(getConfigFile()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fail open config file!", e);
        }

        try {
            properties.store(configFileOutputStream, null);
        } catch (IOException e) {
            throw new RuntimeException("Fail write into config file!", e);
        }
    }

    private File getConfigFile() {
        File configFile = new File(CONFIG_FILE);

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                return getConfigFile();
            } catch (IOException e) {
                throw new RuntimeException("Fail create  config file!", e);
            }
        }

        return configFile;
    }

    public Properties load() {
        File configFile = getConfigFile();

        Properties properties = new Properties();
        try {
            properties.load(new BufferedInputStream(new FileInputStream(configFile)));
        } catch (IOException e) {
            throw new RuntimeException("Fail load properties!", e);
        }

        return properties;
    }
}
