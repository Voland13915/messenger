package messenger.facade;

import messenger.builder.*;
import messenger.factorymethod.*;
import messenger.prototype.*;
import messenger.singleton.WebSocketManager;

public class MessengerFacade {

    // Singleton внутри — снаружи не видно
    private final WebSocketManager wsManager = WebSocketManager.getInstance();

    // ── Отправить текст (+ цитата если есть) ─────────────────────────
    public void sendText(String sender, String text, String quote) {
        // Factory Method — создаёт нужный тип сообщения
        new TextMessageCreator().factoryMethod(text);

        // Builder — собирает составное сообщение
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, text, quote, null, null);
    }

    // ── Отправить файл + подпись ───────────────────────────────────────
    public void sendFile(String sender, String caption,
                         String filePath, String type, String quote) {
        // Factory Method — IMAGE или VIDEO
        MessageCreator creator = type.equals("IMAGE")
                ? new ImageMessageCreator()
                : new VideoMessageCreator();
        creator.factoryMethod(filePath);

        // Builder — собирает составное сообщение с вложением
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, caption, quote, filePath, null);
    }

    // ── Отправить геолокацию ───────────────────────────────────────────
    public void sendLocation(String sender, String coordinates, String quote) {
        // Builder — собирает сообщение с координатами
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(
                sender, "📍 " + coordinates, quote, null, coordinates);
    }

    // ── Переслать сообщение — Prototype ───────────────────────────────
    // Возвращает Message т.к. нужен контент для отправки через WebSocket
    public messenger.prototype.Message forwardMessage(String text, String sender,
                                                      String fromChat, String toChat) {
        messenger.prototype.TextMessage proto =
                new messenger.prototype.TextMessage(text, sender, fromChat);
        return new MessageForwarder(proto).forward(toChat);
    }

    // ── Статус соединений — Singleton ─────────────────────────────────
    public int getOnlineCount() {
        return wsManager.getConnectionCount();
    }

    public boolean isServerRunning() {
        return wsManager.isConnected();
    }
}