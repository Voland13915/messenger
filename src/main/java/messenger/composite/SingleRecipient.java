package messenger.composite;

// Leaf — один пользователь, не имеет потомков
public class SingleRecipient implements Recipient {
    private final String name;

    public SingleRecipient(String name) {
        this.name = name;
    }

    // Operation() — листовой узел обрабатывает сам
    @Override
    public void send(String message) {
        System.out.println("  [SingleRecipient] → " + name + ": " + message);
    }

    @Override
    public String getName() { return name; }
}