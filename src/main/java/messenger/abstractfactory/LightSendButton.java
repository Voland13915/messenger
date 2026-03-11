package messenger.abstractfactory;

public class LightSendButton implements SendButton {
    @Override
    public void render() {
        System.out.println("  [LightSendButton] Светлая кнопка отправки");
    }
}