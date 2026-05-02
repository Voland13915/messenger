package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

public class SendTextCommand implements MessageCommand {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    private final String to;
    private final String text;
    private final String quote;
    public SendTextCommand(MessengerFacade facade, WebSocketManager wsManager,
                           String to, String text, String quote) {
        this.facade    = facade;
        this.wsManager = wsManager;
        this.to        = to;
        this.text      = text;
        this.quote     = quote;
    }

    /** Execute() → receiver->Action() */
    @Override
    public void execute() {
        facade.sendText("Вы", text, quote);
        wsManager.send(to, text, "TEXT", null, null, quote);
    }

    @Override
    public String describe() {
        return "SendText → [" + to + "]: " + text;
    }
}