package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

public class ForwardCommand implements MessageCommand {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    private final String text;
    private final String originalSender;
    private final String fromChat;
    private final String toChat;
    private final String msgType;
    private final String filePath;
    private final String location;

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
        facade.forwardMessage(text, originalSender, fromChat, toChat);

        wsManager.send(toChat, "⤷ " + text, msgType, filePath, location);
    }

    @Override
    public String describe() {
        return "Forward [" + fromChat + " → " + toChat + "] [" + msgType + "]: " + text;
    }
}