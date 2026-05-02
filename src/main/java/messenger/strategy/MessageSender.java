package messenger.strategy;

public class MessageSender {

    private SendStrategy strategy;

    public void setStrategy(SendStrategy strategy) {
        this.strategy = strategy;
        System.out.println("[MessageSender] Стратегия установлена: "
                + strategy.getClass().getSimpleName());
    }

    public SendStrategy getStrategy() { return strategy; }

    public String send(SendContext context) {
        if (strategy == null) {
            throw new IllegalStateException("Стратегия не установлена");
        }
        return strategy.execute(context);
    }
}