package messenger.abstractfactory;

// AbstractProductA
interface MessageBubble {
    void render();
}

// AbstractProductB
interface SendButton {
    void render();
}

// ProductA1
class LightMessageBubble implements MessageBubble {
    @Override
    public void render() {
        System.out.println("  [LightMessageBubble] Белый пузырёк сообщения");
    }
}

// ProductA2
class DarkMessageBubble implements MessageBubble {
    @Override
    public void render() {
        System.out.println("  [DarkMessageBubble] Тёмный пузырёк сообщения");
    }
}

// ProductB1
class LightSendButton implements SendButton {
    @Override
    public void render() {
        System.out.println("  [LightSendButton] Светлая кнопка отправки");
    }
}

// ProductB2
class DarkSendButton implements SendButton {
    @Override
    public void render() {
        System.out.println("  [DarkSendButton] Тёмная кнопка отправки");
    }
}

// AbstractFactory
interface UIFactory {
    /* CreateProductA() */
    MessageBubble createMessageBubble();

    /* CreateProductB() */
    SendButton createSendButton();
}

// ConcreteFactory1
class LightThemeFactory implements UIFactory {
    /* CreateProductA() */
    @Override
    public MessageBubble createMessageBubble() {
        return new LightMessageBubble();
    }

    /* CreateProductB() */
    @Override
    public SendButton createSendButton() {
        return new LightSendButton();
    }
}

// ConcreteFactory2
class DarkThemeFactory implements UIFactory {
    /* CreateProductA() */
    @Override
    public MessageBubble createMessageBubble() {
        return new DarkMessageBubble();
    }

    /* CreateProductB() */
    @Override
    public SendButton createSendButton() {
        return new DarkSendButton();
    }
}

// Client
class MessengerUI {
    private final MessageBubble messageBubble;
    private final SendButton sendButton;

    /* Client получает фабрику извне и не знает о конкретных классах */
    public MessengerUI(UIFactory factory) {
        this.messageBubble = factory.createMessageBubble();
        this.sendButton    = factory.createSendButton();
    }

    public void renderUI() {
        messageBubble.render();
        sendButton.render();
    }
}

// Демонстрация
class AbstarctFactoryDemo {
    public static void main(String[] args) {
        System.out.println("=== Abstract Factory: UIFactory ===");

        System.out.println("  -- Светлая тема --");
        MessengerUI lightUI = new MessengerUI(new LightThemeFactory());
        lightUI.renderUI();

        System.out.println("  -- Тёмная тема --");
        MessengerUI darkUI = new MessengerUI(new DarkThemeFactory());
        darkUI.renderUI();
    }
}