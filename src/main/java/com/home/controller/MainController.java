package com.home.controller;

import com.home.FileCache;
import com.home.service.ConfigService;
import com.home.service.MessageService;
import com.home.service.PmcService;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.File;
import java.util.List;
import java.util.Properties;

import static com.home.GlobalConstant.PROPERTY_NAME.*;

@Component
public class MainController extends FXController {

    @FXML
    private TextField timeFiled;

    @FXML
    private ComboBox<String> targetBox;

    @FXML
    private CheckBox sendMessageCheck;

    @FXML
    private CheckBox autorunCheck;

    @Autowired
    private PmcService pmcService;

    @Autowired
    private FileCache fileCache;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private LoginController loginController;

    @Override
    protected String getNameFXmlFile() {
        return "Main.fxml";
    }

    @PostConstruct
    private void init() {
        stage.setResizable(false);
    }

    @Override
    public void show() {
        timeFiled.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) {
                timeFiled.setText(newValue);
            } else {
                timeFiled.setText(oldValue);
            }
        });

        initTargetBox();

        loadViewFromProperties();

        super.show();
    }

    public void updateActivity() {
        fileCache.clear();
        initTargetBox();
    }

    public void logOut() {
        Properties properties = configService.load();

        properties.remove(LOGIN);
        properties.remove(PASSWORD);

        configService.save(properties);

        fileCache.clear();

        loginController.show();
    }

    private void loadViewFromProperties() {
        Properties properties = configService.load();

        targetBox.setPromptText(properties.getProperty(TARGET_ACTIVITY));
        timeFiled.setText(properties.getProperty(FILL_TIME));
        sendMessageCheck.setSelected(Boolean.parseBoolean(properties.getProperty(SEND_MESSAGE_AFTER_FILL)));
        autorunCheck.setSelected(Boolean.parseBoolean(properties.getProperty(AUTORUN)));
    }

    public void apply() {
        Properties properties = configService.load();

        String targetBoxValue = targetBox.getValue();
        if (targetBoxValue != null) {
            properties.setProperty(TARGET_ACTIVITY, targetBoxValue);
        }
        properties.setProperty(FILL_TIME, timeFiled.getText());
        properties.setProperty(SEND_MESSAGE_AFTER_FILL, String.valueOf(sendMessageCheck.isSelected()));
        properties.setProperty(AUTORUN, String.valueOf(autorunCheck.isSelected()));

        configService.save(properties);

        autorunUpdate(autorunCheck.isSelected());
    }

    private void autorunUpdate(boolean selected) {
        if (selected) {
            String value = "javaw -jar \"" + System.getProperty("user.dir") + "\\"+getJarName()+"\"";
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", APP_NAME, value);
        } else {
            Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", APP_NAME);
        }
    }

    private String getJarName() {
        return new java.io.File(this.getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }

    public void fillTime() {
        Properties properties = configService.load();

        pmcService.fillTime(properties.getProperty(TARGET_ACTIVITY), properties.getProperty(FILL_TIME));

        if (Boolean.parseBoolean(properties.getProperty(SEND_MESSAGE_AFTER_FILL))) {
            try {
                File file = pmcService.makeScreenShot();
                messageService.sendReport(file);
            } catch (MessagingException e) {
                throw new RuntimeException("Fail send message!", e);
            }
        }
    }

    private void initTargetBox() {
        List<String> activitiesList = pmcService.getActivitiesList();

        targetBox.getItems().addAll(activitiesList);
    }
}
