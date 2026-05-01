package bigFiles.fileReaders;

import bigFiles.fileFactories.BigTextFileFactory;

/**
 * Singleton reader specialized in loading text files assets.
 * <p>
 * It automatically links to the {@link TextFileFactory} to handle 
 * instance creation and registry lookup.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigTextFileReader extends BigFileReader {
	/** The unique instance of the singleton. */
	private static BigTextFileReader instance;

	/**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigTextFileReader() {
    	super(BigTextFileFactory.getInstance());
    }

    /**
     * Returns the thread-safe singleton instance of this reader.
     * 
     * @return The singleton instance.
     */
    public static synchronized BigTextFileReader getInstance() {
        if (instance == null) {
            instance = new BigTextFileReader();
        }
        return instance;
    }
}
