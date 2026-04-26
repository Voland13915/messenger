package messenger.proxy;

import javafx.scene.image.Image;
import java.io.File;

public class RealImageLoader implements ImageLoader {
    private final String filePath;
    private final Image image;

    public RealImageLoader(String filePath) {
        this.filePath = filePath;
        System.out.println("  [RealImageLoader] Загружаю изображение: " + filePath);
        this.image = new Image(new File(filePath).toURI().toString(), 280, 200, true, true);
    }

    @Override public Image getImage()    { return image; }
    @Override public String getFilePath() { return filePath; }
}