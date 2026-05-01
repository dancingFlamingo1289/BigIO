package bigFiles.files;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

/**
 * A BigData implementation for text files.
 * Provides the exact same API as TextFile but handles files that exceed RAM 
 * by using disk-based indexing and streaming.
 * 
 * @author Elias Kassas
 */
public class BigTextFile extends BigFile implements Iterable<String> {
    /**
     * Constructor for BigTextFile.
     * @param folder Destination folder.
     * @param fileName File name without extension.
     * @param extension File extension (e.g., ".txt").
     * @throws IOException If indexing fails.
     */
    public BigTextFile(File folder, String fileName, String extension) throws IOException {
        super(folder, fileName, extension);
        if (exists()) {
            read();
        }
    }

    public BigTextFile(String fileName) throws IOException {
        this(null, fileName, ".txt");
    }

    // ========== CORE INDEXING LOGIC ==========

    /**
     * Scans the file to record the byte position of every line.
     * Enables O(1) seek performance for large files.
     */
    @Override
    public void buildIndex() throws IOException {
        lock.writeLock().lock();
        try {
            index.clear();
            totalElements = 0;
            if (!exists() || file.length() == 0) return;

            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                long currentPos = 0;
                // Read through the file once to map line starts
                while (raf.readLine() != null) {
                    index.add(currentPos);
                    currentPos = raf.getFilePointer();
                }
                totalElements = index.size();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void read() throws IOException {
        buildIndex();
        updateTimestamp();
    }

    // ========== API MATCHING TEXTFILE ==========

    /**
     * Overwrites the file with the provided arguments.
     * If args is empty or null, the file remains unchanged (Save behavior).
     */
    @Override
    public void write(Object... args) throws Exception {
        lock.writeLock().lock();
        try {
            // UX Safety: If no args are passed, we treat it as a 'Save' operation.
            // Since BigFile is always synced with disk, we do nothing.
            if (args == null || args.length == 0) {
                return; 
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                processArgsToWriter(writer, args);
            }
        } finally {
            read(); // Rebuild index after overwrite
            lock.writeLock().unlock();
        }
    }

    /**
     * Appends data to the end of the file.
     * Supports single objects or Lists of objects.
     */
    @Override
    public void append(Object... args) throws Exception {
        if (args == null || args.length == 0) return;

        lock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            processArgsToWriter(writer, args);
        } finally {
            read(); // Update index with new lines
            lock.writeLock().unlock();
        }
    }

    /**
     * Internal utility to process arguments (Objects or Lists) into the writer.
     * Matches the logic found in TextFile.java.
     */
    private void processArgsToWriter(BufferedWriter writer, Object... args) throws IOException {
        for (Object arg : args) {
            if (arg instanceof List<?> list) {
                for (Object o : list) {
                    if (o != null) {
                        writer.write(o.toString());
                        writer.newLine();
                    }
                }
            } else if (arg != null) {
                writer.write(arg.toString());
                writer.newLine();
            }
        }
    }

    // ========== GETTERS & ITERATION ==========

    /**
     * Retrieves a specific line by index without reading the whole file.
     * @param lineIndex The 0-based index of the line.
     * @return The content of the line.
     * @throws ClassNotFoundException 
     */
    public String getLine(int lineIndex) throws IOException, ClassNotFoundException {
        lock.readLock().lock();
        try {
            ensureIndexUpToDate();
            if (lineIndex < 0 || lineIndex >= totalElements) {
                throw new IndexOutOfBoundsException("Line index " + lineIndex + " out of bounds.");
            }
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(index.get(lineIndex));
                return raf.readLine();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns an iterator that streams lines one by one from disk.
     */
    @Override
    public Iterator<String> iterator() {
        try {
            return Files.lines(file.toPath()).iterator();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean deletePhysicalFile() {
        lock.writeLock().lock();
        try {
            index.clear();
            totalElements = 0;
            return file.delete();
        } finally {
            lock.writeLock().unlock();
        }
    }
}