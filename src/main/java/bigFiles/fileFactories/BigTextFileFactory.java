package bigFiles.fileFactories;

import java.io.IOException;

import bigFiles.files.BigFile;
import bigFiles.files.BigTextFile;

/**
 * Factory class responsible for creating and managing {@link BigTextFile} instances.
 * <p>
 * This class provides a singleton access point for handling large text documents. 
 * By using the {@link BigFileRegistry}, it prevents redundant memory consumption 
 * when multiple components need to read the same text source.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigTextFileFactory implements BigFileFactory {
	/** The unique instance of the factory. */
    private static BigTextFileFactory instance;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigTextFileFactory() {}

    /**
     * Returns the thread-safe singleton instance of this factory.
     * 
     * @return The {@code BigTextFileFactory} instance.
     */
    public static synchronized BigTextFileFactory getInstance() {
        if (instance == null) instance = new BigTextFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves a {@link BigTextFile} instance for the specified path.
     * 
     * @param path The full path to the text file.
     * @return A {@link BigFile} (specifically a {@link BigTextFile}) instance.
     * @throws RuntimeException If an {@link IOException} occurs during instantiation.
     */
    @Override
    public BigFile create(String path) {
        return BigFileRegistry.getSharedInstance(path, p -> {
            try {
				return new BigTextFile(p);
			} catch (IOException e) {
				throw new RuntimeException("Error while opening or creating " + path, e);
			}
        });
    }
}