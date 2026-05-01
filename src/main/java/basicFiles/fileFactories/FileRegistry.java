package basicFiles.fileFactories;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import basicFiles.files.BaseFile;

/**
 * A central registry designed to manage and cache shared instances of {@link BaseFile} objects.
 * This class ensures that only one instance of a file is created for any unique physical path,
 * optimizing memory usage and ensuring data consistency across the application.
 * 
 * @author Elias Kassas
 */
class FileRegistry {
    /** A map storing cached {@link BaseFile} instances, keyed by their absolute system paths. */
	private static final Map<String, BaseFile> instances = new HashMap<>();
	
    /**
     * Retrieves a shared instance of a file from the registry or creates it if it does not exist.
     * The provided path is normalized to its absolute form to prevent duplicate entries 
     * caused by different path representations (e.g., relative vs. absolute).
     * <p>
     * This method is synchronized to maintain thread safety during the retrieval 
     * and creation of shared instances.
     * </p>
     * 
     * @param path The system path of the file to retrieve or create.
     * @param creator A functional interface used to instantiate the specific {@link BaseFile} 
     * subclass if no instance is currently cached.
     * @return The shared {@link BaseFile} instance associated with the normalized path.
     */
	public static synchronized BaseFile getSharedInstance(String path, Function<String, BaseFile> creator) {
        // Normalizes the path to ensure that "./test.txt" and "test.txt" are treated as the same key
        String absolutePath = new File(path).getAbsolutePath();
        
        return instances.computeIfAbsent(absolutePath, p -> creator.apply(p));
    }
}