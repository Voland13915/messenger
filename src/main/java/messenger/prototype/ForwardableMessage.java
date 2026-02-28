package messenger.prototype;

/**
 * Prototype — интерфейс клонируемого сообщения.
 * Объявляет интерфейс для клонирования самого себя.
 */
public interface ForwardableMessage extends Cloneable {
    ForwardableMessage clone();
    void setForwardedBy(String user);
    void display();
}
