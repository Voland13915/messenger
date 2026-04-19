package messenger.chain;

/**
 * Handler — обработчик запросов.
 *
 * По диаграмме GoF:
 *   - определяет интерфейс HandleRequest()
 *   - хранит ссылку на преемника (successor)
 *   - реализует связь с преемником: если не может обработать — передаёт дальше
 *
 * Запрос = попытка отправить сообщение.
 * Результат = ValidationResult (успех или причина блокировки).
 */
public abstract class MessageValidator {

    // Преемник — следующий обработчик в цепочке
    private MessageValidator successor = null;

    /**
     * Установить преемника — строим цепочку.
     * Возвращает преемника для удобного chain-вызова:
     *   first.setSuccessor(second).setSuccessor(third)
     */
    public MessageValidator setSuccessor(MessageValidator successor) {
        this.successor = successor;
        return successor;
    }

    /**
     * HandleRequest() — обработать запрос.
     *
     * Если ConcreteHandler способен обработать — делает это.
     * Если нет — передаёт преемнику: successor.HandleRequest(request)
     */
    public abstract ValidationResult handleRequest(ValidationRequest request);

    /**
     * Вспомогательный метод — передать запрос преемнику.
     * Вызывается из ConcreteHandler когда он не блокирует запрос.
     */
    protected ValidationResult passToSuccessor(ValidationRequest request) {
        if (successor != null) {
            return successor.handleRequest(request);
        }
        // Конец цепочки — все проверки пройдены
        return ValidationResult.ok();
    }
}