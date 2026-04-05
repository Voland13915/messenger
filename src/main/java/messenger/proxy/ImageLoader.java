package messenger.proxy;

import javafx.scene.image.Image;

// Subject — общий интерфейс для RealSubject и Proxy
public interface ImageLoader {
    Image getImage();
    String getFilePath();
}