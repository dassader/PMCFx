package com.home;

import com.google.common.io.Files;
import com.home.controller.StartUpController;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ResourceUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RunMe extends Application {
    public static final String PHANTOMJS_FILE = "phantomjs.exe";
    private static final Logger log = LoggerFactory.getLogger(RunMe.class);
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        resolvePhantomJs();
        System.setProperty("selenide.browser", "phantomjs");
        System.setProperty("selenide.reports", System.getProperty("java.io.tmpdir"));
        System.setProperty("phantomjs.binary.path", PHANTOMJS_FILE);
        launch(args);
    }

    private static void resolvePhantomJs() {
        File file = new File("./"+PHANTOMJS_FILE);

        if (!file.exists()) {
            try {
                file.createNewFile();
                IOUtils.copy(RunMe.class.getClassLoader().getResourceAsStream(PHANTOMJS_FILE), new BufferedOutputStream(new FileOutputStream(file)));
            } catch (IOException e) {
                log.error("Fail extract phantom js", e);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);

        applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        applicationContext.getBean(StartUpController.class).onStart();
    }
}
