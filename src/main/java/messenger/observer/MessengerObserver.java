package messenger.observer;

/**
 * Observer — интерфейс наблюдателя.
 * Определяет метод Update() который Subject вызывает при изменении состояния.
 */
public interface MessengerObserver {

    /**
     * Вызывается Subject-ом при любом изменении состояния.
     *
     * @param event  тип события: USER_JOINED, USER_LEFT, CONNECTION_CHANGED, MESSAGE_RECEIVED
     * @param data   данные события (имя пользователя, текст сообщения и т.д.)
     */
    void update(ObserverEvent event, Object data);
}