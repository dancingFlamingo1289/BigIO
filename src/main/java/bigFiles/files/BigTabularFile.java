package bigFiles.files;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A BigData implementation for tabular files (CSV, TSV, etc.).
 * This class uses an index of byte offsets to access huge tables without loading 
 * them into memory, while matching the TabularFile API.
 * 
 * @author Elias Kassas
 */
public class BigTabularFile extends BigFile implements Iterable<List<String>> {

    private char separator;

    public BigTabularFile(File folder, String fileName, char separator) throws IOException {
        super(folder, fileName, "");
        this.separator = separator;
        if (exists()) {
            read();
        }
    }

    public BigTabularFile(String fileName, char separator) throws IOException {
        this(null, fileName, separator);
    }

    public BigTabularFile(String fileName) throws IOException {
        this(null, fileName, ',');
    }

    // ========== CORE INDEXING LOGIC ==========

    @Override
    public void buildIndex() throws IOException {
        lock.writeLock().lock();
        try {
            index.clear();
            totalElements = 0;
            if (!exists() || file.length() == 0) return;

            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                long currentPos = 0;
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

    // ========== API MATCHING TABULARFILE ==========

    /**
     * Overwrites the file with the provided data.
     * Supports List of fields, List of rows, or raw Strings.
     */
    @Override
    public void write(Object... args) throws Exception {
        lock.writeLock().lock();
        try {
            if (args == null || args.length == 0) return;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                processArgsToWriter(writer, args);
            }
        } finally {
            read();
            lock.writeLock().unlock();
        }
    }

    /**
     * Appends rows to the end of the file without rewriting it.
     */
    @Override
    public void append(Object... args) throws Exception {
        if (args == null || args.length == 0) return;

        lock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            processArgsToWriter(writer, args);
        } finally {
            read();
            lock.writeLock().unlock();
        }
    }

    private void processArgsToWriter(BufferedWriter writer, Object... args) throws IOException {
        for (Object arg : args) {
            if (arg instanceof List<?> list) {
                if (list.isEmpty()) continue;
                
                // Check if it's a single row (List<String>) or multiple rows (List<List<String>>)
                if (list.get(0) instanceof List) {
                    for (Object innerRow : list) {
                        writer.write(formatCSVLine(convertRow((List<?>) innerRow)));
                        writer.newLine();
                    }
                } else {
                    writer.write(formatCSVLine(convertRow(list)));
                    writer.newLine();
                }
            } else if (arg != null) {
                // Assume raw formatted string
                writer.write(arg.toString());
                writer.newLine();
            }
        }
    }

    // ========== DATA ACCESS ==========

    /**
     * Jumps to the specific row index and parses it.
     * @throws ClassNotFoundException 
     */
    public List<String> getRow(int rowIndex) throws IOException, ClassNotFoundException {
        lock.readLock().lock();
        try {
            ensureIndexUpToDate();
            if (rowIndex < 0 || rowIndex >= totalElements) {
                throw new IndexOutOfBoundsException("Row " + rowIndex + " out of bounds.");
            }
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(index.get(rowIndex));
                return parseCSVLine(raf.readLine());
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    // ========== FORMATTING & PARSING (RFC 4180) ==========

    private String formatCSVLine(List<String> row) {
        List<String> escaped = new ArrayList<>();
        for (String field : row) {
            if (field.contains(String.valueOf(separator)) || field.contains("\"") || field.contains("\n")) {
                escaped.add("\"" + field.replace("\"", "\"\"") + "\"");
            } else {
                escaped.add(field);
            }
        }
        return String.join(String.valueOf(separator), escaped);
    }

    private List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        if (line == null) return fields;
        
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"'); i++;
                    } else inQuotes = false;
                } else cur.append(c);
            } else {
                if (c == '"') inQuotes = true;
                else if (c == separator) {
                    fields.add(cur.toString());
                    cur.setLength(0);
                } else cur.append(c);
            }
        }
        fields.add(cur.toString().trim());
        return fields;
    }

    private List<String> convertRow(List<?> row) {
        List<String> result = new ArrayList<>();
        for (Object field : row) result.add(field == null ? "" : field.toString());
        return result;
    }

    @Override
    public Iterator<List<String>> iterator() {
        try {
            return Files.lines(file.toPath()).map(this::parseCSVLine).iterator();
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