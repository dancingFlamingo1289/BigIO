package bigFiles.fileFactories;

import java.io.IOException;

import bigFiles.files.BigFile;
import bigFiles.files.BigTabularFile;

/**
 * Factory class responsible for creating and managing {@link BigTabularFile} instances.
 * <p>
 * This factory is optimized for structured data files (like CSV or spreadsheets). 
 * Like other big file factories, it ensures thread-safe access and instance 
 * sharing through a centralized registry.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigTabularFileFactory implements BigFileFactory {
	/** The unique instance of the factory. */
	private static BigTabularFileFactory instance;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigTabularFileFactory() {}

    /**
     * Returns the thread-safe singleton instance of this factory.
     * 
     * @return The {@code BigTabularFileFactory} instance.
     */
    public static synchronized BigTabularFileFactory getInstance() {
        if (instance == null) instance = new BigTabularFileFactory();
        return instance;
    }

    @Override
    public BigFile create(String path) {
        return BigFileRegistry.getSharedInstance(path, p -> {
            try {
				return new BigTabularFile(p);
			} catch (IOException e) {
				throw new RuntimeException("Error while opening or creating " + path, e);
			}
        });
    }
}