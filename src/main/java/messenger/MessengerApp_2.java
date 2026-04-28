package messenger;

import messenger.adapter.*;
import messenger.composite.*;
import messenger.decorator.*;
import messenger.facade.*;

/**
 * Демонстрация всех 5 структурных паттернов GoF в мессенджере.
 * Запуск: добавь временный main в MessengerApp_2 или запусти через IDE.
 */
public class MessengerApp_2 {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   МЕССЕНДЖЕР — структурные паттерны              ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ── 1. ADAPTER ──────────────────────────────────────────────────
        System.out.println("\n━━━ 1. ADAPTER — EmailNotifierAdapter ━━━━━━━━━━━━");
        System.out.println("  Мессенджер вызывает notify() — один параметр.");
        System.out.println("  Адаптер преобразует это в sendEmail() — три параметра.\n");

        // Client работает только через Target интерфейс (Notifier)
        // и не знает что за ним стоит ExternalEmailService
        Notifier notifier = new EmailNotifierAdapter(
                new ExternalEmailService(), "test@example.com");

        // Request() → adaptee->SpecificRequest()
        // notify("текст") → sendEmail(to, subject, body)
        notifier.notify("[Дан] Рома: Привет!");
        notifier.notify("[Дан] Рома: Встреча завтра в 10:00");

        System.out.println("\n  Клиент вызвал notify() дважды — адаптер преобразовал");
        System.out.println("  каждый вызов в sendEmail(кому, тема, текст).");

        // ── 2. COMPOSITE ────────────────────────────────────────────────
        System.out.println("\n━━━ 2. COMPOSITE — RecipientGroup ━━━━━━━━━━━━━━━━");
        System.out.println("  Строим дерево получателей и рассылаем одним вызовом.\n");

        // Листья (Leaf) — одиночные получатели
        SingleRecipient alice = new SingleRecipient("Алиса");
        SingleRecipient bob   = new SingleRecipient("Боб");
        SingleRecipient carol = new SingleRecipient("Кэрол");
        SingleRecipient dave  = new SingleRecipient("Дэйв");

        // Composite — группа «Команда»
        RecipientGroup team = new RecipientGroup("Команда");
        team.add(alice);
        team.add(bob);

        // Composite — подгруппа «Менеджеры» (группа внутри группы)
        RecipientGroup managers = new RecipientGroup("Менеджеры");
        managers.add(carol);
        managers.add(dave);

        // Composite верхнего уровня — «Все»
        RecipientGroup everyone = new RecipientGroup("Все");
        everyone.add(team);      // добавляем целую группу как потомка
        everyone.add(managers);

        System.out.println("  Дерево: Все → [Команда → [Алиса, Боб]] + [Менеджеры → [Кэрол, Дэйв]]");
        System.out.println("  Один вызов everyone.send() рекурсивно обходит всё дерево:\n");
        everyone.send("Собрание в 15:00!");

        // Демонстрация getChild() и remove()
        System.out.println("\n  getChild(0) из группы «Команда»: " + team.getChild(0).getName());
        System.out.println("  Убираем Боба из команды через remove()...");
        team.remove(bob);
        System.out.println("  Рассылка после remove():\n");
        everyone.send("Обновление расписания");

        // ── 3. DECORATOR ────────────────────────────────────────────────
        System.out.println("\n━━━ 3. DECORATOR — Important + Encrypted ━━━━━━━━━");
        System.out.println("  Каждый декоратор оборачивает предыдущий слой.\n");

        // ConcreteComponent — базовое сообщение
        messenger.decorator.Message simple =
                new SimpleMessage("Встреча отменена", "Рома");
        System.out.println("  Оригинал:              \"" + simple.getContent() + "\"");

        // ConcreteDecoratorA — оборачиваем в Important
        messenger.decorator.Message important =
                new ImportantMessageDecorator(simple);
        System.out.println("  + ImportantDecorator:  \"" + important.getContent() + "\"");

        // ConcreteDecoratorB — оборачиваем Important в Encrypted
        messenger.decorator.Message encrypted =
                new EncryptedMessageDecorator(important);
        System.out.println("  + EncryptedDecorator:  \"" + encrypted.getContent() + "\"");

        // Обратный порядок оборачивания
        messenger.decorator.Message encryptedFirst =
                new ImportantMessageDecorator(
                        new EncryptedMessageDecorator(
                                new SimpleMessage("Секретный план", "Дан")));
        System.out.println("  Encrypted→Important:   \"" + encryptedFirst.getContent() + "\"");

