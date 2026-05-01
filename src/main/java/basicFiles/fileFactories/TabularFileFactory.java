package basicFiles.fileFactories;

import java.io.IOException;
import basicFiles.files.BaseFile;
import basicFiles.files.TabularFile;

/**
 * Factory class specialized in the creation and management of {@link TabularFile} instances.
 * This class implements the Singleton pattern to provide a global access point and 
 * leverages a central registry to ensure that instances are shared for any unique file path.
 * 
 * @author Elias Kassas
 */
public class TabularFileFactory implements FileFactory {
    
    /**
     * The unique instance of the singleton {@code TabularFileFactory}.
     */
    private static TabularFileFactory instance;

    /**
     * Private constructor to prevent external instantiation, enforcing the Singleton pattern.
     */
    private TabularFileFactory() {}

    /**
     * Returns the unique instance of the factory. 
     * This method is synchronized to ensure thread-safe initialization in multi-threaded environments.
     * 
     * @return The singleton instance of {@code TabularFileFactory}.
     */
    public static synchronized TabularFileFactory getInstance() {
        if (instance == null) instance = new TabularFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves a {@link TabularFile} instance associated with the specified path.
     * The method delegates instance management to the {@link FileRegistry} to maintain 
     * a single object per physical file.
     * 
     * @param path The system path of the tabular file.
     * @return A {@link BaseFile} (specifically a {@link TabularFile} object) corresponding to the given path.
     * @throws RuntimeException If an {@link IOException} occurs during the tabular file initialization.
     */
    @Override
    public BaseFile create(String path) {
        return FileRegistry.getSharedInstance(path, p -> {
            try {
                return new TabularFile(p);
            } catch (IOException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            }
        });
    }
}