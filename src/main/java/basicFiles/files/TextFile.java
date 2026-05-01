package basicFiles.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Concrete class representing a text file (typically .txt).
 * It allows for reading and writing strings line by line while maintaining 
 * an in-memory representation using a {@link LinkedList}.
 * <p>
 * This class implements {@link Iterable}, enabling standard for-each loops:
 * <br>{@code for (String line : myFile) {...}}
 * </p>
 *
 * @author Elias Kassas
 */
public class TextFile extends BaseFile implements Iterable<String> {
    /** In-memory buffer of the file content, where each element represents a single line. */
	private final LinkedList<String> content;

	/**
	 * Constructor for a text file with a dynamic extension.
	 * The file is automatically loaded into memory if it exists; otherwise, it is created.
	 *
	 * @param folder    The folder containing the file.
	 * @param fileName  The name of the file without its extension.
	 * @param extension The specific file extension (e.g., ".txt", ".log").
	 * @throws IOException If the file cannot be accessed or read.
	 */
	public TextFile(File folder, String fileName, String extension) throws IOException {
	    super(folder, fileName, extension);
	    this.content = new LinkedList<>();

	    if (file.exists()) {
	        read();
	    } else {
	        write();
	    }
	}
	
	/**
	 * Default constructor for a standard .txt file.
	 *
	 * @param folder   The directory containing the file.
	 * @param fileName The name of the file without extension.
	 * @throws IOException If a reading error occurs.
	 */
	public TextFile(File folder, String fileName) throws IOException {
	    this(folder, fileName, ".txt");
	}

	/**
	 * Constructor for a text file located in the current working directory.
	 *
	 * @param fileName The file name.
	 * @throws IOException If a reading error occurs.
	 */
	public TextFile(String fileName) throws IOException {
		this(null, fileName, ".txt");
	}

	/**
	 * Reads the file content and populates the in-memory line list.
	 * Updates the internal modification timestamp upon successful completion.
	 *
	 * @throws IOException If the file is missing or permissions are insufficient.
	 */
	@Override
	public void read() throws IOException {
		if (!file.exists()) {
			throw new IOException("File does not exist: " + file.getAbsolutePath());
		}
		if (!file.canRead()) {
			throw new IOException("Insufficient permissions to read: " + file.getAbsolutePath());
		}

		content.clear();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				this.content.add(line);
			}
		}
		
		updateTimestamp();
	}

	/**
	 * Replaces the current file content with the provided arguments and writes to disk.
	 * This method utilizes a write lock to ensure thread safety during the overwrite.
	 *
	 * @param args Objects or Lists of objects whose {@code toString()} values will form the new lines.
	 * @throws IOException If a writing error occurs.
	 */
	@Override
	public void write(Object... args) throws IOException {
	    lock.writeLock().lock();
	    try {
	        if (args != null && args.length > 0) {
	            content.clear(); // Complete replacement of current buffer
	            for (Object arg : args) {
	                if (arg instanceof List<?> list) {
	                    for (Object o : list) content.add(o.toString());
	                } else if (arg != null) {
	                    content.add(arg.toString());
	                }
	            }
	        }
	        Files.write(file.toPath(), content);
	        updateTimestamp();
	    } finally {
	        lock.writeLock().unlock();
	    }
	}
	
	/**
	 * Adds new lines to the end of the existing content and synchronizes with the disk.
	 *
	 * @param args Objects or Lists of objects to append.
	 * @throws IOException If the write operation fails.
	 */
	@Override
	public void append(Object... args) throws IOException {
	    if (args == null || args.length == 0) return;
	    
	    lock.writeLock().lock();
	    try {
	        for (Object arg : args) {
	            if (arg instanceof List<?> list) {
	                for (Object o : list) content.add(o.toString());
	            } else if (arg != null) {
	                content.add(arg.toString());
	            }
	        }
	        Files.write(file.toPath(), content);
	        updateTimestamp();
	    } finally {
	        lock.writeLock().unlock();
	    }
	}

	/**
	 * Adds a single line to the in-memory buffer without immediate disk synchronization.
	 *
	 * @param o The object to add as a new line.
	 */
	public void add(Object o) {
		content.add(o.toString());
	}

	/**
	 * Removes matching lines from the in-memory buffer based on string equality.
	 *
	 * @param o The object representation to remove.
	 */
	public void remove(Object o) {
		content.removeIf(e -> e.equals(o.toString()));
	}

	/**
	 * Erases all content from both the memory buffer and the physical file.
	 * 
	 * @throws IOException If the file cannot be updated.
	 */
	public void clear() throws IOException {
		content.clear();
		Files.write(file.toPath(), Collections.emptyList());
	}

	/**
	 * Retrieves a copy of the lines currently held in memory.
	 * Ensures the buffer is synchronized with disk changes before returning.
	 *
	 * @return An {@link ArrayList} containing the file lines.
	 * @throws IOException If a synchronization read fails.
	 * @throws ClassNotFoundException If class resolution fails during read.
	 */
	public ArrayList<Object> getContent() throws IOException, ClassNotFoundException {
		ensureUpToDate();
		return new ArrayList<>(content);
	}

	/**
	 * Checks if the line list is empty after ensuring data is up to date.
	 *
	 * @return {@code true} if empty; {@code false} otherwise.
	 */
	public boolean isEmpty() throws IOException, ClassNotFoundException {
		ensureUpToDate();
		return content.isEmpty();
	}

	/**
	 * Performs a linear search for a line matching the string representation of an object.
	 *
	 * @param o The object to find.
	 * @return {@code true} if a match exists.
	 */
	public boolean contains(Object o) throws IOException, ClassNotFoundException {
		ensureUpToDate();
		String target = o.toString();
		for (String line : content) {
			if (line.equals(target)) return true;
		}
		return false;
	}

	@Override
	public boolean deletePhysicalFile() {
		if (!file.exists()) return false;

		boolean deleted = file.delete();
		if (deleted) {
			content.clear();
		}
		return deleted;
	}

	/**
	 * Returns an iterator over the lines of the file.
	 * Updates the internal buffer from disk before creating the iterator.
	 *
	 * @return An unmodifiable iterator of file lines.
	 */
	@Override
	public Iterator<String> iterator() {
		try {
			ensureUpToDate();
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException("Iterator update failed", e);
		} 
		return Collections.unmodifiableList(new ArrayList<>(content)).iterator();
	}

	/**
	 * Retrieves a specific line by its index.
	 * 
	 * @param line The zero-based line index.
	 * @return The content of the line.
	 */
	public String getLine(int line) throws ClassNotFoundException, IOException {
		ensureUpToDate();
		return content.get(line);
	}

	/**
	 * Updates a specific line in the in-memory buffer.
	 * Note: This does not trigger an automatic write to disk.
	 * 
	 * @param line    The index to update.
	 * @param newLine The new text content.
	 * @return Always {@code true} upon successful update.
	 */
	public boolean setLine(int line, String newLine) {
		content.set(line, newLine);
		return true;
	}

	/**
	 * Returns the total number of lines in the file.
	 */
	public int getLineCount() throws ClassNotFoundException, IOException {
		ensureUpToDate();
		return content.size();
	}
}