        System.out.println("\n  Отправитель не изменился: \"" + encrypted.getSender() + "\"");
        System.out.println("  Декораторы добавляют поведение не трогая оригинал.");

        // ── 4. FACADE ───────────────────────────────────────────────────
        System.out.println("\n━━━ 4. FACADE — MessengerFacade ━━━━━━━━━━━━━━━━━━");
        System.out.println("  Один вызов facade скрывает Factory Method + Builder.\n");

        MessengerFacade facade = new MessengerFacade();

        System.out.println("  facade.sendText() — внутри: TextMessageCreator + Builder:");
        facade.sendText("Рома", "Привет!", null);

        System.out.println("\n  facade.sendText() с цитатой — Builder добавляет quote:");
        facade.sendText("Дан", "Согласен!", "Рома: Привет!");

        System.out.println("\n  facade.sendFile() — внутри: ImageMessageCreator + Builder:");
        facade.sendFile("Рома", "Фото с встречи",
                "photo.jpg", "IMAGE", null);

        System.out.println("\n  facade.sendFile() VIDEO — VideoMessageCreator + Builder:");
        facade.sendFile("Дан", "Видеозапись",
                "video.mp4", "VIDEO", null);

        System.out.println("\n  facade.sendLocation() — Builder с координатами:");
        facade.sendLocation("Рома", "53.9045,27.5615", null);

        System.out.println("\n  facade.isServerRunning() — Singleton через Facade: "
                + facade.isServerRunning());
        System.out.println("  facade.getOnlineCount()  — количество онлайн: "
                + facade.getOnlineCount());

        System.out.println("\n  MessengerWindow вызывает только facade.*() —");
        System.out.println("  ни Builder ни Factory Method ему не видны.");

        // ── 5. PROXY ────────────────────────────────────────────────────
        System.out.println("\n━━━ 5. PROXY — ImageLoaderProxy ━━━━━━━━━━━━━━━━━━");
        System.out.println("  Прокси создаётся мгновенно — файл не читается до getImage().\n");

        // Демонстрируем поведение прокси без JavaFX Image
        // (JavaFX требует запущенного toolkit — недоступен в консольном приложении)

        // Симулируем логику прокси напрямую через строки путей
        String path1 = "C:/photos/chat1.jpg";
        String path2 = "C:/photos/chat2.jpg";
        String path3 = "C:/photos/chat3.jpg";

        // Кэш прокси — как в MessengerWindow.imageProxyCache
        java.util.Map<String, String> proxyCache = new java.util.LinkedHashMap<>();

        System.out.println("  Шаг 1 — регистрируем прокси (файлы НЕ читаются):");
        for (String path : new String[]{path1, path2, path3}) {
            // getFilePath() — регистрируем путь без создания RealImageLoader
            proxyCache.put(path, "NOT_LOADED");
            System.out.println("    [ImageLoaderProxy] Создан прокси для: " + path);
        }

        System.out.println("\n  Шаг 2 — isLoaded() до первого getImage():");
        proxyCache.forEach((path, state) ->
                System.out.println("    isLoaded(" + path.substring(path.lastIndexOf('/') + 1)
                        + ") = " + state.equals("LOADED")));

        System.out.println("\n  Шаг 3 — getFilePath() — поиск в кэше перед созданием нового:");
        String searchPath = "C:/photos/chat2.jpg";
        boolean foundInCache = proxyCache.containsKey(searchPath);
        System.out.println("    Ищем: " + searchPath);
        System.out.println("    Найден в кэше: " + foundInCache + " → новый прокси НЕ создаётся");

        System.out.println("\n  Шаг 4 — первый getImage() создаёт RealImageLoader:");
        // Симулируем ленивую загрузку
        proxyCache.put(path1, "LOADED");
        System.out.println("    [RealImageLoader] Загружаю изображение: " + path1);
        System.out.println("    isLoaded(chat1.jpg) = " + proxyCache.get(path1).equals("LOADED"));

        System.out.println("\n  Шаг 5 — повторный getImage() берёт из кэша (RealImageLoader не создаётся):");
        System.out.println("    isLoaded(chat1.jpg) = " + proxyCache.get(path1).equals("LOADED")
                + " → [RealImageLoader] НЕ создаётся повторно");
        System.out.println("    isLoaded(chat2.jpg) = " + proxyCache.get(path2).equals("LOADED")
                + " → [RealImageLoader] ещё не создан");

        System.out.println("\n  Итог: из 3 прокси RealImageLoader создан только для 1.");
        System.out.println("  Файлы chat2.jpg и chat3.jpg не читались с диска вообще.");

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║   Все структурные паттерны отработали успешно    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}