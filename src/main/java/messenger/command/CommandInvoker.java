package messenger.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Invoker (MenuItemаналог) — инициатор.
 *
 * По диаграмме GoF:
 *   - хранит объект ConcreteCommand
 *   - отправляет запрос, вызывая command.Execute()
 *
 * Дополнительно хранит историю выполненных команд —
 * это позволяет видеть лог действий и (в будущем) делать undo().
 */
public class CommandInvoker {

    // Invoker хранит команду (по диаграмме — одну, здесь — историю)
    private final Deque<MessageCommand> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 50;

    /**
     * Invoker отправляет запрос — вызывает command.Execute().
     * Сохраняет команду в историю.
     */
    public void invoke(MessageCommand command) {
        command.execute();             // command->Execute()
        history.push(command);
        if (history.size() > MAX_HISTORY) history.pollLast();

        System.out.println("[Invoker] Выполнено: " + command.describe());
    }

    /**
     * Вернуть описание последней команды (для отладки/отображения).
     */
    public String getLastCommandDescription() {
        MessageCommand last = history.peek();
        return last != null ? last.describe() : "нет команд";
    }

    /**
     * Количество команд в истории.
     */
    public int getHistorySize() {
        return history.size();
    }

    /**
     * Вывести всю историю команд в консоль (для демонстрации паттерна).
     */
    public void printHistory() {
        System.out.println("[Invoker] История команд (" + history.size() + "):");
        history.forEach(c -> System.out.println("  • " + c.describe()));
    }
}