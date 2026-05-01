package basicFiles.files.medias;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import basicFiles.files.BaseFile;

/**
 * Class representing an image file (e.g., .png, .jpg, .bmp).
 * This class facilitates reading and writing images via the {@link ImageIO} API
 * and maintains a {@link BufferedImage} representation in memory.
 * <p>
 * It can be used to load external files, resources from the classpath, or 
 * to save generated graphics.
 * </p>
 * @author Elias Kassas
 */
public class Image extends BaseFile {
    /** The image data currently loaded into memory. */
    private BufferedImage image;

    /**
     * Constructs an image file within a specific folder.
     * Automatically reads the image from disk if the file exists.
     *
     * @param folder    The folder containing the image.
     * @param fileName  The file name without the extension.
     * @param format    The image format (e.g., "png").
     * @throws IOException            If an error occurs during file access.
     * @throws ClassNotFoundException If class resolution fails during synchronization.
     */
    public Image(File folder, String fileName, String format) throws IOException, ClassNotFoundException {
        super(folder, fileName, "." + format);
        if (file.exists()) {
            read();
        }
    }

    /**
     * Constructs an image file in the current working directory.
     * Automatically reads the image from disk if the file exists.
     *
     * @param fileName The name without extension.
     * @param format   The image format (e.g., "jpg").
     * @throws IOException            If an error occurs during file access.
     * @throws ClassNotFoundException If class resolution fails.
     */
    public Image(String fileName, String format) throws IOException, ClassNotFoundException {
        super(fileName, "." + format);
        if (file.exists()) {
            read();
        }
    }

    /**
     * Loads an image directly from the classpath (e.g., inside a JAR or project resources).
     *
     * @param resourcePath The internal path to the resource (e.g., "/assets/logo.png").
     * @param fileName     The logical name to assign to the file.
     * @param extension    The file extension (e.g., ".png").
     * @throws IOException If the resource is missing or cannot be read.
     */
    public Image(String resourcePath, String fileName, String extension) throws IOException {
        super(fileName, extension);

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found in classpath: " + resourcePath);
            }
            this.image = ImageIO.read(is);
        }
    }

    /**
     * Reads the image file from the disk into the in-memory buffer.
     *
     * @throws IOException            If the file cannot be read.
     * @throws ClassNotFoundException If metadata synchronization fails.
     */
    @Override
    public void read() throws IOException, ClassNotFoundException {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Writes a {@link BufferedImage} to disk using the defined format.
     *
     * @param args The first argument (args[0]) must be the {@link BufferedImage} to save.
     */
    @Override
    public void write(Object... args) {
        if (args.length == 0 || !(args[0] instanceof BufferedImage img)) {
            System.err.println("Error: No BufferedImage provided for write operation.");
            return;
        }

        try {
            ImageIO.write(img, extension, file);
            this.image = img;
        } catch (IOException e) {
            System.err.println("Error writing image: " + e.getMessage());
        }
    }

    /**
     * Gets the current {@link BufferedImage} stored in memory.
     * 
     * @return The current image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the in-memory image to a new {@link BufferedImage}.
     * 
     * @param newImage The new image to store.
     */
    public void setImage(BufferedImage newImage) {
        this.image = newImage;
    }

    /**
     * Deletes the physical file from the system and flushes the image buffer.
     *
     * @return {@code true} if deleted; {@code false} if the file didn't exist or deletion failed.
     */
    public boolean deletePhysicalFile() {
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("File " + file.getName() + " was physically deleted.");
                if (image != null) image.flush();
            } else {
                System.err.println("Failed to delete file: " + file.getName());
            }
            return deleted;
        }
        return false;
    }

    /**
     * Wraps the current {@link BufferedImage} into an {@link ImageIcon}.
     * Useful for displaying the image in Swing components like JLabel.
     * 
     * @return An {@link ImageIcon} representation of the image.
     */
    public ImageIcon toImageIcon() {
        return (image != null) ? new ImageIcon(image) : null;
    }
    
    /**
     * Image appending is not supported as binary image formats require 
     * specific file structure and headers.
     * 
     * @throws IllegalArgumentException Always thrown as images cannot be appended.
     */
    @Override
	public void append(Object... args) throws Exception {
		throw new IllegalArgumentException("Impossible to call append on an image.");
	}
}