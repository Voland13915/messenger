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

    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    public FileSendStrategy(MessengerFacade facade, WebSocketManager wsManager) {
        this.facade    = facade;
        this.wsManager = wsManager;
    }

    @Override
    public String execute(SendContext context) {
        // Алгоритм B: Factory Method выбирает тип (IMAGE/VIDEO) → WebSocket
        facade.sendFile("Вы", context.getText(),
                context.getFilePath(), context.getFileType(), context.getQuote());
        wsManager.send(context.getRecipient(), context.getText(),
                context.getFileType(), context.getFilePath(), null);
        System.out.println("[FileSendStrategy] Файл [" + context.getFileType()
                + "] отправлен: " + context.getText());
        return context.getText();
    }
}