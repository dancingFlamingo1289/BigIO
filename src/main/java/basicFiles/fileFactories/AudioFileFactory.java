package basicFiles.fileFactories;

import basicFiles.files.BaseFile;
import basicFiles.files.medias.Audio;

/**
 * Factory class specialized in the creation and management of {@link Audio} file instances.
 * This class implements the Singleton pattern to provide a global point of access and 
 * leverages a registry to ensure instance sharing for the same file path.
 * @author Elias Kassas
 */
public class AudioFileFactory implements FileFactory {
	/**
     * The unique instance of the singleton {@code AudioFileFactory}.
     */
	private static AudioFileFactory instance;

	/**
     * Private constructor to prevent external instantiation, enforcing the Singleton pattern.
     */
    private AudioFileFactory() {}

    /**
     * Returns the unique instance of the factory. 
     * This method is synchronized to ensure thread-safe initialization in multi-threaded environments.
     * 
     * @return The singleton instance of {@code AudioFileFactory}.
     */
    public static synchronized AudioFileFactory getInstance() {
        if (instance == null) instance = new AudioFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves an {@link Audio} file instance associated with the specified path.
     * This method uses the internal {@link FileRegistry} to return a shared instance if the 
     * path has already been processed, or creates a new {@link Audio} object otherwise.
     * 
     * @param path The absolute or relative system path of the audio file.
     * @return A {@link BaseFile} (specifically an {@link Audio} object) corresponding to the given path.
     */
    @Override
    public BaseFile create(String path) {
        return FileRegistry.getSharedInstance(path, p -> {
            return new Audio(p);
        });
    }
}