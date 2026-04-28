package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

// Strategy (ConcreteStrategyC) — алгоритм отправки геолокации
public class LocationSendStrategy implements SendStrategy {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public LocationSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    // AlgorithmInterface() — Builder с координатами
    @Override
    public String execute(SendContext context) {
        facade.sendLocation("Вы", context.getCoordinates(), context.getQuote());
        System.out.println("[LocationSendStrategy] AlgorithmInterface: Builder [geo]"
                + " → получатель: " + context.getRecipient());
        return context.getText();
    }
}