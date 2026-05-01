package basicFiles.fileReaders;

import basicFiles.fileFactories.FileFactory;
import basicFiles.files.BaseFile;

/**
 * Abstract base class for file readers.
 * This class provides the template for loading files from both the local file system 
 * and application resources using an injected {@link FileFactory}.
 * 
 * @author Elias Kassas
 */
public abstract class FileReader {
    
    /**
     * The specific factory (Text, Binary, etc.) used to instantiate file objects.
     * This dependency is injected via the subclass constructor.
     */
    protected final FileFactory factory;

    /**
     * Protected constructor to initialize the reader with a specific factory.
     * 
     * @param factory The {@link FileFactory} implementation to be used for file creation.
     */
    protected FileReader(FileFactory factory) {
        this.factory = factory;
    }

    /**
     * Loads a file from a specified system path.
     * Delegates the instantiation and caching logic to the internal factory.
     * 
     * @param path The absolute or relative system path of the file.
     * @return A {@link BaseFile} instance corresponding to the provided path.
     * @throws FileReaderException If a disk reading error occurs during instantiation.
     */
    public BaseFile loadFromPath(String path) throws FileReaderException {
        try {
            // Unique logic: delegation to the factory which handles cache and type
            return factory.create(path);
        } catch (Exception e) {
            throw new FileReaderException("Erreur de lecture disque : " + path, e);
        }
    }

    /**
     * Loads a file from the application resources using a specific {@link ClassLoader}.
     * This method supports both standard filesystem execution and execution within a JAR file 
     * by creating temporary files when direct path access is unavailable.
     * 
     * @param fileName    The name or path of the resource relative to the resource root.
     * @param classLoader The {@link ClassLoader} to be used for locating the resource.
     * @return A {@link BaseFile} instance associated with the resource.
     * @throws FileReaderException If the resource is not found or cannot be loaded correctly.
     */
    public BaseFile loadFromResources(String fileName, ClassLoader classLoader) throws FileReaderException {
        try {
            java.net.URL resource = classLoader.getResource(fileName);
            if (resource == null) throw new FileReaderException("Ressource introuvable : " + fileName);

            // inside the IDE or the classical filesystem
            if (resource.getProtocol().equals("file")) {
                String path = java.nio.file.Path.of(resource.toURI()).toFile().getAbsolutePath();
                return factory.create(path);
            } else {
                // inside a JAR : temporary extraction to provide a valid path to the factory
                java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("dancingFlamingo1289_res_", "_" + fileName.replace("/", "_"));
                try (java.io.InputStream is = resource.openStream()) {
                    java.nio.file.Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                tempFile.toFile().deleteOnExit();
                return factory.create(tempFile.toAbsolutePath().toString());
            }
        } catch (Exception e) {
            throw new FileReaderException("Erreur lors du chargement des ressources : " + fileName, e);
        }
    }
    
    /**
     * Loads a file from the application resources using the default ClassLoader.
     * 
     * @param fileName The name or path of the resource.
     * @return A {@link BaseFile} instance associated with the resource.
     * @throws FileReaderException If the resource loading fails.
     */
    public BaseFile loadFromResources(String fileName) throws FileReaderException {
        return loadFromResources(fileName, getClass().getClassLoader());
    }
}