package com.home.controller;

import static com.home.GlobalConstant.PROPERTY_NAME.LOGIN;
import static com.home.GlobalConstant.PROPERTY_NAME.PASSWORD;
import static com.home.GlobalConstant.PROPERTY_NAME.URL;
import static javafx.scene.control.Alert.AlertType.ERROR;

import com.home.AuthenticationException;
import com.home.service.ConfigService;
import com.home.service.PmcService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.util.Properties;

import javax.annotation.PostConstruct;

@Component
public class LoginController extends FXController {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private TextField url;
    @FXML
    private Button loginButton;
    @FXML
    private ProgressIndicator progressIndicator;

    @Autowired
    private PmcService pmcService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MainController mainController;

    @Override
    protected String getNameFXmlFile() {
        return "Login.fxml";
    }

    @PostConstruct
    private void init() {
        stage.setResizable(false);
    }

    public void login() {
        saveFields();

        progressIndicator.setVisible(true);

        new Thread(() -> {
            try {
                pmcService.getActivitiesList();
                Platform.runLater(() -> mainController.show());
            } catch (AuthenticationException ex) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(ERROR);
                    alert.setContentText("Fail check login data!");
                    alert.setHeaderText(null);
                    alert.show();
                });
            } finally {
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }
        }).start();
    }

    private void saveFields() {
        Properties properties = configService.load();

        properties.setProperty(LOGIN, login.getText());
        properties.setProperty(PASSWORD, password.getText());
        properties.setProperty(URL, url.getText());

        configService.save(properties);
    }

    @Override
    public void show() {
        Properties properties = configService.load();

        login.setText(properties.getProperty(LOGIN));
        password.setText(properties.getProperty(PASSWORD));
        url.setText(properties.getProperty(URL));

        super.show();
    }
}
