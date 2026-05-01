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

    /**
     * Builds the index for the binary file.
     * Currently optimized for single-object or flat binary structures.
     * 
     * @throws IOException if an I/O error occurs during indexing.
     */
    @Override
    public void buildIndex() throws IOException {
        lock.writeLock().lock();
        try {
            index.clear();
            totalElements = 0;

            // Quick validation: if the file doesn't exist or is empty, stop here.
            if (!exists() || file.length() == 0) {
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                // Since we treat the file as a single-object collection for now,
                // an 'if' statement is more appropriate and readable than a loop.
                if (fis.available() > 0) {
                    long currentPos = 0;
                    index.add(currentPos);

                    // Note: We avoid recreating multiple InputStreams to preserve resources.
                    // Object reading logic can be expanded here as the library evolves.
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
}