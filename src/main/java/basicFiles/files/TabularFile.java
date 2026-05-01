package basicFiles.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing a tabular file (CSV, TSV, SSV, etc.).
 * This class allows for reading and writing structured data as a list of string rows.
 * It implements {@link Iterable} to facilitate row-by-row traversal.
 * 
 * @author Elias Kassas
 */
public class TabularFile extends BaseFile implements Iterable<List<String>> {
	/** In-memory representation of the file content as a list of rows, where each row is a list of field strings. */
	private List<List<String>> content;

	/** The character used to separate fields (e.g., ',', ';', or '\t'). */
	private char separator;

	/**
	 * Constructor for a generic tabular file with a custom separator.
	 * Automatically loads the file into memory if it exists on disk.
	 *
	 * @param fileName  The name of the file including its extension.
	 * @param separator The character used as a field separator.
	 * @throws IOException If an error occurs during the initial read operation.
	 */
	public TabularFile(String fileName, char separator) throws IOException {
		super(fileName, "");
		this.content = new ArrayList<>();
		this.separator = separator;

		if (file.exists()) {
			read();
		}
	}

	/**
	 * Constructor for a generic tabular file within a specific directory.
	 *
	 * @param folder    The parent directory.
	 * @param fileName  The name of the file.
	 * @param separator The field separator character.
	 * @throws IOException If an error occurs during reading.
	 */
	public TabularFile(File folder, String fileName, char separator) throws IOException {
		super(folder, fileName, "");
		this.content = new ArrayList<>();
		this.separator = separator;

		if (file.exists()) {
			read();
		}
	}

	/**
	 * Specialized constructor for CSV files (comma-separated) within a directory.
	 *
	 * @param folder   The parent directory.
	 * @param fileName The name of the file (extension .csv is handled by the base class).
	 * @throws IOException If an error occurs during reading.
	 */
	public TabularFile(File folder, String fileName) throws IOException {
		this(folder, fileName, ',');
	}

	/**
	 * Specialized constructor for CSV files in the current working directory.
	 *
	 * @param fileName The name of the file.
	 * @throws IOException If an error occurs during reading.
	 */
	public TabularFile(String fileName) throws IOException {
		super(fileName, ".csv");
		this.content = new ArrayList<>();
		this.separator = ',';

		if (file.exists()) {
			read();
		}
	}

	/**
	 * Retrieves the row located at the specified index.
	 * 
	 * @param index The zero-based index of the row.
	 * @return A {@link List} of strings representing the row's fields.
	 */
	public List<String> getRow(int index) {
		return content.get(index);
	}

	/**
	 * Replaces the row at the given index and immediately synchronizes the change to disk.
	 * 
	 * @param index       The index of the row to replace.
	 * @param replacement The new list of fields for the row.
	 * @return The previously stored row.
	 * @throws IOException If the write operation fails.
	 */
	public List<String> setRow(int index, List<String> replacement) throws IOException {
		List<String> previous = content.set(index, replacement);
		write();
		return previous;
	}

