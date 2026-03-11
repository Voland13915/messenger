package messenger.factorymethod;

public class VideoMessage implements Message {
    private final String videoUrl;

    public VideoMessage(String videoUrl) { this.videoUrl = videoUrl; }

    @Override
    public void send() { System.out.println("  [VideoMessage] Отправка видео: " + videoUrl); }

    @Override
    public String getType() { return "VIDEO"; }
}