package com.home;

import com.home.controller.StartUpController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunMe extends Application {
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        System.setProperty("selenide.browser", "phantomjs");
        System.setProperty("selenide.reports", System.getProperty("java.io.tmpdir"));
        System.setProperty("phantomjs.binary.path", "phantomjs.exe");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);

        applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        applicationContext.getBean(StartUpController.class).onStart();
    }
}
