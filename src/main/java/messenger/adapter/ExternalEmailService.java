package messenger.adapter;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class ExternalEmailService {

    private static final String SMTP_HOST     = "smtp.gmail.com";
    private static final String SMTP_PORT     = "587";
    private static final String FROM_EMAIL    = "migovdan031@gmail.com";   // ← твой Gmail
    private static final String APP_PASSWORD  = "nnks wqzp lybu srsd";    // ← App Password

    public void sendEmail(String to, String subject, String body) {
        System.out.println("  [ExternalEmailService] Отправка email...");
        System.out.println("    Кому:   " + to);
        System.out.println("    Тема:   " + subject);
        System.out.println("    Текст:  " + body);

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            SMTP_PORT);
        props.put("mail.smtp.ssl.trust",       SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body, "UTF-8");

            Transport.send(message);
            System.out.println("  [ExternalEmailService] ✓ Email успешно отправлен на: " + to);

        } catch (MessagingException e) {
            System.err.println("  [ExternalEmailService] ✗ Ошибка отправки: " + e.getMessage());
            System.err.println("  Проверь FROM_EMAIL и APP_PASSWORD в ExternalEmailService.java");
        }
    }
}