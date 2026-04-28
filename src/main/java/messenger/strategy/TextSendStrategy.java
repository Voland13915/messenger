package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

// Strategy (ConcreteStrategyA) — алгоритм отправки текста
public class TextSendStrategy implements SendStrategy {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public TextSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    // AlgorithmInterface() — Factory Method + Builder через Facade
    // Реальную WebSocket-отправку делает Command
    @Override
    public String execute(SendContext context) {
        facade.sendText("Вы", context.getText(), context.getQuote());
        System.out.println("[TextSendStrategy] AlgorithmInterface: Factory Method [TEXT] + Builder"
                + " → получатель: " + context.getRecipient());
        return context.getText();
    }
}