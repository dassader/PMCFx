package com.home.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static com.home.GlobalConstant.PROPERTY_NAME.*;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ConfigService configService;

    @Override
    public void sendReport(File screenShot) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "owabud.epam.com");
        props.put("mail.smtp.port", "25");

        Properties properties = configService.load();

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty(LOGIN), properties.getProperty(PASSWORD));
            }
        });

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(properties.getProperty(LOGIN)));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(properties.getProperty(LOGIN)));
        message.setSubject(APP_NAME);

        //======================================
        MimeMultipart multipart = new MimeMultipart("related");

        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = "<img style='width: 80%' src=\"cid:image\">";
        messageBodyPart.setContent(htmlText, "text/html");
        multipart.addBodyPart(messageBodyPart);

        //=======================================
        messageBodyPart = new MimeBodyPart();
        DataSource fileDataSource = null;
        try {
            fileDataSource = new ByteArrayDataSource(new BufferedInputStream(new FileInputStream(screenShot)), "image/png");
        } catch (java.io.IOException e) {
            throw new RuntimeException("Fail send screenshot!", e);
        }

        messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
        messageBodyPart.setHeader("Content-ID", "<image>");

        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
