package bigFiles.fileReaders;

import bigFiles.fileFactories.BigBinaryFileFactory;

/**
 * Singleton reader specialized in loading binary files assets.
 * <p>
 * It automatically links to the {@link BigBinaryFileFactory} to handle 
 * instance creation and registry lookup.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigBinaryFileReader extends BigFileReader {
	/** The unique instance of the singleton. */
	private static BigBinaryFileReader instance;

	/**
     * Private constructor to enforce the Singleton pattern.
     */
    private BigBinaryFileReader() {
    	super(BigBinaryFileFactory.getInstance());
    }

    public static synchronized BigBinaryFileReader getInstance() {
        if (instance == null) {
            instance = new BigBinaryFileReader();
        }
        return instance;
    }
}
