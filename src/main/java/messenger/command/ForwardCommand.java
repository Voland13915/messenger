package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteCommand — пересылка сообщения (Prototype внутри Facade).
 */
public class ForwardCommand implements MessageCommand {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    private final String text;
    private final String originalSender;
    private final String fromChat;
    private final String toChat;

    public ForwardCommand(MessengerFacade facade, WebSocketManager wsManager,
                          String text, String originalSender,
                          String fromChat, String toChat) {
        this.facade         = facade;
        this.wsManager      = wsManager;
        this.text           = text;
        this.originalSender = originalSender;
        this.fromChat       = fromChat;
        this.toChat         = toChat;
    }

    @Override
    public void execute() {
        // Facade → Prototype: клонирует сообщение и меняет chatId
        messenger.prototype.Message fwd =
                facade.forwardMessage(text, originalSender, fromChat, toChat);
        wsManager.send(toChat, "⤷ " + fwd.getContent());
    }

    @Override
    public String describe() {
        return "Forward [" + fromChat + " → " + toChat + "]: " + text;
    }
}