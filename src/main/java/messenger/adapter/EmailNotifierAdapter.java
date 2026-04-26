package messenger.adapter;

public class EmailNotifierAdapter implements Notifier {

    private final ExternalEmailService adaptee;
    private final String recipientEmail;

    public EmailNotifierAdapter(ExternalEmailService adaptee, String recipientEmail) {
        this.adaptee         = adaptee;
        this.recipientEmail  = recipientEmail;
    }

    @Override
    public void notify(String message) {
        // Адаптируем вызов — преобразуем notify() в sendEmail()
        adaptee.sendEmail(
                recipientEmail,
                "Новое сообщение в Messenger",
                message
        );
    }
}