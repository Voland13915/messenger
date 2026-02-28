package messenger.prototype;

/**
 * ConcretePrototype2 — сообщение с файлом.
 * Реализует клонирование: при пересылке файловое сообщение копируется целиком.
 */
public class FileMessage implements ForwardableMessage {

    private String originalSender;
    private String fileName;
    private long fileSizeBytes;
    private String forwardedBy;

    public FileMessage(String originalSender, String fileName, long fileSizeBytes) {
        this.originalSender = originalSender;
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
    }

    /**
     * Clone() — возвращает копию сообщения с файлом.
     */
    @Override
    public FileMessage clone() {
        try {
            return (FileMessage) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Клонирование не поддерживается", e);
        }
    }

    @Override
    public void setForwardedBy(String user) {
        this.forwardedBy = user;
    }

    @Override
    public void display() {
        System.out.println("  [FileMessage] " + originalSender + " → файл: " + fileName +
                " (" + (fileSizeBytes / 1024) + " КБ)");
        if (forwardedBy != null) {
            System.out.println("    ↩ Переслал: " + forwardedBy);
        }
    }
}
