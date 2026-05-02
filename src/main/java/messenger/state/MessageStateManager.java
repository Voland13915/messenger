package messenger.state;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MessageStateManager {

    private final Map<String, MessageContext> contexts = new LinkedHashMap<>();

    public MessageContext createContext(String text, String recipient) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        MessageContext ctx = new MessageContext(id, text, recipient);
        contexts.put(id, ctx);
        ctx.request();
        return ctx;
    }

    public void markDelivered(String messageId) {
        MessageContext ctx = contexts.get(messageId);
        if (ctx != null) ctx.setState(new DeliveredState());
    }

    public void markRead(String messageId) {
        MessageContext ctx = contexts.get(messageId);
        if (ctx != null) {
            ctx.setState(new ReadState());
            ctx.request();
        }
    }

    public void markFailed(String messageId, String reason) {
        MessageContext ctx = contexts.get(messageId);
        if (ctx != null) ctx.setState(new FailedState(reason));
    }

    public String getStatusIcon(String messageId) {
        MessageContext ctx = contexts.get(messageId);
        return ctx != null ? ctx.getStatusIcon() : "";
    }

    public MessageContext getContext(String messageId) {
        return contexts.get(messageId);
    }
}