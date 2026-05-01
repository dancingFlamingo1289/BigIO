package bigFiles.fileReaders;

import bigFiles.fileFactories.BigTabularFileFactory;

/**
 * Singleton reader specialized in loading tabular files assets.
 * <p>
 * It automatically links to the {@link TabularFileFactory} to handle 
 * instance creation and registry lookup.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigTabularFileReader extends BigFileReader {
	/** The unique instance of the singleton. */
	private static BigTabularFileReader instance;

	/**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigTabularFileReader() {
    	super(BigTabularFileFactory.getInstance());
    }

    /**
     * Returns the thread-safe singleton instance of this reader.
     * 
     * @return The singleton instance.
     */
    public static synchronized BigTabularFileReader getInstance() {
        if (instance == null) {
            instance = new BigTabularFileReader();
        }
        return instance;
    }
}
