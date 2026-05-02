package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

public class SendFileCommand implements MessageCommand {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    private final String to;
    private final String caption;
    private final String filePath;
    private final String type;
    private final String quote;

    public SendFileCommand(MessengerFacade facade, WebSocketManager wsManager,
                           String to, String caption, String filePath,
                           String type, String quote) {
        this.facade    = facade;
        this.wsManager = wsManager;
        this.to        = to;
        this.caption   = caption;
        this.filePath  = filePath;
        this.type      = type;
        this.quote     = quote;
    }

    @Override
    public void execute() {
        facade.sendFile("Вы", caption, filePath, type, quote);
        wsManager.send(to, caption, type, filePath, null);
    }

    @Override
    public String describe() {
        return "SendFile [" + type + "] → [" + to + "]: " + caption;
    }
}