package messenger.abstractfactory;

/**
 * AbstractFactory — абстрактная фабрика ботов.
 * Объявляет интерфейс для создания семейства связанных объектов:
 * Bot (обработчик) и BotProfile (профиль).
 */
public interface BotFactory {
    Bot createBot();
    BotProfile createBotProfile();
}
