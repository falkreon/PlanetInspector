package blue.endless.pi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import blue.endless.pi.gui.Tileset;

public class Assets {
	private static Map<String, BufferedImage> imageCache = new HashMap<>();
	
	/**
	 * Loads an image resource in from the assets folder in the jar and returns it. Each call produces a new copy of the
	 * image.
	 * @param id the resource ID of the image, including its extension
	 * @return The newly-loaded image, or empty if it couldn't be loaded.
	 */
	public static Optional<BufferedImage> readImage(String id) {
		String location = "assets/"+id;
		try (InputStream str = Tileset.class.getClassLoader().getResourceAsStream(location)) {
			if (str == null) {
				return Optional.empty();
			} else {
				BufferedImage result = ImageIO.read(str);
				return Optional.ofNullable(result);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return Optional.empty();
		}
	}
	
	/**
	 * If the image is in the cache, returns the cached image. Otherwise, reads it in from the assets folder in the jar.
	 * @param id The resource ID of the image, including its extension
	 * @return The cached image, or the loaded image if it wasn't cached, or empty if it was unretrievable.
	 */
	public static Optional<BufferedImage> getCachedImage(String id) {
		BufferedImage maybe = imageCache.get(id);
		if (maybe != null) return Optional.of(maybe);
		
		Optional<BufferedImage> opt = readImage(id);
		if (opt.isPresent()) {
			imageCache.put(id, opt.get());
		}
		return opt;
	}
}
