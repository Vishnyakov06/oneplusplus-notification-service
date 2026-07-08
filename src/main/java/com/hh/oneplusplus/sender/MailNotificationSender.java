package com.hh.oneplusplus.sender;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("MAIL")
public class MailNotificationSender implements NotificationSender{
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public MailNotificationSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Long userId, String email, NotificationResponseDto notificationResponseDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("One++ уведомление");
        message.setText(notificationResponseDto.fallbackMessage());
        mailSender.send(message);
    }
}
