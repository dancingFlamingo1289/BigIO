package basicFiles.fileReaders;

import basicFiles.fileFactories.TextFileFactory;

/**
 * Reader class specialized in handling plain text files.
 * This class implements the Singleton pattern and utilizes the {@link TextFileFactory} 
 * to create and manage {@link basicFiles.files.TextFile} instances.
 * 
 * @author Elias Kassas
 */
public class TextFileReader extends FileReader {
    
    /**
     * The unique instance of the singleton {@code TextFileReader}.
     */
	private static TextFileReader instance;

    /**
     * Private constructor that initializes the reader by injecting the 
     * singleton instance of {@link TextFileFactory} into the parent {@link FileReader}.
     */
    private TextFileReader() {
    	super(TextFileFactory.getInstance());
    }

    /**
     * Returns the unique instance of the reader.
     * This method is synchronized to ensure thread-safe initialization in multi-threaded environments.
     * 
     * @return The singleton instance of {@code TextFileReader}.
     */
    public static synchronized TextFileReader getInstance() {
        if (instance == null) {
            instance = new TextFileReader();
        }
        return instance;
    }
}