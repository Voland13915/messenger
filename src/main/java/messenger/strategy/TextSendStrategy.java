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

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public TextSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    @Override
    public String execute(SendContext context) {
        // Алгоритм A: Facade (Builder + Factory Method) → WebSocket
        facade.sendText("Вы", context.getText(), context.getQuote());
        wsManager.send(context.getRecipient(), context.getText());
        System.out.println("[TextSendStrategy] Текст отправлен: " + context.getText());
        return context.getText();
    }
}