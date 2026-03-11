package messenger.abstractfactory;

public interface UIFactory {
    MessageBubble createMessageBubble();
    SendButton createSendButton();
}