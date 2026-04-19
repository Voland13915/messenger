package messenger.strategy;

/**
 * Context — контекст (по диаграмме GoF: Composition).
 *
 * - Конфигурируется объектом ConcreteStrategy
 * - Хранит ссылку на объект Strategy
 * - Переадресует запросы клиентов объекту-стратегии:
 *   contextInterface() → strategy.AlgorithmInterface()
 *
 * Клиент (MessengerWindow) создаёт ConcreteStrategy и передаёт контексту,
 * после чего общается исключительно с контекстом.
 */
public class MessageSender {

    // Context хранит ссылку на Strategy
    private SendStrategy strategy;

    /**
     * Конфигурация контекста объектом ConcreteStrategy.
     * Клиент вызывает это при смене типа сообщения.
     */
    public void setStrategy(SendStrategy strategy) {
        this.strategy = strategy;
        System.out.println("[MessageSender] Стратегия установлена: "
                + strategy.getClass().getSimpleName());
    }

    public SendStrategy getStrategy() { return strategy; }

    /**
     * ContextInterface() — интерфейс для клиента.
     *
     * Переадресует запрос стратегии:
     *   strategy.AlgorithmInterface(context)
     *
     * @return текст сообщения для добавления в chatHistory
     */
    public String send(SendContext context) {
        if (strategy == null) {
            throw new IllegalStateException("Стратегия не установлена");
        }
        // Context переадресует запрос стратегии
        return strategy.execute(context);
    }
}