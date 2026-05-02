package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

public class TextSendStrategy implements SendStrategy {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public TextSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    @Override
    public String execute(SendContext context) {
        facade.sendText("Вы", context.getText(), context.getQuote());
        System.out.println("[TextSendStrategy] AlgorithmInterface: Factory Method [TEXT] + Builder"
                + " → получатель: " + context.getRecipient());
        return context.getText();
    }
}