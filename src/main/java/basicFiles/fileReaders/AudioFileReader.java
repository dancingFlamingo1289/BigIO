package basicFiles.fileReaders;

import basicFiles.fileFactories.AudioFileFactory;

/**
 * Reader class specialized in handling audio files.
 * This class implements the Singleton pattern and utilizes the {@link AudioFileFactory} 
 * to create and manage {@link basicFiles.files.medias.Audio} file instances.
 * 
 * @author Elias Kassas
 */
public class AudioFileReader extends FileReader {
    /** The unique instance of the singleton {@code AudioFileReader}. */
	private static AudioFileReader instance;

    /**
     * Private constructor that initializes the reader by injecting the 
     * singleton instance of {@link AudioFileFactory} into the parent {@link FileReader}.
     */
    private AudioFileReader() {
    	super(AudioFileFactory.getInstance());
    }

    /**
     * Returns the unique instance of the reader.
     * This method is synchronized to ensure thread-safe initialization.
     * 
     * @return The singleton instance of {@code AudioFileReader}.
     */
    public static synchronized AudioFileReader getInstance() {
        if (instance == null) {
            instance = new AudioFileReader();
        }
        return instance;
    }
}