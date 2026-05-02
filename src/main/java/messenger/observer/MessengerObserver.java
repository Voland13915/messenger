package messenger.observer;

public interface MessengerObserver {
    void update(ObserverEvent event, Object data);
}