package bigFiles.fileFactories;

import bigFiles.files.BigFile;
import bigFiles.files.medias.BigAudio;

/**
 * Factory class responsible for creating and managing {@link BigAudio} instances.
 * <p>
 * This class implements the <b>Singleton</b> pattern to provide a global point of access
 * and ensures that audio files are handled through the {@link BigFileRegistry} to
 * maintain instance uniqueness and shared access across the application.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigAudioFileFactory implements BigFileFactory {
    /** The unique instance of the factory. */
    private static BigAudioFileFactory instance;

    /** Private constructor to prevent external instantiation. */
    private BigAudioFileFactory() {}

    /**
     * Returns the thread-safe singleton instance of this factory.
     * 
     * @return The {@code BigAudioFileFactory} instance.
     */
    public static synchronized BigAudioFileFactory getInstance() {
        if (instance == null) instance = new BigAudioFileFactory();
        return instance;
    }

    /**
     * Creates or retrieves a {@link BigAudio} instance for the specified path.
     * <p>
     * This method utilizes the {@link BigFileRegistry} to check if an instance for 
     * the given path already exists. If not, it uses a lambda expression to 
     * instantiate a new {@link BigAudio} object.
     * </p>
     * 
     * @param path The filesystem path of the audio file.
     * @return A {@link BigFile} (specifically a {@link BigAudio}) instance.
     */
    @Override
    public BigFile create(String path) {
        return BigFileRegistry.getSharedInstance(path, p -> {
            return new BigAudio(p);
        });
    }
}