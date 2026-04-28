package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

// Strategy (ConcreteStrategyB) — алгоритм отправки файла
public class FileSendStrategy implements SendStrategy {

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public FileSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    // AlgorithmInterface() — Factory Method выбирает IMAGE/VIDEO + Builder
    @Override
    public String execute(SendContext context) {
        facade.sendFile("Вы", context.getText(),
                context.getFilePath(), context.getFileType(), context.getQuote());
        System.out.println("[FileSendStrategy] AlgorithmInterface: Factory Method ["
                + context.getFileType() + "] + Builder → получатель: " + context.getRecipient());
        return context.getText();
    }
}