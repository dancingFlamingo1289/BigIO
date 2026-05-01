package bigFiles.fileFactories;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import bigFiles.files.BigFile;

/**
 * Centralized registry for managing {@link BigFile} instances.
 * <p>
 * This class implements a <b>Multiton-like</b> pattern to ensure that each physical file 
 * on the disk is represented by exactly one object in memory. This prevents 
 * synchronization issues and reduces memory overhead when multiple parts of the 
 * application access the same file.
 * </p>
 * @author Elias Kassas
 */
class BigFileRegistry { 
    /** Internal map storing active file instances, indexed by their absolute filesystem paths. */
	private static final Map<String, BigFile> instances = new HashMap<>();
	
    /**
     * Retrieves an existing {@link BigFile} instance or creates a new one if it does not exist.
     * <p>
     * The method normalizes the provided path to its absolute form (e.g., converting 
     * "./file.txt" and "file.txt" to the same key) to maintain registry integrity. 
     * It uses {@code computeIfAbsent} to ensure that the creation logic is only 
     * executed when necessary.
     * </p>
     * 
     * @param path    The relative or absolute path to the file.
     * @param creator A functional interface (lambda) describing how to instantiate 
     *                 the specific {@link BigFile} subtype if not found.
     * @return The shared {@link BigFile} instance for the given path.
     */
	public static synchronized BigFile getSharedInstance(String path, Function<String, BigFile> creator) {
        // Path normalization ensures "./test.txt" and "test.txt" map to the same instance
        String absolutePath = new File(path).getAbsolutePath();
        
        return instances.computeIfAbsent(absolutePath, p -> creator.apply(p));
    }
}