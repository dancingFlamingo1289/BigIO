package bigFiles.files.medias;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import bigFiles.files.BigFile;

/**
 * Class representing an image file (.png, .jpg, etc.).
 * Allows reading and writing images using ImageIO.
 * The image can be created using a JPanel whose g2d was obtained.
 */
public class BigImage extends BigFile {
    /** Image loaded in memory. */
    private BufferedImage image;

    /** Image FORMAT (e.g. "png", "jpg"). */
    private final String FORMAT;

    /**
     * Constructor. <br>
     * Loading from disk (into memory) is automatically done if the file exists.
     *
     * @param folder Folder containing the image.
     * @param fileName Name without extension.
     * @param format Image format (e.g. "png").
     */
    public BigImage(File folder, String fileName, String format) throws IOException, ClassNotFoundException {
        super(folder, fileName, "." + format);
        this.FORMAT = format;
        if (file.exists()) {
            read();
        }
    }

    /**
     * Constructor.<br>
     * Loading from disk (into memory) is automatically done if the file exists.
     *
     * @param fileName File name without extension.
     * @param format Image format (e.g. "png").
     */
    public BigImage(String fileName, String format) throws IOException, ClassNotFoundException {
        super(fileName, "." + format);
        this.FORMAT = format;
        if (file.exists()) {
            read();
        }
    }

    /**
     * Loads an image from the classpath.
     *
     * @param resourcePath Path to the image inside the classpath (e.g. "/QPLPB.jpg").
     * @param fileName Logical name of the file (without extension).
     * @param extension Extension (e.g. ".jpg").
     * @throws IOException if the resource is not found or invalid.
     */
    public BigImage(String resourcePath, String fileName, String extension) throws IOException {
        super(fileName, extension);
        this.FORMAT = extension;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found in the classpath: " + resourcePath);
            }
            this.image = ImageIO.read(is);
        }
    }

    @Override
    public void read() throws IOException {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
        }
    }

    @Override
    public void write(Object... args) {
        if (args.length == 0 || !(args[0] instanceof BufferedImage img)) {
            System.err.println("Error: no BufferedImage provided.");
            return;
        }

        try {
            ImageIO.write(img, FORMAT, file);
            this.image = img;
        } catch (IOException e) {
            System.err.println("Error writing image: " + e.getMessage());
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage newImage) {
        this.image = newImage;
    }

    /**
     * Physically deletes the file from the filesystem.
     *
     * @return true if deleted successfully, false otherwise.
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
        } else {
            System.out.println("File does not exist: " + file.getName());
            return false;
        }
    }

    /**
     * Converts the image to an ImageIcon for display.
     */
    public ImageIcon toImageIcon() {
        return new ImageIcon(image);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File folder = new File("Exemple");
        BigImage imgFile = new BigImage(folder, "dessin", "png");

        // If the file exists, the image is already loaded
        if (imgFile.getImage() == null) {
            // Create a simple image in memory (red rectangle)
            BufferedImage img = new BufferedImage(300, 150, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 300, 150);
            g.setColor(Color.RED);
            g.fillRect(50, 30, 200, 90);
            g.dispose();

            // Write the image to disk
            imgFile.write(img);
            Object[] o = {"Image:", imgFile.toImageIcon()};
            JOptionPane.showMessageDialog(null, o);
            System.out.println("Image saved!");
        } else {
            Object[] o = {"Image:", imgFile.toImageIcon()};
            JOptionPane.showMessageDialog(null, o);
            System.out.println("Image already exists. Width: " + imgFile.getImage().getWidth());
        }
    }

	@Override
	public void buildIndex() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void append(Object... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
