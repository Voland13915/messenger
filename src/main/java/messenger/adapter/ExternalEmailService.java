package messenger.adapter;

// Adaptee — существующий сервис с несовместимым интерфейсом
public class ExternalEmailService {

    // SpecificRequest() — другой интерфейс, не совпадает с Target
    public void sendEmail(String to, String subject, String body) {
        System.out.println("  [ExternalEmailService] Отправка email");
        System.out.println("    Кому:   " + to);
        System.out.println("    Тема:   " + subject);
        System.out.println("    Текст:  " + body);
    }
}