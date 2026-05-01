package bigFiles.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Composite class representing a directory specifically for BigFiles.
 * This class ensures that large-scale data structures can be organized
 * hierarchically without mixing with standard RAM-based files.
 * 
 * @author Elias Kassas
 */
public class BigFolder extends BigFile {
    /** Internal storage for children: Key = File Name, Value = BigFile object. */
    private final Map<String, BigFile> children;

    /** Tracks synchronization state of child elements. */
    private final Map<String, Boolean> isSynced;

    public BigFolder(File folder, String fileName) {
        super(folder, fileName, "");
        this.children = new HashMap<>();
        this.isSynced = new HashMap<>();

        if (!this.file.exists()) {
            this.file.mkdirs();
        }
    }

    public BigFolder(String folderName) {
        this(new File("."), folderName);
    }

    /**
     * Adds a BigFile or another BigFolder to this directory.
     * @param component The BigFile element to add.
     */
    public void add(BigFile component) {
        String name = component.getName();
        children.put(name, component);
        isSynced.put(name, false); 
    }

    /**
     * Removes a child from the internal map.
     * @param component The BigFile element to remove.
     */
    public void remove(BigFile component) {
        String name = component.getName();
        children.remove(name);
        isSynced.remove(name);
    }

    /**
     * Folder indexing simply refreshes the timestamp and clears sync flags 
     * to ensure children are checked during the next write.
     */
    @Override
    public void buildIndex() throws IOException {
        updateTimestamp();
    }

    @Override
    public void read() throws IOException {
        buildIndex();
    }

    /**
     * Recursively saves all modified children to the disk.
     * This ensures that only "dirty" files are processed, saving I/O time.
     */
    @Override
    public void write(Object... args) throws Exception {
        lock.writeLock().lock();
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            for (Map.Entry<String, BigFile> entry : children.entrySet()) {
                String fileName = entry.getKey();
                BigFile child = entry.getValue();

                // Check if the current child needs an update
                if (isSynced.get(fileName) == null || !isSynced.get(fileName)) {
                    child.write(args); 
                    isSynced.put(fileName, true);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Broadcasts an append operation to all children in the folder.
     */
    @Override
    public void append(Object... args) throws Exception {
        for (BigFile child : children.values()) {
            child.append(args);
        }
    }

    /**
     * Recursively deletes all physical files and subfolders.
     * @return true if the entire directory tree was successfully deleted.
     */
    @Override
    public boolean deletePhysicalFile() {
        boolean allDeleted = true;
        for (BigFile child : children.values()) {
            if (!child.deletePhysicalFile()) {
                allDeleted = false;
            }
        }
        return allDeleted && file.delete();
    }

    /**
     * Returns the list of BigFile children.
     */
    public List<BigFile> getChildren() {
        return new ArrayList<>(children.values());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BIG_FOLDER: ").append(getName()).append(" (").append(getFilePath()).append(")\n");

        if (children.isEmpty()) {
            sb.append("  (empty)");
        } else {
            for (BigFile child : children.values()) {
                // Indent child's toString for a nice tree view
                sb.append("  |-- ").append(child.toString().replace("\n", "\n  ")).append("\n");
            }
        }

        return sb.toString();
    }
}