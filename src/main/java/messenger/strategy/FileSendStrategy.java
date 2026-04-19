package messenger.strategy;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteStrategyB — алгоритм отправки файла (IMAGE / VIDEO).
 *
 * Реализует AlgorithmInterface() для файлов:
 *   Facade.sendFile() → Factory Method выбирает ImageMessage или VideoMessage
 *   wsManager.send() → доставка с filePath
 */
public class FileSendStrategy implements SendStrategy {

    private final MessengerFacade facade;

    public FileSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade = facade;
    }

    @Override
    public String execute(SendContext context) {
        // AlgorithmInterface(): только паттерны — Factory Method (IMAGE/VIDEO) + Builder
        facade.sendFile("Вы", context.getText(),
                context.getFilePath(), context.getFileType(), context.getQuote());
        System.out.println("[FileSendStrategy] Алгоритм: Factory Method ["
                + context.getFileType() + "] + Builder");
        return context.getText();
    }
}