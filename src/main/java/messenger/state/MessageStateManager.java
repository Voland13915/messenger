package messenger.state;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Менеджер состояний сообщений.
 *
 * Хранит MessageContext для каждого отправленного сообщения.
 * MessengerWindow обращается сюда чтобы:
 *   - создать контекст при отправке
 *   - получить иконку состояния для UI
 *   - продвинуть состояние (delivered → read)
 */
public class MessageStateManager {

    // messageId → Context
    private final Map<String, MessageContext> contexts = new LinkedHashMap<>();

    /**
     * Создать контекст для нового исходящего сообщения.
     * Начальное состояние — SendingState.
     * Вызывается сразу после отправки.
     */
    public MessageContext createContext(String text, String recipient) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        MessageContext ctx = new MessageContext(id, text, recipient);
        contexts.put(id, ctx);
        // Request() → state->Handle() → SendingState → DeliveredState
        ctx.request();
        return ctx;
    }

    /**
     * Перевести сообщение в состояние «Доставлено».
     * Вызывается когда сервер подтвердил получение.
     */
    public void markDelivered(String messageId) {
        MessageContext ctx = contexts.get(messageId);
        if (ctx != null) ctx.setState(new DeliveredState());
    }

    /**
     * Перевести сообщение в состояние «Прочитано».
     * Вызывается когда получатель открыл чат (Observer уведомит).
     */
    public void markRead(String messageId) {
        MessageContext ctx = contexts.get(messageId);
        if (ctx != null) {
            ctx.setState(new ReadState());
            ctx.request(); // Handle() для ReadState
        }
    }

    /**
     * Перевести в состояние «Ошибка».
     */
    public void markFailed(String messageId, String reason) {
        MessageContext ctx = contexts.get(messageId);
        if (ctx != null) ctx.setState(new FailedState(reason));
    }

    /**
     * Получить иконку состояния для отображения в пузырьке.
     */
    public String getStatusIcon(String messageId) {
        MessageContext ctx = contexts.get(messageId);
        return ctx != null ? ctx.getStatusIcon() : "";
    }

    public MessageContext getContext(String messageId) {
        return contexts.get(messageId);
    }
}