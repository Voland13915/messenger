package messenger.proxy;

import javafx.scene.image.Image;

// Proxy — виртуальный заместитель
// Откладывает создание RealImageLoader до первого реального запроса
public class ImageLoaderProxy implements ImageLoader {

    private final String filePath;
    // realSubject — ссылка на реальный объект (изначально null)
    private RealImageLoader realSubject = null;

    // Заглушка — показывается пока реальное изображение не загружено
    private static final Image PLACEHOLDER = createPlaceholder();

    public ImageLoaderProxy(String filePath) {
        this.filePath = filePath;
        System.out.println("  [ImageLoaderProxy] Создан прокси для: " + filePath);
    }

    // Request() — при первом вызове создаём RealSubject и кэшируем
    @Override
    public Image getImage() {
        if (realSubject == null) {
            // Только сейчас создаём реальный объект
            realSubject = new RealImageLoader(filePath);
        }
        // realSubject->Request()
        return realSubject.getImage();
    }

    @Override
    public String getFilePath() { return filePath; }

    // Можно проверить загружено ли уже
    public boolean isLoaded() { return realSubject != null; }

    public Image getPlaceholder() { return PLACEHOLDER; }

    private static Image createPlaceholder() {
        // Простая серая заглушка через data URI
        return new Image(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mN8/+F9PQAI8wNPvd7POQAAAABJRU5ErkJggg==",
                280, 200, true, true
        );
    }
}