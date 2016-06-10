package com.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Collections;

@SpringBootApplication
@EnableCaching
public class ApplicationConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @Scope("prototype")
    public FXMLLoader fxmlLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();

        fxmlLoader.setControllerFactory(clazz -> applicationContext.getBean(clazz));

        return fxmlLoader;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        Cache cache = fileCache();

        cacheManager.setCaches(Collections.singletonList(cache));

        return cacheManager;
    }

    @Bean
    public FileCache fileCache() {
        return new FileCache<>(false, "fileCache");
    }
}
