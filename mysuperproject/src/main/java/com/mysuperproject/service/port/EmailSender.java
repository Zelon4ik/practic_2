package com.mysuperproject.service.port;

public interface EmailSender {
    void sendEmail(String to, String subject, String text);
}
