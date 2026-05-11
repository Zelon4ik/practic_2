package com.mysuperproject.infrastructure.email;

import com.mysuperproject.service.port.EmailSender;
import com.mysuperproject.util.PropertiesUtil;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpEmailSender implements EmailSender {

    private final String username;
    private final String password;
    private final String fromEmail;
    private final Properties props;

    public SmtpEmailSender() {
        // Зчитуємо конфігурацію з application.properties
        this.username = PropertiesUtil.get("mail.username");
        this.password = PropertiesUtil.get("mail.password");
        this.fromEmail = PropertiesUtil.get("mail.from");

        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Важливо для Mailtrap та інших сервісів
        props.put("mail.smtp.host", PropertiesUtil.get("mail.host"));
        props.put("mail.smtp.port", PropertiesUtil.get("mail.port"));
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        Session session =
                Session.getInstance(
                        props,
                        new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            System.out.println("Email sent successfully to " + to);

        } catch (AuthenticationFailedException e) {
            System.err.println(
                    "Authentication failed for email sender. Please check your mail.username and mail.password in application.properties.");
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + to);
            e.printStackTrace();
            throw new RuntimeException("Помилка при надсиланні email: " + e.getMessage(), e);
        }
    }
}
