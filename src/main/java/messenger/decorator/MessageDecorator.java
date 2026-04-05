package messenger.decorator;

// Decorator — хранит ссылку на Component и делегирует ему
public abstract class MessageDecorator implements Message {
    // component — ссылка на оборачиваемый объект
    protected final Message component;

    public MessageDecorator(Message component) {
        this.component = component;
    }

    // component->Operation() — делегируем базовому объекту
    @Override public String getContent() { return component.getContent(); }
    @Override public String getSender()  { return component.getSender(); }
}