package messenger.abstractfactory;

// ConcreteProduct A2 — новостной бот
class NewsBot implements Bot {
    @Override
    public String getName() { return "NewsBot TechNews"; }

    @Override
    public void handleMessage(String message) {
        System.out.println("  [NewsBot] Запрос: \"" + message + "\" → Последние новости: AI достиг нового рекорда!");
    }
}

// ConcreteProduct B2 — профиль новостного бота
class NewsBotProfile implements BotProfile {
    @Override
    public void showInfo() {
        System.out.println("  [Профиль] NewsBot — рассылает актуальные новости из мира технологий.");
    }
}

/**
 * ConcreteFactory2 — создаёт семейство объектов для новостного бота.
 */
public class NewsBotFactory implements BotFactory {

    @Override
    public Bot createBot() {
        return new NewsBot();
    }

    @Override
    public BotProfile createBotProfile() {
        return new NewsBotProfile();
    }
}
