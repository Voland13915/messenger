package messenger.factorymethod;

public class TextMessage implements Message {
    private final String content;

    public TextMessage(String content) { this.content = content; }

    @Override
    public void send() { System.out.println("  [TextMessage] Отправка текста: " + content); }

    @Override
    public String getType() { return "TEXT"; }
}