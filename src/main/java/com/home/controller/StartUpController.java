package com.home.controller;

import com.home.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.home.GlobalConstant.PROPERTY_NAME.*;

@Component
public class StartUpController {

    @Autowired
    private LoginController loginController;

    @Autowired
    private MainController mainController;

    @Autowired
    private ConfigService configService;

    public void onStart() {
        Properties load = configService.load();

        if (load.getProperty(LOGIN) == null ||
                load.getProperty(PASSWORD) == null) {
            loginController.show();
        }
    }

    public void onAlreadyRun() {
        Properties load = configService.load();

        if (load.getProperty(LOGIN) != null &&
                load.getProperty(PASSWORD) != null &&
                load.getProperty(URL) != null) {
            mainController.show();
        } else {
            loginController.show();
        }
    }
}
