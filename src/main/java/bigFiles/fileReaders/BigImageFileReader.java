package bigFiles.fileReaders;

import bigFiles.fileFactories.BigImageFileFactory;

/**
 * Singleton reader specialized in loading image files assets.
 * <p>
 * It automatically links to the {@link ImageFileFactory} to handle 
 * instance creation and registry lookup.
 * </p>
 * 
 * @author Elias Kassas
 */
public class BigImageFileReader extends BigFileReader {
	/** The unique instance of the singleton. */
	private static BigImageFileReader instance;

	/**
     * Private constructor to enforce the Singleton pattern.
     */
	private BigImageFileReader() {
    	super(BigImageFileFactory.getInstance());
    }

    /**
     * Returns the thread-safe singleton instance of this reader.
     * 
     * @return The singleton instance.
     */
    public static synchronized BigImageFileReader getInstance() {
        if (instance == null) {
            instance = new BigImageFileReader();
        }
        return instance;
    }
}
