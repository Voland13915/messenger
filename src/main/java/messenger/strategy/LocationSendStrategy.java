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

    private final MessengerFacade facade;

    public LocationSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade = facade;
    }

    @Override
    public String execute(SendContext context) {
        // AlgorithmInterface(): только паттерны — Builder с координатами
        facade.sendLocation("Вы", context.getCoordinates(), context.getQuote());
        System.out.println("[LocationSendStrategy] Алгоритм: Builder [geo]");
        return context.getText();
    }
}