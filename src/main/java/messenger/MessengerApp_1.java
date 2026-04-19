package messenger;

public class MessengerApp_1 {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   МЕССЕНДЖЕР — порождающие паттерны              ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ── 1. SINGLETON ────────────────────────────────────────────────
        System.out.println("\n━━━ 1. SINGLETON — WebSocketManager ━━━━━━━━━━━━━━");

        messenger.singleton.WebSocketManager ws1 = messenger.singleton.WebSocketManager.getInstance();
        messenger.singleton.WebSocketManager ws2 = messenger.singleton.WebSocketManager.getInstance();
        System.out.println("  ws1 и ws2 — один и тот же объект: " + (ws1 == ws2));

        // Новый WebSocketManager работает с реальным сокетом,
        // поэтому для демонстрации паттерна выводим факт единственности
        System.out.println("  Singleton гарантирует один экземпляр на всё приложение.");
        System.out.println("  Подключение к серверу происходит через UI (MessengerWindow).");

        // ── 2. FACTORY METHOD ───────────────────────────────────────────
        System.out.println("\n━━━ 2. FACTORY METHOD — типы сообщений ━━━━━━━━━━━");
        System.out.println("  sendMessage() одинаковый, Creator решает что создать:\n");

        new messenger.factorymethod.TextMessageCreator().sendMessage("Как дела?");
        new messenger.factorymethod.ImageMessageCreator().sendMessage("https://cdn.example.com/photo.jpg");
        new messenger.factorymethod.VideoMessageCreator().sendMessage("https://cdn.example.com/video.mp4");

        // ── 3. ABSTRACT FACTORY ─────────────────────────────────────────
        System.out.println("\n━━━ 3. ABSTRACT FACTORY — темы оформления ━━━━━━━━");
        System.out.println("  Фабрика гарантирует что все элементы одной темы:\n");

        System.out.println("  -- Светлая тема --");
        messenger.abstractfactory.MessengerUI lightUI =
                new messenger.abstractfactory.MessengerUI(new messenger.abstractfactory.LightThemeFactory());
        lightUI.renderUI();

        System.out.println("  -- Тёмная тема --");
        messenger.abstractfactory.MessengerUI darkUI =
                new messenger.abstractfactory.MessengerUI(new messenger.abstractfactory.DarkThemeFactory());
        darkUI.renderUI();

        // ── 4. BUILDER ──────────────────────────────────────────────────
        System.out.println("\n━━━ 4. BUILDER — составное сообщение ━━━━━━━━━━━━━");
        System.out.println("  Простое сообщение (только текст):\n");

        messenger.builder.ConcreteMessageBuilder builder1 = new messenger.builder.ConcreteMessageBuilder();
        new messenger.builder.MessageDirector(builder1).construct("alice", "Привет!", null, null, null);
        System.out.println("  " + builder1.getResult());

        System.out.println("\n  Полное сообщение (текст + цитата + вложение + геолокация):\n");

        messenger.builder.ConcreteMessageBuilder builder2 = new messenger.builder.ConcreteMessageBuilder();
        new messenger.builder.MessageDirector(builder2).construct(
                "bob", "Встретимся здесь!", "Договорились!",
                "https://cdn.example.com/map.jpg", "53.9045,27.5615"
        );
        System.out.println("  " + builder2.getResult());

        // ── 5. PROTOTYPE ────────────────────────────────────────────────
        System.out.println("\n━━━ 5. PROTOTYPE — пересылка сообщений ━━━━━━━━━━━");
        System.out.println("  Клонируем сообщение и меняем только chatId:\n");

        messenger.prototype.TextMessage original =
                new messenger.prototype.TextMessage("Привет!", "alice", "chat_1");
        System.out.println("  Оригинал : [" + original.getChatId() + "] "
                + original.getSender() + " → \"" + original.getContent() + "\"");

        messenger.prototype.MessageForwarder forwarder = new messenger.prototype.MessageForwarder(original);
        messenger.prototype.Message forwarded = forwarder.forward("chat_2");
        System.out.println("  Переслано: [" + forwarded.getChatId() + "] "
                + forwarded.getSender() + " → \"" + forwarded.getContent() + "\"");

        System.out.println("  Оригинал не изменился: chatId = " + original.getChatId());

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║   Все паттерны отработали успешно                ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}