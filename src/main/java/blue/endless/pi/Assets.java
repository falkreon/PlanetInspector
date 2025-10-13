package blue.endless.pi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.imageio.ImageIO;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.gui.Tileset;

public class Assets {
	private static Map<String, BufferedImage> imageCache = new HashMap<>();
	private record PalettedImageKey(String resId, int low, int mid, int high) {
		PalettedImageKey(String resId, int[] palette) {
			this(resId, palette[0], palette[1], palette[2]);
		}
		
	}
	private static Map<PalettedImageKey, BufferedImage> palettedImageCache = new HashMap<>();
	
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
	
	public static Optional<BufferedImage> getPalettedImage(String id, int... palette) {
		BufferedImage maybe = palettedImageCache.get(new PalettedImageKey(id, palette));
		if (maybe != null) return Optional.of(maybe);
		
		Optional<BufferedImage> opt = readImage(id);
		opt.ifPresent((it) -> Palette.colorize(it, palette));
		return opt;
	}
	
	public static Optional<ObjectElement> readObject(String id) {
		String location = "assets/"+id;
		try (InputStream str = Tileset.class.getClassLoader().getResourceAsStream(location)) {
			if (str == null) {
				return Optional.empty();
			} else {
				return Optional.of(Jankson.readJsonObject(str));
			}
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
			return Optional.empty();
		}
	}
	
	public static <T> Optional<T> readObject(String id, Function<ObjectElement, Optional<T>> deserializer) {
		return readObject(id).flatMap(deserializer);
	}
}
