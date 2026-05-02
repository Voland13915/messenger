package messenger.strategy;

public interface SendStrategy {
    String execute(SendContext context);
}