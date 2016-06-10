package com.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.AuthenticationException;
import com.home.OpenMainPageException;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Selenide.*;
import static com.home.GlobalConstant.PROPERTY_NAME.*;

@Component
public class PmcServiceImpl implements PmcService {

    public static final String FILE_NAME = "pmc-auto-fill";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConfigService configService;

    @Override
    @Cacheable("fileCache")
    public List<String> getActivitiesList() {

        openTimeJournal();

        executeJavaScript(resourceAsString("jquery.js"));

        String activities = executeJavaScript(resourceAsString("findActivities.js"));

        List<String> activityList;
        try {
            activityList = objectMapper.readValue(activities, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,String.class));
        } catch (IOException e) {
            throw new OpenMainPageException();
        }

        close();

        return activityList;
    }

    @Override
    public void fillTime(String activityName, String time) {
        openTimeJournal();

        executeJavaScript(resourceAsString("jquery.js"));

        String fillTimeScript = resourceAsString("fillTime.js")
                .replace("%activityName%", activityName)
                .replace("%time%", time);

        executeJavaScript(fillTimeScript);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        $(By.xpath("//input[@value='Save Changes']")).click();

        close();
    }

    @Override
    public File makeScreenShot() {
        openTimeJournal();

        new File(reportsFolder, FILE_NAME + ".png").deleteOnExit();

        screenshot(FILE_NAME);

        close();

        return new File(reportsFolder, FILE_NAME + ".png");
    }

    private void openTimeJournal() {
        Properties load = configService.load();
        String login = load.getProperty(LOGIN);
        String password = load.getProperty(PASSWORD);
        String serverUrl = load.getProperty(URL);

        open(serverUrl);

        $(getElement(By.name("username"))).setValue(login);
        $(getElement(By.name("password"))).setValue(password);
        $(getElement(By.name("Login"))).click();

        if ($(getElement(By.className("blContentContainerText"))).text().contains("Invalid user ID or password")) {
            throw new AuthenticationException();
        }
    }

    private String resourceAsString(String name) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(name);

        try {
            return IOUtils.toString(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("Error read "+name+" file!", e);
        }
    }

}
