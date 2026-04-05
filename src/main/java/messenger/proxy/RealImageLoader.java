package messenger.proxy;

import javafx.scene.image.Image;
import java.io.File;

// RealSubject — реально загружает изображение
public class RealImageLoader implements ImageLoader {
    private final String filePath;
    private final Image image;

    public RealImageLoader(String filePath) {
        this.filePath = filePath;
        // Реальная загрузка происходит здесь
        System.out.println("  [RealImageLoader] Загружаю изображение: " + filePath);
        this.image = new Image(new File(filePath).toURI().toString(), 280, 200, true, true);
    }

    // RealSubject->Request()
    @Override public Image getImage()    { return image; }
    @Override public String getFilePath() { return filePath; }
}