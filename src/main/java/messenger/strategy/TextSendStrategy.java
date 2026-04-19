package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteStrategyA — алгоритм отправки текстового сообщения.
 *
 * Реализует AlgorithmInterface() для текста:
 *   Facade.sendText() → Factory Method создаёт TextMessage
 *   wsManager.send() → доставка через WebSocket
 */
public class TextSendStrategy implements SendStrategy {

    private final MessengerFacade facade;

    public TextSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade = facade;
        // wsManager не используется в стратегии — отправка в Command
    }

    @Override
    public String execute(SendContext context) {
        // AlgorithmInterface(): только паттерны — Factory Method + Builder через Facade
        // Реальную отправку делает Command (wsManager.send вызывается там)
        facade.sendText("Вы", context.getText(), context.getQuote());
        System.out.println("[TextSendStrategy] Алгоритм: Factory Method + Builder");
        return context.getText();
    }
}