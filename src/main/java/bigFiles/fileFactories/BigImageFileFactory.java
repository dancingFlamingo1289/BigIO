package bigFiles.fileFactories;

import java.io.IOException;

import bigFiles.files.BigFile;
import bigFiles.files.medias.BigImage;

/**
 * Factory class responsible for creating and managing {@link BigImage} instances.
 * <p>
 * This class implements the <b>Singleton</b> pattern and uses the {@link BigFileRegistry}
 * to ensure that each image file is only loaded once in memory. It automatically 
 * extracts the file name and format from the provided path.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigImageFileFactory implements BigFileFactory {
	/** The unique instance of the factory. */
	private static BigImageFileFactory instance;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigImageFileFactory() {}

    /**
     * Returns the thread-safe singleton instance of this factory.
     * 
     * @return The {@code BigImageFileFactory} instance.
     */
    public static synchronized BigImageFileFactory getInstance() {
        if (instance == null) instance = new BigImageFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves a {@link BigImage} instance for the specified path.
     * <p>
     * The path is parsed to separate the filename from the extension. If the instance 
     * does not exist in the registry, a new one is created.
     * </p>
     * 
     * @param path The full path to the image file.
     * @return A {@link BigFile} (specifically a {@link BigImage}) instance.
     * @throws RuntimeException If an {@link IOException} or {@link ClassNotFoundException} 
     *                          occurs during the creation process.
     */
    @Override
    public BigFile create(String path) {
        return BigFileRegistry.getSharedInstance(path, p -> {
            try {
				return new BigImage(p.replace(p.substring(p.lastIndexOf('.')), ""), p.substring(p.lastIndexOf('.') + 1));
			} catch (IOException e) {
				throw new RuntimeException("Error while opening or creating " + path, e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Error while opening or creating " + path, e);
			}
        });
    }
}