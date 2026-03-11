package messenger.abstractfactory;

public class LightMessageBubble implements MessageBubble {
    @Override
    public void render() {
        System.out.println("  [LightMessageBubble] Белый пузырёк сообщения");
    }
}