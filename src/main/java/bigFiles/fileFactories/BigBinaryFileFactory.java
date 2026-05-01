package bigFiles.fileFactories;

import java.io.IOException;
import bigFiles.files.BigBinaryFile;
import bigFiles.files.BigFile;

/**
 * Factory class responsible for creating and managing {@link BigBinaryFile} instances.
 * <p>
 * This class implements the <b>Singleton</b> pattern and integrates with the 
 * {@link BigFileRegistry} to ensure that each physical binary file is represented 
 * by a single instance in memory.
 * </p>
 *
 * @author Elias Kassas
 */
public class BigBinaryFileFactory implements BigFileFactory {
    /** The unique instance of the factory. */
    private static BigBinaryFileFactory instance;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigBinaryFileFactory() {}

    /**
     * Returns the thread-safe singleton instance of this factory.
     * 
     * @return The {@code BigBinaryFileFactory} instance.
     */
    public static synchronized BigBinaryFileFactory getInstance() {
        if (instance == null) instance = new BigBinaryFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves a {@link BigBinaryFile} instance for the specified path.
     * <p>
     * This method parses the provided path to separate the filename from its extension.
     * It then uses the {@link BigFileRegistry} to provide a shared instance. 
     * If instantiation fails due to I/O or class errors, a {@link RuntimeException} is thrown.
     * </p>
     * 
     * @param path The full filesystem path of the binary file.
     * @return A {@link BigFile} (specifically a {@link BigBinaryFile}) instance.
     * @throws RuntimeException If an {@link IOException} or {@link ClassNotFoundException} occurs during creation.
     */
    @Override
    public BigFile create(String path) {
        return BigFileRegistry.getSharedInstance(path, p -> {
            try {
                // Extract filename without extension and the extension itself
                String name = p.replace(p.substring(p.lastIndexOf('.')), "");
                String extension = p.substring(p.lastIndexOf('.') + 1);
                
                return new BigBinaryFile(name, extension);
            } catch (IOException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error while opening or creating " + path, e);
            }
        });
    }
}