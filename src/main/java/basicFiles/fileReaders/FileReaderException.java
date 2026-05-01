package basicFiles.fileReaders;

/**
 * Custom exception class for handling errors related to file reading operations.
 * This checked exception is thrown by {@link FileReader} when a file cannot be 
 * accessed or processed from the disk or application resources.
 * 
 * @author Elias Kassas
 */
class FileReaderException extends Exception {
    /**
     * Unique identifier for serialization consistency.
     */
    private static final long serialVersionUID = -7783482991904740629L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message The error message explaining the cause of the exception.
     */
	public FileReaderException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and 
     * the underlying cause of the failure.
     * 
     * @param message The error message explaining the cause of the exception.
     * @param cause   The original cause (typically an {@link java.io.IOException} or 
     *                {@link java.net.URISyntaxException}).
     */
    public FileReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}