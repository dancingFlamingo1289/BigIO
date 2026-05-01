package basicFiles.fileFactories;

import java.io.IOException;
import basicFiles.files.BaseFile;
import basicFiles.files.TextFile;

/**
 * Factory class specialized in the creation and management of {@link TextFile} instances.
 * This class implements the Singleton pattern to provide a global access point and 
 * utilizes a centralized registry to ensure that a single instance is shared for any unique file path.
 * 
 * @author Elias Kassas
 */
public class TextFileFactory implements FileFactory {
	/**
	 * The unique instance of the singleton {@code TextFileFactory}.
	 */
	private static TextFileFactory instance;

	/**
	 * Private constructor to prevent external instantiation, enforcing the Singleton pattern.
	 */
	private TextFileFactory() {}

	/**
	 * Returns the unique instance of the factory. 
	 * This method is synchronized to ensure thread-safe initialization in multi-threaded environments.
	 * 
	 * @return The singleton instance of {@code TextFileFactory}.
	 */
	public static synchronized TextFileFactory getInstance() {
		if (instance == null) instance = new TextFileFactory();
		return instance;
	}

	/**
	 * Creates or retrieves a {@link TextFile} instance associated with the specified path.
	 * The method delegates instance management to the {@link FileRegistry} to maintain 
	 * a single object per physical file.
	 * 
	 * @param path The system path of the text file.
	 * @return A {@link BaseFile} (specifically a {@link TextFile} object) corresponding to the given path.
	 * @throws RuntimeException If an {@link IOException} occurs during the text file initialization.
	 */
	@Override
	public BaseFile create(String path) {
		return FileRegistry.getSharedInstance(path, p -> {
			try {
				return new TextFile(p);
			} catch (IOException e) {
				throw new RuntimeException("Error while opening or creating " + path, e);
			}
		});
	}
}