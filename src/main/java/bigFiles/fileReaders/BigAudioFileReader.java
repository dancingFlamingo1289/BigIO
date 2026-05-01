package bigFiles.fileReaders;

import bigFiles.fileFactories.BigAudioFileFactory;

/**
 * Singleton reader specialized in loading AudioFile assets.
 * <p>
 * It automatically links to the {@link AudioFileFactory} to handle 
 * instance creation and registry lookup.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigAudioFileReader extends BigFileReader {
	/** The unique instance of the singleton. */
	private static BigAudioFileReader instance;

	/**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigAudioFileReader() {
    	super(BigAudioFileFactory.getInstance());
    }

    /**
     * Returns the thread-safe singleton instance of this reader.
     * 
     * @return The singleton instance.
     */
    public static synchronized BigAudioFileReader getInstance() {
        if (instance == null) {
            instance = new BigAudioFileReader();
        }
        return instance;
    }
}
