package basicFiles.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class representing a binary file.
 * Allows writing and reading a serializable Java object,
 * without enforcing a specific extension (e.g., .bin).
 *
 * @author Elias Kassas
 */
public class BinaryFile extends BaseFile {
	/** In-memory content loaded from the file. */
	private Object content;

	/** Extension used for this file. */
	private final String extension;

	/**
	 * Constructor for a binary file with a configurable extension.
	 *
	 * @param folder      Folder containing the file.
	 * @param fileName    File name without extension.
	 * @param extension   File extension (e.g.: ".dat", ".save", ".bkp").
	 */
	public BinaryFile(File folder, String fileName, String extension)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		super(folder, fileName, extension.startsWith(".") ? extension : "." + extension);
		this.extension = extension.startsWith(".") ? extension : "." + extension;

		if (file.exists()) {
			read();
		}
	}

	/**
	 * Constructor for a binary file without a folder.
	 *
	 * @param fileName   File name without extension.
	 * @param extension  File extension.
	 */
	public BinaryFile(String fileName, String extension)
			throws FileNotFoundException, ClassNotFoundException, IOException {

		super(fileName, extension.startsWith(".") ? extension : "." + extension);
		this.extension = extension.startsWith(".") ? extension : "." + extension;

		if (file.exists()) {
			read();
		}
	}

	//	public BinaryFile(String path) {
	//		
	//	}

	@Override
	protected String getExtension() {
		return extension;
	}

	@Override
	public void write(Object... args) throws IOException {
		lock.readLock().lock();

		try {
			if (args.length == 0 || !(args[0] instanceof Serializable serializable)) {
				System.err.println("Error: the provided object is not Serializable.");
				return;
			}

			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
				out.writeObject(serializable);
				content = serializable;
			} catch (IOException e) {
				System.err.println("Error while writing to the file: " + e.getMessage());
				throw e;
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void read() throws FileNotFoundException, IOException, ClassNotFoundException {
		if (!file.exists()) return;

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		content = in.readObject();
		in.close();
	}

	/**
	 * Returns the object loaded from the file.
	 *
	 * @return The object loaded, or null if empty.
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * Clears the content of the file and memory.
	 * @throws IOException 
	 */
	public void clear() throws IOException {
		content = null;

		try (FileOutputStream out = new FileOutputStream(file)) {
			// Overwrites with an empty file
		} catch (IOException e) {
			System.err.println("Error while clearing the file: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Physically removes the file from the file system.
	 *
	 * @return true if deletion succeeded, false otherwise.
	 * @throws FileNotFoundException 
	 */
	public boolean deletePhysicalFile() {
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
//				System.out.println("File " + file.getName() + " was physically deleted.");
				content = null;
			} 
			return deleted;
		} else {
			//System.out.println("File does not exist: " + file.getName());
			return false;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "{ \n" + content +
				"\n}";
	}

	/**
     * Checks if the file's internal data buffer is currently empty.
     * This method evaluates the presence of loaded data rather than 
     * checking the physical file size on disk.
     * 
     * @return {@code true} if the file content has not been initialized 
     *         or is explicitly null; {@code false} otherwise.
     */
    public boolean isEmpty() {
        return content == null;
    }
	
	@Override
	public void append(Object... args) throws Exception {
		throw new IllegalArgumentException("Impossible to call append on an audio.");
	}
}
