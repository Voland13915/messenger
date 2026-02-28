package messenger.factorymethod;

/**
 * Creator — абстрактный создатель сообщений.
 * Объявляет фабричный метод createMessage(), возвращающий объект типа Message.
 * Может также определять реализацию по умолчанию фабричного метода.
 */
public abstract class MessageCreator {

    // Фабричный метод — подклассы переопределяют его для создания конкретных продуктов
    public abstract Message createMessage(String sender, String content);

    // Операция, использующая фабричный метод
    public void sendMessage(String sender, String content) {
        Message msg = createMessage(sender, content);
        System.out.print("  Отправка: ");
        msg.display();
    }
}
