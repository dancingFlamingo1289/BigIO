package bigFiles.fileFactories;

import java.io.IOException;
import bigFiles.files.BigFile;

/**
 * Functional interface defining the contract for a BigFile factory.
 * <p>
 * Implementations of this interface are responsible for the instantiation 
 * logic of specific {@link BigFile} subtypes (e.g., text, binary, audio).
 * This abstraction allows the application to create file objects dynamically 
 * by path without being coupled to concrete constructors.
 * </p>
 * 
 * @author Elias Kassas
 */
@FunctionalInterface
public interface BigFileFactory {
    
    /**
     * Creates or retrieves a {@link BigFile} instance associated with the given path.
     * <p>
     * Implementation details (such as whether the file is newly instantiated 
     * or retrieved from a registry) are handled by the concrete factory class.
     * </p>
     *
     * @param path The filesystem path of the file to be managed.
     * @return A concrete {@link BigFile} instance.
     * @throws IOException If the file cannot be accessed, created, or read.
     */
	BigFile create(String path) throws IOException;
}
