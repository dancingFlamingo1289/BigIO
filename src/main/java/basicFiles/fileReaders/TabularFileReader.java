package basicFiles.fileReaders;

import basicFiles.fileFactories.TabularFileFactory;

/**
 * Reader class specialized in handling tabular files (e.g., CSV, TSV).
 * This class implements the Singleton pattern and utilizes the {@link TabularFileFactory} 
 * to create and manage {@link basicFiles.files.TabularFile} instances.
 * 
 * @author Elias Kassas
 */
public class TabularFileReader extends FileReader {
    
    /**
     * The unique instance of the singleton {@code TabularFileReader}.
     */
	private static TabularFileReader instance;

    /**
     * Private constructor that initializes the reader by injecting the 
     * singleton instance of {@link TabularFileFactory} into the parent {@link FileReader}.
     */
    private TabularFileReader() {
    	super(TabularFileFactory.getInstance());
    }

    /**
     * Returns the unique instance of the reader.
     * This method is synchronized to ensure thread-safe initialization in multi-threaded environments.
     * 
     * @return The singleton instance of {@code TabularFileReader}.
     */
    public static synchronized TabularFileReader getInstance() {
        if (instance == null) {
            instance = new TabularFileReader();
        }
        return instance;
    }
}