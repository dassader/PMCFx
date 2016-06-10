package com.home.controller;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

import static com.home.GlobalConstant.PROPERTY_NAME.APP_NAME;

@Lazy
public abstract class FXController {
    private static FXController currentController;

    @Autowired
    private FXMLLoader fxmlLoader;
    protected Stage stage;

    public FXController() {
        stage = new Stage();
    }

    @PostConstruct
    private void init() throws IOException {
        URL fxml = getClass().getClassLoader().getResource(getNameFXmlFile());

        fxmlLoader.setLocation(fxml);

        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);

        stage.setTitle(APP_NAME);
        stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icon.png")));
    }

    protected abstract String getNameFXmlFile();

    public void show() {
        if (currentController != null && !isModal()) {
            currentController.hide();
        }

        currentController = this;

        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    protected boolean isModal() {
        return false;
    }
}
