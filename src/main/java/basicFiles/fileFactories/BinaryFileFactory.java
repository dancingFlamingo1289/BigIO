package basicFiles.fileFactories;

import java.io.IOException;
import basicFiles.files.BaseFile;
import basicFiles.files.BinaryFile;

/**
 * Factory class specialized in the creation and management of {@link BinaryFile} instances.
 * This class implements the Singleton pattern to provide a centralized access point 
 * and utilizes a registry to ensure that a single instance is shared for any given file path.
 * 
 * @author Elias Kassas
 */
public class BinaryFileFactory implements FileFactory {
    
    /**
     * The unique instance of the singleton {@code BinaryFileFactory}.
     */
    private static BinaryFileFactory instance;

    /**
     * Private constructor to prevent external instantiation, enforcing the Singleton pattern.
     */
    private BinaryFileFactory() {}

    /**
     * Returns the unique instance of the factory. 
     * This method is synchronized to guarantee thread-safe initialization in multi-threaded environments.
     * 
     * @return The singleton instance of {@code BinaryFileFactory}.
     */
    public static synchronized BinaryFileFactory getInstance() {
        if (instance == null) instance = new BinaryFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves a {@link BinaryFile} instance associated with the specified path.
     * The method extracts the file name and extension from the provided path to initialize the object.
     * It delegates instance caching and sharing to the {@link FileRegistry}.
     * 
     * @param path The system path of the binary file to be created or retrieved.
     * @return A {@link BaseFile} (specifically a {@link BinaryFile}) corresponding to the path.
     * @throws RuntimeException If an {@link IOException} or {@link ClassNotFoundException} occurs 
     *                          during the binary file initialization within the registry's creation logic.[cite: 1]
     */
    @Override
    public BaseFile create(String path) {
        return FileRegistry.getSharedInstance(path, p -> {
            try {
                // Extracts the name by removing the extension and captures the extension separately
                return new BinaryFile(p.replace(p.substring(p.lastIndexOf('.')), ""), p.substring(p.lastIndexOf('.') + 1));
            } catch (IOException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            }
        });
    }
}