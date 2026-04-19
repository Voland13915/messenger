package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteCommand — отправка геолокации.
 */
public class SendLocationCommand implements MessageCommand {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    private final String to;
    private final String coordinates;
    private final String quote;

    public SendLocationCommand(MessengerFacade facade, WebSocketManager wsManager,
                               String to, String coordinates, String quote) {
        this.facade      = facade;
        this.wsManager   = wsManager;
        this.to          = to;
        this.coordinates = coordinates;
        this.quote       = quote;
    }

    @Override
    public void execute() {
        facade.sendLocation("Вы", coordinates, quote);
        wsManager.send(to, "📍 " + coordinates, "TEXT", null, coordinates);
    }

    @Override
    public String describe() {
        return "SendLocation → [" + to + "]: " + coordinates;
    }
}