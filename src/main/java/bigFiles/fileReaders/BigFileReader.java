package bigFiles.fileReaders;

import bigFiles.fileFactories.BigFileFactory;
import bigFiles.files.BigFile;

/**
 * Base class for high-level file loading operations.
 * <p>
 * This class coordinates between a {@link BigFileFactory} and the filesystem or classpath.
 * It provides a consistent API for loading files while wrapping low-level exceptions
 * into {@link BigFileReaderException}.
 * </p>
 * 
 * @author Elias Kassas
 */
public abstract class BigFileReader {
	/** The factory used to instantiate the specific file type. */
    protected final BigFileFactory factory;

    /**
     * Constructs a reader with a specific file factory.
     * 
     * @param factory The factory implementation (e.g., Image, Text).
     */
    protected BigFileReader(BigFileFactory factory) {
        this.factory = factory;
    }

    /**
     * Loads a file from a standard filesystem path.
     * 
     * @param path The absolute or relative path to the file.
     * @return The loaded {@link BigFile} instance.
     * @throws BigFileReaderException If the file cannot be read or instantiated.
     */
    public BigFile loadFromPath(String path) throws BigFileReaderException {
        try {
            return factory.create(path);
        } catch (Exception e) {
            throw new BigFileReaderException("Erreur de lecture disque : " + path, e);
        }
    }

    /**
     * Loads a file located within the application's resources (classpath).
     * 
     * @param fileName    The path to the resource.
     * @param classLoader The ClassLoader to use for discovery.
     * @return The loaded {@link BigFile} instance.
     * @throws BigFileReaderException If the resource is not found or inaccessible.
     */
    public BigFile loadFromResources(String fileName, ClassLoader classLoader) throws BigFileReaderException {
        try {
            java.net.URL resource = classLoader.getResource(fileName);
            if (resource == null) throw new BigFileReaderException("Ressource introuvable : " + fileName);
            
            String path = java.nio.file.Path.of(resource.toURI()).toFile().getAbsolutePath();
            return factory.create(path);
        } catch (Exception e) {
            throw new BigFileReaderException("Erreur lors du chargement des ressources : " + fileName, e);
        }
    }
    
    /**
     * Loads a file located within the application's resources (classpath) without the ClassLoader 
     * (it uses the ClassLoader of the BigFileReader class).
     * 
     * @param fileName    The path to the resource.
     * @return The loaded {@link BigFile} instance.
     * @throws BigFileReaderException If the resource is not found or inaccessible.
     */
    public BigFile loadFromResources(String fileName) throws BigFileReaderException {
        return loadFromResources(fileName, getClass().getClassLoader());
    }
}