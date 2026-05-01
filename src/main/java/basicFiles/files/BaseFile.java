package basicFiles.files;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract class representing a generic file.
 * This class provides the common behaviors for all file types
 * (naming, path management, renaming, and existence checks), without assuming 
 * a specific format or content structure.
 * <p>
 * Subclasses must define specific implementation details for reading, writing, 
 * and appending data based on the file type (e.g., text, binary, or media).
 * </p>
 * 
 * @author Elias Kassas
 */
public abstract class BaseFile {
    /** The parent directory containing the file. */
	protected final File folder;

    /** The physical {@link File} object representation on the disk. */
	protected File file;

    /** The file name, typically including the extension. */
	protected String fileName;

    /** The file extension (e.g., ".txt", ".csv"). */
	protected String extension;
	
	/** 
     * A concurrency lock used to prevent multiple instances pointing to the 
     * same physical file from performing simultaneous write operations. 
     */
	protected final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/** Stores the timestamp of the last known modification performed by the application to detect external changes. */
	private long lastKnownModification = 0;

	/**
	 * Constructs a generic file within a specific folder.
     * Ensures the extension is correctly formatted and initializes the folder structure.
	 * @param folder    The directory where the file is (or will be) located.
	 * @param fileName  The name of the file.
	 * @param extension The file extension.
	 */
	public BaseFile(File folder, String fileName, String extension) {
        this.extension = extension.startsWith(".") ? extension : "." + extension;
        this.fileName = fileName.replace(this.extension, "") + this.extension;
        this.folder = folder;

		if (this.folder.mkdirs()) {
			this.fileName = fileName;
		}
		this.file = new File(this.folder, this.fileName);
	}

	/**
	 * Constructs a generic file without a specified folder.
	 * @param fileName  The name of the file.
	 * @param extension The file extension.
	 */
	public BaseFile(String fileName, String extension) {
		this.fileName = fileName.replace(extension, "") + extension;
		this.folder = null;
		this.file = new File(this.fileName);
	}

	// ========== COMMON METHODS ==========

	/**
	 * Renames the physical file on disk and updates the internal state.
	 * @param newName The new name (extension will be appended if missing).
	 * @throws IllegalArgumentException If the physical rename operation fails.
	 */
	public void rename(String newName) throws IllegalArgumentException {
		String extension = getExtension();
		File newFile = new File(folder, newName.replace(extension, "") + extension);
		if (file.renameTo(newFile)) {
			file = newFile;
			fileName = newFile.getName();
		} else {
			throw new IllegalArgumentException("Unable to rename file: " + fileName);
		}
	}

	/**
	 * Checks whether the file exists on the physical disk.
	 * @return {@code true} if the file exists; {@code false} otherwise.
	 */
	public boolean exists() {
		return file.exists();
	}

	/**
	 * Retrieves the file name including its extension.
	 * @return The file name.
	 */
	public String getName() {
		return fileName;
	}

	/**
	 * Retrieves the absolute path of the file on the system.
	 * @return The full file path.
	 */
	public String getFilePath() {
		return file.getAbsolutePath();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[name=" + fileName + ", path=" + getFilePath() + "]";
	}

	// ========== ABSTRACT METHODS ==========

	/**
	 * Reads the file content from the disk.
	 * Specific formatting and processing are handled by concrete subclasses.
	 * @throws IOException            If an error occurs during disk access.
	 * @throws ClassNotFoundException If an object read requires a missing class.
	 */
	public abstract void read() throws IOException, ClassNotFoundException;

	/**
	 * Writes data to the file, overwriting existing content.
	 * @param args Variable arguments used by subclasses for specialized writing.
	 * @throws Exception If an error occurs during the write operation.
	 */
	public abstract void write(Object... args) throws Exception;
	
	/**
	 * Appends data to the end of the file.
	 * @param args Variable arguments used by subclasses for specialized appending.
	 * @throws Exception If an error occurs during the append operation.
	 */
	public abstract void append(Object... args) throws Exception;
	
	/**
	 * Convenience method to save the current state of the file by calling {@link #write()}.
	 * @throws Exception If the save operation fails.
	 */
	public void save() throws Exception {
		write();
	}

	/**
	 * Returns the extension associated with this file type.
	 * @return The file extension (e.g., ".txt").
	 */
	protected String getExtension() {
		return this.extension;
	}

	/**
	 * Physically deletes the file from the file system.
	 * @return {@code true} if the file was successfully deleted; {@code false} otherwise.
	 */
	public abstract boolean deletePhysicalFile();

	/**
     * Determines if the file has been modified externally since the last update.
     * @return {@code true} if the disk version is newer than the cached version.
     */
	protected boolean isOutdated() {
        return file.exists() && file.lastModified() > lastKnownModification;
    }

    /**
     * Synchronizes the internal modification timestamp with the physical file's current status.
     */
    protected void updateTimestamp() {
        this.lastKnownModification = file.lastModified();
    }
    
    /**
     * Ensures the internal state is consistent with the disk by reloading the file if it is outdated.
     * @throws IOException            If a reading error occurs.
     * @throws ClassNotFoundException If class resolution fails during reading.
     */
    protected void ensureUpToDate() throws IOException, ClassNotFoundException {
        if (isOutdated()) {
            read();
        }
    }
}