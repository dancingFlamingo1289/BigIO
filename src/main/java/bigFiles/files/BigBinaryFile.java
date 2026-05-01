package bigFiles.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A BigData implementation for binary files.
 * Instead of loading one giant object, it can store a sequence of serializable objects
 * and access them individually via indexing.
 * 
 * @author Elias Kassas
 */
public class BigBinaryFile extends BigFile {

    public BigBinaryFile(File folder, String fileName, String extension) throws IOException, ClassNotFoundException {
        super(folder, fileName, extension);
        if (exists()) {
            read();
        }
    }

    public BigBinaryFile(String fileName, String extension) throws IOException, ClassNotFoundException {
        this(null, fileName, extension);
    }

    // ========== CORE INDEXING LOGIC ==========

    @Override
    public void buildIndex() throws IOException {
        lock.writeLock().lock();
        try {
            index.clear();
            totalElements = 0;
            if (!exists() || file.length() == 0) return;

            try (FileInputStream fis = new FileInputStream(file)) {
                long currentPos = 0;
                while (fis.available() > 0) {
                    index.add(currentPos);
                    try (CustomObjectInputStream ois = new CustomObjectInputStream(new FileInputStream(file))) {
                        ois.skipBytes((int) currentPos); // This is a simplification
                        // In a real 'Big' binary file, you'd store lengths or use a custom header
                        // For now, we'll index objects by tracking the stream position
                    }
                    // For standard Java Serialization, indexing is complex without custom headers.
                    // To keep it simple and 'Big', we treat the file as a collection of objects.
                    break; // Default behavior for a single-object binary file
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

    // ========== API MATCHING TEXT/TABULAR ==========

    /**
     * Overwrites the file with the provided serializable objects.
     */
    @Override
    public void write(Object... args) throws Exception {
        if (args == null || args.length == 0) return;

        lock.writeLock().lock();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Object arg : args) {
                if (arg instanceof Serializable serializable) {
                    oos.writeObject(serializable);
                }
            }
        } finally {
            read();
            lock.writeLock().unlock();
        }
    }

    /**
     * Appends objects to the binary file.
     * Uses a special stream to avoid header corruption.
     */
    @Override
    public void append(Object... args) throws Exception {
        if (args == null || args.length == 0) return;

        lock.writeLock().lock();
        boolean exists = file.exists() && file.length() > 0;
        
        try (FileOutputStream fos = new FileOutputStream(file, true);
             ObjectOutputStream oos = exists ? new AppendingObjectOutputStream(fos) : new ObjectOutputStream(fos)) {
            
            for (Object arg : args) {
                if (arg instanceof Serializable serializable) {
                    oos.writeObject(serializable);
                }
            }
        } finally {
            read();
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves the first object (for backward compatibility with BinaryFile).
     */
    public Object getContent() throws Exception {
        return getObject(0);
    }

    /**
     * Retrieves a specific object from the sequence.
     */
    public Object getObject(int index) throws Exception {
        if (!exists() || file.length() == 0) return null;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            for (int i = 0; i < index; i++) {
                ois.readObject(); // Skip preceding objects
            }
            return ois.readObject();
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

    // ========== INNER UTILITIES ==========

    /**
     * Helper to append to ObjectOutputStreams without writing a new header.
     */
    private static class AppendingObjectOutputStream extends ObjectOutputStream {
        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        @Override
        protected void writeStreamHeader() throws IOException {
            reset(); // Do not write a new header
        }
    }

    /** Dummy class for indexing logic */
    private static class CustomObjectInputStream extends ObjectInputStream {
        public CustomObjectInputStream(InputStream in) throws IOException { super(in); }
    }
}