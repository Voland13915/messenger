package messenger.state;

/**
 * State — интерфейс состояния (аналог TCPState из GoF).
 *
 * Определяет интерфейс для инкапсуляции поведения,
 * ассоциированного с конкретным состоянием контекста MessageContext.
 *
 * Handle() — обработать запрос от Context.
 * Каждый ConcreteState реализует поведение своего состояния.
 */
public interface MessageState {

    /**
     * Handle() — выполнить действие текущего состояния.
     *
     * Context делегирует запрос текущему состоянию:
     *   state->Handle()
     *
     * @param context контекст — можно сменить состояние через context.setState()
     */
    void handle(MessageContext context);

    /**
     * Человекочитаемое название состояния — для UI (галочки, иконки).
     */
    String getDisplayName();

    /**
     * Символ для отображения в пузырьке сообщения.
     * Аналог галочек в Telegram: отправка → ✓ → ✓✓ → ✓✓ (синие)
     */
    String getStatusIcon();
}