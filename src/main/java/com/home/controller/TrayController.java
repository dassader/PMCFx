package com.home.controller;

import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import static com.home.GlobalConstant.PROPERTY_NAME.APP_NAME;

@Component
public class TrayController {

    @Autowired
    private StartUpController startUpController;

    @PostConstruct
    private void init() {
        if (SystemTray.isSupported()) {
            URL resource = this.getClass().getClassLoader().getResource("icon.png");

            final SystemTray systemTray = SystemTray.getSystemTray();
            final TrayIcon trayIcon = new TrayIcon(new ImageIcon(resource).getImage(), APP_NAME, initPopupMenu());
            trayIcon.setImageAutoSize(true);

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Platform.runLater(() -> startUpController.onAlreadyRun());
                }
            };
            trayIcon.addMouseListener(mouseAdapter);
            try {
                systemTray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private PopupMenu initPopupMenu() {
        PopupMenu popupMenu = new PopupMenu();

        MenuItem exit = new MenuItem("Exit");

        exit.addActionListener(target -> System.exit(0));

        popupMenu.add(exit);

        return popupMenu;
    }

}
