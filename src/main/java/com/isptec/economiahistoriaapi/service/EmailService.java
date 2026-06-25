package com.isptec.economiahistoriaapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("economiacomhistoria@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Recuperação de Senha - Economia com História");
        message.setText("Olá,\n\n"
                + "Recebemos um pedido para repor a sua senha.\n"
                + "Clique no link abaixo para criar uma nova senha:\n\n"
                + resetLink + "\n\n"
                + "Se não solicitou a reposição da senha, ignore este email.\n\n"
                + "Cumprimentos,\nEquipa Economia com História");

        mailSender.send(message);
    }
}
