package basicFiles.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a directory capable of containing files and other folders.
 * This implementation follows the <b>Composite Pattern</b>, allowing a folder to 
 * treat individual files and sub-folders uniformly as {@link BaseFile} objects.
 * 
 * @author Elias Kassas
 */
public class Folder extends BaseFile {
	/** 
	 * A map storing the folder's contents, where the key is the file name 
	 * and the value is the corresponding {@link BaseFile} instance. 
	 */
	private final Map<String, BaseFile> children;

	/** 
	 * Tracks the synchronization status of each child element.
	 * {@code true} if the element is synced with the disk, {@code false} if it is new or modified. 
	 */
	private final Map<String, Boolean> isSynced;

	/**
	 * Constructs a folder within a specific parent directory.
	 * Automatically creates the physical directory on disk if it does not exist.
	 *
	 * @param folder   The parent directory.
	 * @param fileName The name of the folder.
	 */
	public Folder(File folder, String fileName) {
		super(folder, fileName, "");
		this.children = new HashMap<>();
		this.isSynced = new HashMap<>();

		if (!this.file.exists()) {
			this.file.mkdirs();
		}
	}

	/**
	 * Constructs a folder in the current working directory.
	 *
	 * @param folderName The name of the folder.
	 */
	public Folder(String folderName) {
		this(new File("."), folderName);
	}

	/**
	 * Adds a file or sub-folder to this folder.
	 * Marks the newly added component as out-of-sync to ensure it is written during the next save.
	 *
	 * @param component The {@link BaseFile} component to add.
	 */
	public void add(BaseFile component) {
		String name = component.getName();
		children.put(name, component);
		isSynced.put(name, false); 
	}

	/**
	 * Removes a file or sub-folder from this folder's internal registry.
	 *
	 * @param component The {@link BaseFile} component to remove.
	 */
	public void remove(BaseFile component) {
		String name = component.getName();
		children.remove(name);
		isSynced.remove(name);
	}

	/**
	 * Folders do not possess a file extension.
	 * 
	 * @return An empty string.
	 */
	@Override
	protected String getExtension() {
		return ""; 
	}

	/**
	 * Reads the contents of the physical directory and populates the children map.
	 * 
	 * @throws ClassNotFoundException Currently thrown as this method is not yet implemented.
	 * @throws IOException If a disk access error occurs.
	 */
	@Override
	public void read() throws IOException, ClassNotFoundException {
		throw new ClassNotFoundException("NotYetImplemented");
	}

	/**
	 * Recursively writes all unsynced children to the disk.
	 * This method uses a read lock to safely iterate through the children and 
	 * ensures that only modified or new elements are physically written.
	 *
	 * @param args Optional parameters passed down to the children's write methods.
	 * @throws Exception If an error occurs during the writing of any child element.
	 */
	@Override
	public void write(Object... args) throws Exception {
		lock.readLock().lock();

		try {
			if (!file.exists()) {
				file.mkdirs();
			}

			for (Map.Entry<String, BaseFile> entry : children.entrySet()) {
				String fileName = entry.getKey();
				BaseFile fileComponent = entry.getValue();

				// Check if the current element needs to be written
				if (isSynced.get(fileName) == null || !isSynced.get(fileName)) {
					fileComponent.write(args); 
					isSynced.put(fileName, true);
				}
				// No action taken if the element is already synced
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Recursively deletes all physical files and sub-directories contained within this folder, 
	 * followed by the folder itself.
	 *
	 * @return {@code true} if the folder and all its contents were successfully deleted; {@code false} otherwise.
	 */
	@Override
	public boolean deletePhysicalFile() {
		for (BaseFile child : children.values()) {
			child.deletePhysicalFile();
		}
		return file.delete();
	}

	/**
	 * Retrieves a list of all child components contained within this folder.
	 * 
	 * @return A {@link List} of {@link BaseFile} objects.
	 */
	public List<BaseFile> getChildren() {
		return new ArrayList<>(children.values());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FOLDER ").append(getName()).append(" (").append(getFilePath()).append(")\n");

		for (BaseFile child : children.values()) {
			sb.append("  |-- ").append(child.toString().replace("\n", "\n  ")).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Appending is not supported for folder structures.
	 * 
	 * @param args Ignored.
	 * @throws IllegalArgumentException Always thrown as this operation is invalid for folders.
	 */
	@Override
	public void append(Object... args) throws Exception {
		throw new IllegalArgumentException("Not yet possible to call append on a folder.");
	}
}