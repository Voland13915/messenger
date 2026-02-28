package messenger.abstractfactory;

// ConcreteProduct A3 — бот-помощник
class AssistantBot implements Bot {
    @Override
    public String getName() { return "Assistant Bot"; }

    @Override
    public void handleMessage(String message) {
        System.out.println("  [AssistantBot] Задача: \"" + message + "\" → Выполняю: добавил в календарь.");
    }
}

// ConcreteProduct B3 — профиль бота-помощника
class AssistantBotProfile implements BotProfile {
    @Override
    public void showInfo() {
        System.out.println("  [Профиль] AssistantBot — управляет задачами, напоминаниями и расписанием.");
    }
}

/**
 * ConcreteFactory3 — создаёт семейство объектов для бота-помощника.
 */
public class AssistantBotFactory implements BotFactory {

    @Override
    public Bot createBot() {
        return new AssistantBot();
    }

    @Override
    public BotProfile createBotProfile() {
        return new AssistantBotProfile();
    }
}
