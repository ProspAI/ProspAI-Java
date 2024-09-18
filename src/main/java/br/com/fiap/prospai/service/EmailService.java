package br.com.fiap.prospai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMarketingEmail(String to, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Campanha de Marketing Personalizada");
        message.setText(content); // Ou configure um template HTML para o corpo do email
        mailSender.send(message);
    }
}
