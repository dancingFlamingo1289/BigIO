package basicFiles.fileReaders;

import basicFiles.fileFactories.BinaryFileFactory;

/**
 * Reader class specialized in handling binary files.
 * This class implements the Singleton pattern and utilizes the {@link BinaryFileFactory} 
 * to create and manage {@link basicFiles.files.BinaryFile} instances.
 * 
 * @author Elias Kassas
 */
public class BinaryFileReader extends FileReader {
    
    /**
     * The unique instance of the singleton {@code BinaryFileReader}.
     */
	private static BinaryFileReader instance;

    /**
     * Private constructor that initializes the reader by injecting the 
     * singleton instance of {@link BinaryFileFactory} into the parent {@link FileReader}.
     */
    private BinaryFileReader() {
    	super(BinaryFileFactory.getInstance());
    }

    /**
     * Returns the unique instance of the reader.
     * This method is synchronized to ensure thread-safe initialization.
     * 
     * @return The singleton instance of {@code BinaryFileReader}.
     */
    public static synchronized BinaryFileReader getInstance() {
        if (instance == null) {
            instance = new BinaryFileReader();
        }
        return instance;
    }
}