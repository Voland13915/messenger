package messenger.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandInvoker {

    private final Deque<MessageCommand> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 50;

    public void invoke(MessageCommand command) {
        command.execute();
        history.push(command);
        if (history.size() > MAX_HISTORY) history.pollLast();

        System.out.println("[Invoker] Выполнено: " + command.describe());
    }

    public String getLastCommandDescription() {
        MessageCommand last = history.peek();
        return last != null ? last.describe() : "нет команд";
    }

    public int getHistorySize() {
        return history.size();
    }

    public void printHistory() {
        System.out.println("[Invoker] История команд (" + history.size() + "):");
        history.forEach(c -> System.out.println("  • " + c.describe()));
    }
}