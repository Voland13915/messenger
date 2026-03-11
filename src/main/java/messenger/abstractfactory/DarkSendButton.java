package messenger.abstractfactory;

public class DarkSendButton implements SendButton {
    @Override
    public void render() {
        System.out.println("  [DarkSendButton] Тёмная кнопка отправки");
    }
}