package messenger.command;

/**
 * Command — интерфейс команды.
 * Объявляет операцию Execute().
 */
public interface MessageCommand {

    /** Execute() — выполнить команду */
    void execute();

    /**
     * Описание команды для лога/отладки.
     * ConcreteCommand хранит состояние — возвращаем его здесь.
     */
    String describe();
}