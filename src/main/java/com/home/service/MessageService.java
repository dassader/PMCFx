package com.home.service;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;

public interface MessageService {

    void sendReport(File screenShot) throws MessagingException;
}
