package bigFiles.fileReaders;

/**
 * Custom exception representing errors encountered during the file loading process.
 * <p>
 * This exception is used by {@link BigFileReader} to provide meaningful error messages
 * when disk I/O, resource resolution, or factory instantiation fails.
 * </p>
 * 
 * @author Elias Kassas
 */
class BigFileReaderException extends Exception {
    private static final long serialVersionUID = -7783482991904740629L;

    /**
     * Constructs a new exception with a descriptive message.
     * @param message The error detail.
     */
	public BigFileReaderException(String message) {
        super(message);
    }

	/**
     * Constructs a new exception with a message and the underlying cause.
     * @param message The error detail.
     * @param cause   The original exception (e.g., IOException).
     */
    public BigFileReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}

