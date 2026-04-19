package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteStrategyC — алгоритм отправки геолокации.
 *
 * Реализует AlgorithmInterface() для геолокации:
 *   Facade.sendLocation() → Builder собирает сообщение с координатами
 *   wsManager.send() → доставка с location-полем
 */
public class LocationSendStrategy implements SendStrategy {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public LocationSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    @Override
    public String execute(SendContext context) {
        // Алгоритм C: Builder строит geo-сообщение → WebSocket с coordinates
        facade.sendLocation("Вы", context.getCoordinates(), context.getQuote());
        wsManager.send(context.getRecipient(), context.getText(),
                "TEXT", null, context.getCoordinates());
        System.out.println("[LocationSendStrategy] Геолокация отправлена: "
                + context.getCoordinates());
        return context.getText();
    }
}