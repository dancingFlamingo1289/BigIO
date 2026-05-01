package bigFiles.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract class representing a large-scale file.
 * Unlike BaseFile, this class is designed for files that exceed the available RAM.
 * It focuses on disk-based operations, indexing, and streaming rather than 
 * in-memory storage.
 * 
 * @author Elias Kassas
 */
public abstract class BigFile {
    /** Folder containing the file. */
    protected final File folder;

    /** Physical file on disk. */
    protected File file;

    /** File name (with extension). */
    protected String fileName;

    /** File extension (e.g., .txt, .csv, .log). */
    protected String extension;

    /** Lock for thread-safety during heavy I/O operations. */
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    /** 
     * Lightweight index to store offsets or key positions. 
     * This allows random access without loading the whole file. 
     */
    protected final List<Long> index;

    /** Total count of records or lines indexed. */
    protected long totalElements = 0;

    /** Timestamp of the last known modification during indexing. */
    private long lastKnownModification = 0;

    /**
     * Constructor for a BigFile.
     *
     * @param folder    Folder where the file is located.
     * @param fileName  File name without the extension.
     * @param extension File extension (e.g., ".txt").
     */
    public BigFile(File folder, String fileName, String extension) {
        this.extension = extension.startsWith(".") ? extension : "." + extension;
        this.fileName = fileName.replace(this.extension, "") + this.extension;
        this.folder = folder;

        if (this.folder != null && this.folder.mkdirs());

        this.file = new File(this.folder, this.fileName);
        this.index = new ArrayList<>();
    }

    /**
     * Constructor for a BigFile without a specific folder.
     *
     * @param fileName  File name without the extension.
     * @param extension File extension.
     */
    public BigFile(String fileName, String extension) {
        this.extension = extension.startsWith(".") ? extension : "." + extension;
        this.fileName = fileName.replace(this.extension, "") + this.extension;
        this.folder = null;
        this.file = new File(this.fileName);
        this.index = new ArrayList<>();
    }

    // ========== ABSTRACT METHODS ==========

    /**
     * Scans the file to build an internal map of record positions.
     * This is the core logic for enabling fast access to large data.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public abstract void buildIndex() throws IOException;

    /**
     * Reads metadata and rebuilds the index if the file has changed.
     *
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException 
     */
    public abstract void read() throws IOException, ClassNotFoundException;
    
    /**
     * Overwrites the file with the provided data.
     * Implementation should handle large data through streams to prevent RAM overflow.
     *
     * @param args Data to be written to the file.
     * @throws Exception If writing fails.
     */
    public abstract void write(Object... args) throws Exception;

    /**
     * Appends data to the end of the file. 
     * Rewriting the entire file is avoided to maintain performance.
     *
     * @param data The object to append.
     * @throws Exception If the operation fails.
     */
    public abstract void append(Object... args) throws Exception;

    /**
     * Deletes the physical file from the disk.
     *
     * @return true if successfully deleted.
     */
    public abstract boolean deletePhysicalFile();

    // ========== CORE LOGIC ==========

    /**
     * Checks if the physical file has been modified since the last indexing.
     */
    protected boolean isOutdated() {
        return file.exists() && file.lastModified() > lastKnownModification;
    }

    /**
     * Updates the internal timestamp to match the file's current state.
     */
    protected void updateTimestamp() {
        this.lastKnownModification = file.lastModified();
    }

    /**
     * Ensures the index and metadata are synchronized with the disk state.
     * @throws ClassNotFoundException 
     */
    protected void ensureIndexUpToDate() throws IOException, ClassNotFoundException {
        if (isOutdated()) {
            read();
        }
    }

    // ========== UTILITIES ==========

    public String getFilePath() {
        return file.getAbsolutePath();
    }

    public boolean exists() {
        return file.exists();
    }

    public String getName() {
        return fileName;
    }

    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[BigData, name=" + fileName + 
               ", elements=" + totalElements + ", path=" + getFilePath() + "]";
    }
}