package messenger.proxy;

import javafx.scene.image.Image;

public class ImageLoaderProxy implements ImageLoader {

    private final String filePath;
    private RealImageLoader realSubject = null;

    private static final Image PLACEHOLDER = createPlaceholder();

    public ImageLoaderProxy(String filePath) {
        this.filePath = filePath;
        System.out.println("  [ImageLoaderProxy] Создан прокси для: " + filePath);
    }

    @Override
    public Image getImage() {
        if (realSubject == null) {
            realSubject = new RealImageLoader(filePath);
        }
        return realSubject.getImage();
    }

    @Override
    public String getFilePath() { return filePath; }

    public boolean isLoaded() { return realSubject != null; }

    public Image getPlaceholder() { return PLACEHOLDER; }

    private static Image createPlaceholder() {
        return new Image(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mN8/+F9PQAI8wNPvd7POQAAAABJRU5ErkJggg==",
                280, 200, true, true
        );
    }
}