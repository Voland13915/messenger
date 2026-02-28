package messenger;

import messenger.factorymethod.*;
import messenger.abstractfactory.*;
import messenger.singleton.*;
import messenger.builder.*;
import messenger.prototype.*;

/**
 * Точка входа. Демонстрирует работу всех пяти порождающих паттернов
 * в контексте мессенджера с ботами и файлами.
 */
public class Main {

    public static void main(String[] args) {

        // ─────────────────────────────────────────────────────────────────
        // 1. FACTORY METHOD — создание разных типов сообщений
        // ─────────────────────────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         Паттерн 1: Factory Method                ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Идея: Creator полагается на подклассы для создания конкретного продукта.\n");

        // Клиент работает с Creator, не зная конкретного типа сообщения
        MessageCreator textCreator  = new TextMessageCreator();
        MessageCreator imageCreator = new ImageMessageCreator();
        MessageCreator videoCreator = new VideoMessageCreator(45);

        textCreator.sendMessage("alice", "Привет! Как дела?");
        imageCreator.sendMessage("bob", "photo_2025.jpg");
        videoCreator.sendMessage("carol", "Моя поездка в горы");

        // ─────────────────────────────────────────────────────────────────
        // 2. ABSTRACT FACTORY — создание семейств ботов
        // ─────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║         Паттерн 2: Abstract Factory              ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Идея: фабрика создаёт семейство связанных объектов (Bot + BotProfile).\n");

        // Три конкретные фабрики создают три разных семейства ботов
        BotFactory[] factories = {
            new ChatBotFactory(),
            new NewsBotFactory(),
            new AssistantBotFactory()
        };

        for (BotFactory factory : factories) {
            Bot bot = factory.createBot();
            BotProfile profile = factory.createBotProfile();

            System.out.println("--- Создан: " + bot.getName() + " ---");
            profile.showInfo();
            bot.handleMessage("расскажи мне что-нибудь");
        }

        // ─────────────────────────────────────────────────────────────────
        // 3. SINGLETON — глобальный WebSocket-менеджер
        // ─────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║         Паттерн 3: Singleton                     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Идея: единственный экземпляр, доступный через Instance().\n");

        // Все вызовы getInstance() возвращают один и тот же объект
        WebSocketManager manager1 = WebSocketManager.getInstance();
        manager1.start();
        manager1.connect("alice");
        manager1.connect("bob");

        WebSocketManager manager2 = WebSocketManager.getInstance(); // тот же экземпляр
        manager2.connect("carol");

        System.out.println("  manager1 == manager2? " + (manager1 == manager2));
        System.out.println("  Активных соединений: " + manager2.getConnectionCount());

        manager1.disconnect("bob");
        System.out.println("  После отключения bob: " + manager1.getConnectionCount() + " соединения");

        // ─────────────────────────────────────────────────────────────────
        // 4. BUILDER — создание сложных сообщений пошагово
        // ─────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║         Паттерн 4: Builder                       ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Идея: Director управляет построением, Builder собирает части.\n");

        // Director использует Builder для пошагового создания сообщений
        MessageDirector director1 = new MessageDirector(new RichMessageBuilder("system"));
        RichMessage notification = director1.buildNotification("Вас добавили в группу «Java Dev»");
        System.out.println("--- Уведомление ---");
        notification.display();

        MessageDirector director2 = new MessageDirector(new RichMessageBuilder("alice"));
        RichMessage mediaMsg = director2.buildMediaMessage(
                "Смотри какое фото!",
                "https://cdn.example.com/sunset.jpg",
                "report_2025.pdf"
        );
        System.out.println("--- Медиасообщение ---");
        mediaMsg.display();

        // Прямое использование Builder без Director (произвольная конфигурация)
        RichMessage customMsg = new RichMessageBuilder("bob")
                .setText("Файл с отчётом")
                .setAttachment("q4_results.xlsx")
                .addButton("Открыть")
                .addButton("Сохранить")
                .addButton("Удалить")
                .build();
        System.out.println("--- Произвольное сообщение ---");
        customMsg.display();

        // ─────────────────────────────────────────────────────────────────
        // 5. PROTOTYPE — клонирование сообщений при пересылке
        // ─────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║         Паттерн 5: Prototype                     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Идея: клиент просит прототип создать свою копию.\n");

        // Оригинальное сообщение
        ChatMessage original = new ChatMessage("alice", "Встреча в пятницу в 15:00", "10:32");
        System.out.println("Оригинал:");
        original.display();

        // Клонирование при пересылке — клиент не знает деталей создания
        ChatMessage forwarded = original.clone();
        forwarded.setForwardedBy("bob");
        System.out.println("\nПереслал bob:");
        forwarded.display();

        ChatMessage forwardedAgain = forwarded.clone();
        forwardedAgain.setForwardedBy("carol");
        System.out.println("\nПереслала carol:");
        forwardedAgain.display();

        // Клонирование файлового сообщения
        FileMessage fileOriginal = new FileMessage("dave", "presentation.pptx", 2_048_000);
        FileMessage fileForwarded = fileOriginal.clone();
        fileForwarded.setForwardedBy("eve");
        System.out.println("\nФайловое сообщение (пересланное):");
        fileForwarded.display();

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║   Все 5 порождающих паттернов продемонстрированы ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}
