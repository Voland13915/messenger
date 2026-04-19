package messenger.observer;

/**
 * Типы событий которые Subject может отправлять наблюдателям.
 */
public enum ObserverEvent {
    USER_JOINED,         // новый пользователь вошёл в сеть
    USER_LEFT,           // пользователь отключился
    CONNECTION_CHANGED,  // статус соединения изменился (true = подключён, false = нет)
    MESSAGE_RECEIVED     // получено новое сообщение от сервера
}