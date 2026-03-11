package messenger.factorymethod;

public abstract class MessageCreator {
    public abstract Message factoryMethod(String data);

    public void sendMessage(String data) {
        Message product = factoryMethod(data);
        product.send();
    }
}