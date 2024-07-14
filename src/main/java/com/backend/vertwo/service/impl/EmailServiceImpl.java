package com.backend.vertwo.service.impl;

import com.backend.vertwo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.backend.vertwo.utils.email.EmailUtils.getEmailMessage;
import static com.backend.vertwo.utils.email.EmailUtils.getResetPasswordMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String NEW_USER_ACCOUNT_VERIFY_EMAIL = "New User Account Verify Email";
    private static final String RESET_PASSWORD_REQUEST = "Reset Password Email";
    private static final Logger log = LogManager.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender sender;

//    @Value("${spring.mail.verify.host}")
    @Value("Hello")
    private String host;

//    @Value("${spring.mail.username}")
    @Value("myemail@gmail.com")
    private String fromEmail;

    @Override
    public void sendNewAccountEmail(String name, String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFY_EMAIL);
            message.setFrom(fromEmail);
            message.setTo(email);

            message.setText(getEmailMessage(name, host, token));
            sender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Unable to send new account email");
        }
    }

    @Override
    public void sendResetPasswordEmail(String name, String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(RESET_PASSWORD_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(email);

            message.setText(getResetPasswordMessage(name, host, token));
            sender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Unable to send new account email");
        }
    }
}
