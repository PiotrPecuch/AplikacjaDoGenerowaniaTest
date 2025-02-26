package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailSenderService {

    @Value("${email.domain}")
    private String domain;

    private final JavaMailSender javaMailSender;

    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(String userEmail,String adminEmail, String link) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(adminEmail);
            mimeMessageHelper.setTo(userEmail);
            mimeMessageHelper.setSubject("Zresetowanie hasła");

            String content = "<p>Czesc, </p>" +
                    "<p>Poprosiles o zmiane hasla.</p>" +
                    "<p>Kliknij w <a href=\"" + link + "\">link</a>, aby zmienic haslo</p>";

            mimeMessageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    @Async
    public void sendNotification(String userEmail, List<ExamStudentsInformations> esiList) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(userEmail);

            for(ExamStudentsInformations esi : esiList) {

                mimeMessageHelper.setTo(esi.getIndexNumber().toString() + domain);
                mimeMessageHelper.setSubject("Powiadomienie o ocenie");

                String content = String.format(
                        "Z egzaminu %s otrzymałeś następującą liczbę punktów: %.2f/%s%%<br>Adres email prowadzącego: %s",
                        esi.getGeneratedExam().getExamDescription(),
                        esi.getPointsFromExam(),
                        esi.getPercentageOfPoints(),
                        userEmail
                );

                mimeMessageHelper.setText(content, true);

                javaMailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
