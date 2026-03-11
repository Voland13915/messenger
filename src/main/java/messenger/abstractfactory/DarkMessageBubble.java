package messenger.abstractfactory;

public class DarkMessageBubble implements MessageBubble {
    @Override
    public void render() {
        System.out.println("  [DarkMessageBubble] Тёмный пузырёк сообщения");
    }
}