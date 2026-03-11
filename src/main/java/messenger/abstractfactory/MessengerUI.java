package messenger.abstractfactory;

public class MessengerUI {
    private final MessageBubble messageBubble;
    private final SendButton sendButton;

    public MessengerUI(UIFactory factory) {
        this.messageBubble = factory.createMessageBubble();
        this.sendButton    = factory.createSendButton();
    }

    public void renderUI() {
        messageBubble.render();
        sendButton.render();
    }
}