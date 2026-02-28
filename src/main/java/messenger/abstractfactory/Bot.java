package messenger.abstractfactory;

/**
 * AbstractProductA — абстрактный бот.
 * Объявляет интерфейс для бота-обработчика сообщений.
 */
public interface Bot {
    String getName();
    void handleMessage(String message);
}
