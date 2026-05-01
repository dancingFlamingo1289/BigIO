package basicFiles.fileFactories;

import java.io.IOException;
import basicFiles.files.BaseFile;

/**
 * Functional interface defining the contract for file factory implementations.
 * Classes implementing this interface are responsible for instantiating 
 * specific types of {@link BaseFile} objects based on a provided system path.
 * @author Elias Kassas
 */
public interface FileFactory {
    /**
     * Creates or retrieves a file object associated with the specified path.
     * Implementations of this method determine the concrete type of 
     * {@link BaseFile} to be returned and may handle instance persistence 
     * or caching logic.
     * 
     * @param path The absolute or relative system path to the file.
     * @return A {@link BaseFile} instance corresponding to the given path.
     * @throws IOException If an error occurs during file access, creation, or if the path is invalid.
     */
	BaseFile create(String path) throws IOException;
}