package messenger.abstractfactory;

public class LightThemeFactory implements UIFactory {
    @Override
    public MessageBubble createMessageBubble() { return new LightMessageBubble(); }
    @Override
    public SendButton createSendButton() { return new LightSendButton(); }
}