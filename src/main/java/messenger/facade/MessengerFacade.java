package messenger.facade;

import messenger.builder.*;
import messenger.factorymethod.*;
import messenger.prototype.*;
import messenger.singleton.WebSocketManager;

public class MessengerFacade {

    // Singleton внутри — снаружи не видно
    private final WebSocketManager wsManager = WebSocketManager.getInstance();

    // ── Отправить текст (+ цитата если есть) ─────────────────
    public ComplexMessage sendText(String sender, String text, String quote) {
        // Factory Method
        new TextMessageCreator().factoryMethod(text);

        // Builder
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, text, quote, null, null);
        return builder.getResult();
    }

    // ── Отправить файл + подпись (+ цитата если есть) ────────
    public ComplexMessage sendFile(String sender, String caption,
                                   String filePath, String type, String quote) {
        // Factory Method
        MessageCreator creator = type.equals("IMAGE")
                ? new ImageMessageCreator()
                : new VideoMessageCreator();
        creator.factoryMethod(filePath);

        // Builder
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, caption, quote, filePath, null);
        return builder.getResult();
    }

    // ── Отправить геолокацию (+ цитата если есть) ────────────
    public ComplexMessage sendLocation(String sender, String coordinates, String quote) {
        // Builder
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, "📍 " + coordinates, quote, null, coordinates);
        return builder.getResult();
    }

    // ── Переслать сообщение — Prototype ───────────────────────
    public messenger.prototype.Message forwardMessage(String text, String sender,
                                                      String fromChat, String toChat) {
        messenger.prototype.TextMessage proto =
                new messenger.prototype.TextMessage(text, sender, fromChat);
        return new MessageForwarder(proto).forward(toChat);
    }

    // ── Статус соединений — Singleton ─────────────────────────
    public int getOnlineCount() {
        return wsManager.getConnectionCount();
    }

    public boolean isServerRunning() {
        return wsManager.isRunning();
    }
}