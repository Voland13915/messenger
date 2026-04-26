package messenger.facade;

import messenger.builder.*;
import messenger.factorymethod.*;
import messenger.prototype.*;
import messenger.singleton.WebSocketManager;

public class MessengerFacade {

    private final WebSocketManager wsManager = WebSocketManager.getInstance();

    public void sendText(String sender, String text, String quote) {
        new TextMessageCreator().factoryMethod(text);

        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, text, quote, null, null);
    }

    public void sendFile(String sender, String caption,
                         String filePath, String type, String quote) {
        MessageCreator creator = type.equals("IMAGE")
                ? new ImageMessageCreator()
                : new VideoMessageCreator();
        creator.factoryMethod(filePath);

        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(sender, caption, quote, filePath, null);
    }

    public void sendLocation(String sender, String coordinates, String quote) {
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(
                sender, "📍 " + coordinates, quote, null, coordinates);
    }

    public messenger.prototype.Message forwardMessage(String text, String sender,
                                                      String fromChat, String toChat) {
        messenger.prototype.TextMessage proto =
                new messenger.prototype.TextMessage(text, sender, fromChat);
        return new MessageForwarder(proto).forward(toChat);
    }

    public int getOnlineCount() {
        return wsManager.getConnectionCount();
    }

    public boolean isServerRunning() {
        return wsManager.isConnected();
    }
}