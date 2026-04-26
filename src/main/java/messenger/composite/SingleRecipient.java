package messenger.composite;

public class SingleRecipient implements Recipient {
    private final String name;

    public SingleRecipient(String name) {
        this.name = name;
    }

    @Override
    public void send(String message) {
        System.out.println("  [SingleRecipient] → " + name + ": " + message);
    }

    @Override
    public String getName() { return name; }
}