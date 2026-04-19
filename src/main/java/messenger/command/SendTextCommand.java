package messenger.command;

import messenger.facade.MessengerFacade;
import messenger.singleton.WebSocketManager;

/**
 * ConcreteCommand — отправка текстового сообщения.
 *
 * Состояние (по диаграмме GoF):
 *   receiver  = wsManager + facade
 *   state     = to, text, quote
 *
 * Execute() вызывает receiver->Action():
 *   facade.sendText()   — Builder + Factory Method (паттерны)
 *   wsManager.send()    — реальная отправка через WebSocket
 */
public class SendTextCommand implements MessageCommand {

    // receiver — получатели (знают как выполнить операцию)
    private final MessengerFacade  facade;
    private final WebSocketManager wsManager;

    // состояние команды
    private final String to;
    private final String text;
    private final String quote;   // может быть null

    public SendTextCommand(MessengerFacade facade, WebSocketManager wsManager,
                           String to, String text, String quote) {
        this.facade    = facade;
        this.wsManager = wsManager;
        this.to        = to;
        this.text      = text;
        this.quote     = quote;
    }

    /** Execute() → receiver->Action() */
    @Override
    public void execute() {
        facade.sendText("Вы", text, quote);                        // Facade: Builder + Factory Method
        wsManager.send(to, text, "TEXT", null, null, quote);       // WebSocket: с цитатой
    }

    @Override
    public String describe() {
        return "SendText → [" + to + "]: " + text;
    }
}