package basicFiles.fileFactories;

import java.io.IOException;
import basicFiles.files.BaseFile;
import basicFiles.files.medias.Image;

/**
 * Factory class specialized in the creation and management of {@link Image} file instances.
 * This class implements the Singleton pattern to provide a global access point and 
 * utilizes a registry to ensure that only one instance of an image is shared for a specific file path.
 * 
 * @author Elias Kassas
 */
public class ImageFileFactory implements FileFactory {
    /**
     * The unique instance of the singleton {@code ImageFileFactory}.
     */
    private static ImageFileFactory instance;

    /**
     * Private constructor to prevent external instantiation, enforcing the Singleton pattern.
     */
    private ImageFileFactory() {}

    /**
     * Returns the unique instance of the factory. 
     * This method is synchronized to ensure thread-safe initialization.
     * 
     * @return The singleton instance of {@code ImageFileFactory}.
     */
    public static synchronized ImageFileFactory getInstance() {
        if (instance == null) instance = new ImageFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves an {@link Image} instance associated with the specified path.
     * The method parses the path to separate the file name from its extension and 
     * delegates the instance management to the {@link FileRegistry}.
     * 
     * @param path The system path of the image file.
     * @return A {@link BaseFile} (specifically an {@link Image} object) corresponding to the given path.[cite: 1, 2]
     * @throws RuntimeException If an {@link IOException} or {@link ClassNotFoundException} occurs 
     *                          during the image initialization process.
     */
    @Override
    public BaseFile create(String path) {
        return FileRegistry.getSharedInstance(path, p -> {
            try {
                // Extracts the base name and the extension from the full path
                return new Image(p.replace(p.substring(p.lastIndexOf('.')), ""), p.substring(p.lastIndexOf('.') + 1));
            } catch (IOException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            }
        });
    }
}