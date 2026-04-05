package messenger.composite;

import java.util.ArrayList;
import java.util.List;

// Composite — группа получателей, хранит потомков
public class RecipientGroup implements Recipient {
    private final String name;
    // потомки
    private final List<Recipient> members = new ArrayList<>();

    public RecipientGroup(String name) {
        this.name = name;
    }

    // Add(Component)
    public void add(Recipient recipient) {
        members.add(recipient);
    }

    // Remove(Component)
    public void remove(Recipient recipient) {
        members.remove(recipient);
    }

    // GetChild(int)
    public Recipient getChild(int index) {
        return members.get(index);
    }

    public List<Recipient> getMembers() {
        return members;
    }

    // Operation() — для всех потомков: g.Operation()
    @Override
    public void send(String message) {
        System.out.println("  [RecipientGroup] Рассылка в группу «" + name + "»:");
        for (Recipient member : members) {
            member.send(message); // рекурсивно — если потомок тоже Composite
        }
    }

    @Override
    public String getName() { return name; }
}