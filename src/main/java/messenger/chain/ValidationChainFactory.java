package messenger.chain;

/**
 * Строитель цепочки — клиент не знает из каких обработчиков она состоит.
 *
 * Структура объектов (по диаграмме GoF):
 *
 *   Client → EmptyMessage → Length → Recipient → Connection → (конец)
 *                 ↓              ↓           ↓            ↓
 *             Преемник       Преемник    Преемник     passToSuccessor → ok()
 */
public class ValidationChainFactory {

    /**
     * Собрать цепочку и вернуть первый обработчик.
     * Client обращается только к нему — остальные скрыты.
     */
    public static MessageValidator build() {
        // Создаём ConcreteHandler-ы
        MessageValidator emptyCheck      = new EmptyMessageValidator();
        MessageValidator lengthCheck     = new LengthValidator();
        MessageValidator recipientCheck  = new RecipientValidator();
        MessageValidator connectionCheck = new ConnectionValidator();

        // Строим цепочку: устанавливаем преемников
        // aClient → aConcreteHandler → aConcreteHandler → aConcreteHandler
        emptyCheck
                .setSuccessor(lengthCheck)
                .setSuccessor(recipientCheck)
                .setSuccessor(connectionCheck);

        // Возвращаем голову цепочки
        return emptyCheck;
    }
}