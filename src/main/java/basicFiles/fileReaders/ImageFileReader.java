package basicFiles.fileReaders;

import basicFiles.fileFactories.ImageFileFactory;

/**
 * Reader class specialized in handling image files.
 * This class implements the Singleton pattern and utilizes the {@link ImageFileFactory} 
 * to create and manage {@link basicFiles.files.medias.Image} file instances.
 * 
 * @author Elias Kassas
 */
public class ImageFileReader extends FileReader {
    
    /**
     * The unique instance of the singleton {@code ImageFileReader}.
     */
	private static ImageFileReader instance;

    /**
     * Private constructor that initializes the reader by injecting the 
     * singleton instance of {@link ImageFileFactory} into the parent {@link FileReader}.
     */
    private ImageFileReader() {
    	super(ImageFileFactory.getInstance());
    }

    /**
     * Returns the unique instance of the reader.
     * This method is synchronized to ensure thread-safe initialization in multi-threaded environments.
     * 
     * @return The singleton instance of {@code ImageFileReader}.
     */
    public static synchronized ImageFileReader getInstance() {
        if (instance == null) {
            instance = new ImageFileReader();
        }
        return instance;
    }
}