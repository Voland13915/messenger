package messenger.abstractfactory;

public class DarkThemeFactory implements UIFactory {
    @Override
    public MessageBubble createMessageBubble() { return new DarkMessageBubble(); }
    @Override
    public SendButton createSendButton() { return new DarkSendButton(); }
}