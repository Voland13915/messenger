package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteCommand — пересылка сообщения (Prototype внутри Facade).
 * Передаёт полный тип: TEXT, IMAGE, VIDEO, а также filePath и location.
 */
public class ForwardCommand implements MessageCommand {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    private final String text;
    private final String originalSender;
    private final String fromChat;
    private final String toChat;
    private final String msgType;    // "TEXT", "IMAGE", "VIDEO"
    private final String filePath;   // null для текста/геолокации
    private final String location;   // null для текста/файлов

    public ForwardCommand(MessengerFacade facade, WebSocketManager wsManager,
                          String text, String originalSender,
                          String fromChat, String toChat,
                          String msgType, String filePath, String location) {
        this.facade         = facade;
        this.wsManager      = wsManager;
        this.text           = text;
        this.originalSender = originalSender;
        this.fromChat       = fromChat;
        this.toChat         = toChat;
        this.msgType        = msgType   != null ? msgType   : "TEXT";
        this.filePath       = filePath;
        this.location       = location;
    }

    @Override
    public void execute() {
        // Prototype через Facade: клонирует сообщение и меняет chatId
        facade.forwardMessage(text, originalSender, fromChat, toChat);

        // Реальная отправка через WebSocket с полным типом
        wsManager.send(toChat, "⤷ " + text, msgType, filePath, location);
    }

    @Override
    public String describe() {
        return "Forward [" + fromChat + " → " + toChat + "] [" + msgType + "]: " + text;
    }
}