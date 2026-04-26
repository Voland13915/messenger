package messenger.composite;

import java.util.ArrayList;
import java.util.List;

public class RecipientGroup implements Recipient {
    private final String name;
    private final List<Recipient> members = new ArrayList<>();

    public RecipientGroup(String name) {
        this.name = name;
    }

    public void add(Recipient recipient) {
        members.add(recipient);
    }

    public void remove(Recipient recipient) {
        members.remove(recipient);
    }

    public Recipient getChild(int index) {
        return members.get(index);
    }

    public List<Recipient> getMembers() {
        return members;
    }

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