	/**
	 * Loads tabular data from the physical file into the in-memory buffer.
	 * The file is parsed line by line according to the defined separator.
	 * 
	 * @throws IOException If the file cannot be read.
	 */
	@Override
	public void read() throws IOException {
		content.clear();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.add(parseCSVLine(line));
			}
		}
	}

	/**
	 * Writes the current in-memory content to the physical file.
	 * If arguments are provided, they are added to the content before writing.
	 *
	 * @param args Optional: A {@code List<String>} for a single row, 
	 *             a {@code List<List<String>>} for multiple rows, 
	 *             or a raw {@code String} line.
	 * @throws IOException If an error occurs during the write process.
	 */
	@Override
	public void write(Object... args) throws IOException {
		lock.readLock().lock();

		try {
			if (args.length > 0) {
				Object arg = args[0];
				if (arg instanceof List<?> row) {
					if (!row.isEmpty() && row.get(0) instanceof String) {
						content.add(convertRow(row));
					} else if (!row.isEmpty() && row.get(0) instanceof List<?>) {
						for (Object innerRow : row) {
							content.add(convertRow((List<?>) innerRow));
						}
					}
				} else if (arg instanceof String line) {
					content.add(parseCSVLine(line));
				}
			}

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (List<String> row : content) {
					writer.write(formatCSVLine(row));
					writer.newLine();
				}
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	/** 
	 * Returns a copy of the in-memory tabular data. 
	 * 
	 * @return A list of rows.
	 */
	public List<List<String>> getContent() {
		return new ArrayList<>(content);
	}

	/** 
	 * Returns the content as a list of raw strings joined by the separator. 
	 * 
	 * @return A list of formatted CSV/TSV lines.
	 */
	public List<String> getContentString() {
		List<String> result = new ArrayList<>();
		for (List<String> row : content) {
			result.add(String.join(String.valueOf(separator), row));
		}
		return result;
	}

	/** 
	 * Clears both the in-memory buffer and the physical file content. 
	 * 
	 * @throws IOException If the write operation fails.
	 */
	public void clear() throws IOException {
		content.clear();
		write();
	}

	/** 
	 * Formats a single row into a delimited string with proper RFC 4180 escaping. 
	 */
	private String formatCSVLine(List<String> row) {
		List<String> escaped = new ArrayList<>();
		for (String field : row) {
			escaped.add(escapeField(field));
		}
		return String.join(String.valueOf(separator), escaped);
	}

	/** 
	 * Escapes fields containing separators, quotes, or newlines according to RFC 4180. 
	 */
	private String escapeField(String field) {
		if (field.contains(String.valueOf(separator)) || field.contains("\"") || field.contains("\n")) {
			field = field.replace("\"", "\"\"");
			return "\"" + field + "\"";
		}
		return field;
	}

	/**
	 * Parses a raw delimited line into individual fields, handling quoted values.
	 * 
	 * @param line The raw line from the file.
	 * @return A list of parsed field strings.
	 */
	private List<String> parseCSVLine(String line) {
		List<String> fields = new ArrayList<>();
		StringBuilder cur = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (inQuotes) {
				if (c == '"') {
					if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
						cur.append('"');
						i++;
					} else {
						inQuotes = false;
					}
				} else {
					cur.append(c);
				}
			} else {
				if (c == '"') {
					inQuotes = true;
				} else if (c == separator) {
					fields.add(cur.toString());
					cur.setLength(0);
				} else {
					cur.append(c);
				}
			}
		}
		fields.add(cur.toString().trim());
		return fields;
	}

	private List<String> convertRow(List<?> row) {
		List<String> result = new ArrayList<>();
		for (Object field : row) {
			result.add(field == null ? "" : field.toString());
		}
		return result;
	}

	@Override
	public Iterator<List<String>> iterator() {
		return content.iterator();
	}

	@Override
	public boolean deletePhysicalFile() {
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				content.clear();
			}
			return deleted;
		}
		return false;
	}

	/**
	 * Generates a visually aligned, plain-text table representation of the content.
	 * Useful for console debugging or UI display.
	 * 
	 * @return A string containing the aligned table.
	 */
	public String toAlignedTableString() {
		if (content.isEmpty()) return "(empty)";
		int maxColumns = content.stream().mapToInt(List::size).max().orElse(0);
		int[] widths = new int[maxColumns];

		for (List<String> row : content) {
			for (int i = 0; i < row.size(); i++) {
				widths[i] = Math.max(widths[i], row.get(i).length());
			}
		}

		StringBuilder sb = new StringBuilder();
		for (List<String> row : content) {
			for (int i = 0; i < maxColumns; i++) {
				String cell = (i < row.size()) ? row.get(i) : "";
				sb.append(String.format("%-" + (widths[i] + 2) + "s", cell));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/** 
	 * Removes the row at the specified index and updates the disk. 
	 * 
	 * @param index The index of the row to remove.
	 * @return The removed row content.
	 * @throws IOException If the synchronization fails.
	 */
	public List<String> removeRow(int index) throws IOException {
		if (index < 0 || index >= this.content.size()) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + content.size());
		}
		List<String> removed = content.remove(index);
		write();
		return removed;
	}

	/**
	 * Appending is currently not implemented for tabular files.
	 * 
	 * @throws IllegalArgumentException Always thrown.
	 */
	@Override
	public void append(Object... args) throws Exception {
		throw new IllegalArgumentException("Not yet possible to call append on a tabular file.");
	}
}