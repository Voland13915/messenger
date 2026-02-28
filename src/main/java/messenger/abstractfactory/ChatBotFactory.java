package messenger.abstractfactory;

// ConcreteProduct A1 — чат-бот
class ChatBot implements Bot {
    @Override
    public String getName() { return "ChatBot Assistant"; }

    @Override
    public void handleMessage(String message) {
        System.out.println("  [ChatBot] Получено: \"" + message + "\" → Ответ: Привет! Чем могу помочь?");
    }
}

// ConcreteProduct B1 — профиль чат-бота
class ChatBotProfile implements BotProfile {
    @Override
    public void showInfo() {
        System.out.println("  [Профиль] ChatBot — универсальный помощник, отвечает на вопросы пользователей.");
    }
}

/**
 * ConcreteFactory1 — создаёт семейство объектов для чат-бота.
 */
public class ChatBotFactory implements BotFactory {

    @Override
    public Bot createBot() {
        return new ChatBot();
    }

    @Override
    public BotProfile createBotProfile() {
        return new ChatBotProfile();
    }
}
