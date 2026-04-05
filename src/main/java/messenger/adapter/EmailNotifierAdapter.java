package messenger.adapter;

// Adapter — адаптирует ExternalEmailService к интерфейсу Notifier
public class EmailNotifierAdapter implements Notifier {

    // adaptee — ссылка на адаптируемый объект
    private final ExternalEmailService adaptee;
    private final String recipientEmail;

    public EmailNotifierAdapter(ExternalEmailService adaptee, String recipientEmail) {
        this.adaptee         = adaptee;
        this.recipientEmail  = recipientEmail;
    }

    // Request() → adaptee->SpecificRequest()
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