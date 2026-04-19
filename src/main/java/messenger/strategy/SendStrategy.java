package messenger.strategy;

/**
 * Strategy — интерфейс стратегии.
 *
 * Объявляет общий для всех алгоритмов интерфейс AlgorithmInterface().
 * Context пользуется этим интерфейсом для вызова конкретного алгоритма,
 * определённого в классе ConcreteStrategy.
 */
public interface SendStrategy {

    /**
     * AlgorithmInterface() — выполнить алгоритм отправки.
     *
     * @param context данные необходимые стратегии для работы
     * @return результат — текст который ляжет в chatHistory
     */
    String execute(SendContext context);
}