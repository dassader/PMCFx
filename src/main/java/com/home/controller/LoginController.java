package com.home.controller;

import com.home.AuthenticationException;
import com.home.service.ConfigService;
import com.home.service.PmcService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static com.home.GlobalConstant.PROPERTY_NAME.*;
import static javafx.scene.control.Alert.AlertType.ERROR;

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

        loginButton.setDisable(true);

        Platform.runLater(() -> {
            try {
                pmcService.getActivitiesList();
                mainController.show();
            } catch (AuthenticationException ex) {
                Alert alert = new Alert(ERROR);
                alert.setContentText("Fail check login data!");
                alert.setHeaderText(null);
                alert.show();
            }

            loginButton.setDisable(false);
        });
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
