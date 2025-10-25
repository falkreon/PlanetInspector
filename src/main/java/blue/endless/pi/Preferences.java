package blue.endless.pi;

import java.awt.Color;
import java.awt.Dialog;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.github.weisj.darklaf.theme.SolarizedDarkTheme;
import com.github.weisj.darklaf.theme.SolarizedLightTheme;
import com.github.weisj.darklaf.theme.Theme;
import com.github.weisj.darklaf.theme.spec.AccentColorRule;
import com.github.weisj.darklaf.theme.spec.FontPrototype;
import com.github.weisj.darklaf.theme.spec.FontSizeRule;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.io.json.JsonWriterOptions;

public class Preferences {
	public static Path defaultRoomsDir = Path.of("~");
	public static Path defaultWorldsDir = Path.of("~");
	
	public static void init() {
		ObjectElement prefs = new ObjectElement();
		
		
		Path prefsPath = getPrefsPath();
		Path prefsFile = prefsPath.resolve("preferences.json");
		System.out.println("Using preferences path: "+prefsPath);
		try {
			Files.createDirectories(prefsPath);
			
			
			if (Files.exists(prefsFile)) {
				try (InputStream in = Files.newInputStream(prefsFile, StandardOpenOption.READ)) {
					prefs = Jankson.readJsonObject(in);
				}
			}
			
			
			
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
		
		LafManager.setDecorationsEnabled(true);
		
		ObjectElement theme = prefs.getObject("theme");
		prefs.put("theme", theme);
		String themeName = theme.getPrimitive("name").asString().orElse("darcula");
		theme.put("name", PrimitiveElement.of(themeName));
		int percentTextSize = theme.getPrimitive("percent_text_size").asInt().orElse(125);
		theme.put("percent_text_size", PrimitiveElement.of(percentTextSize));
		
		ObjectElement directories = prefs.getObject("directories");
		prefs.put("directories", directories);
		
		Optional<Path> planetsDir = readOrCompute(directories, "planets_data", Preferences::detectPlanetsDir);
		Optional<Path> worldsDir = readOrCompute(directories, "worlds", Optional::empty);
		Optional<Path> areasDir = readOrCompute(directories, "areas", Optional::empty);
		Optional<Path> roomsDir = readOrCompute(directories, "rooms", Optional::empty);
		
		System.out.println("Directories: "+directories);
		
		if (roomsDir.isPresent()) defaultRoomsDir = roomsDir.get();
		if (worldsDir.isPresent()) defaultWorldsDir = worldsDir.get();
		
		try (BufferedWriter out = Files.newBufferedWriter(prefsFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			
			Jankson.writeJson(prefs, out, JsonWriterOptions.STRICT);
			out.flush();
		} catch (SyntaxError | IOException ex) {
			ex.printStackTrace();
		}
		
		Theme baseTheme = switch(themeName) {
			case "intellij" -> new IntelliJTheme();
			case "solarized_light" -> new SolarizedLightTheme();
			case "solarized_dark" -> new SolarizedDarkTheme();
			case "darcula" -> new DarculaTheme();
			default -> new DarculaTheme();
			
			
		};
		Theme activeTheme = baseTheme.derive(
				FontSizeRule.relativeAdjustment(percentTextSize),
				FontPrototype.getDefault(),
				AccentColorRule.fromColor(new Color(255, 0, 255))
				);
		
		
		LafManager.setTheme(activeTheme);
		LafManager.install();
		
		//ThemeSettings.showSettingsDialog(null, Dialog.ModalityType.APPLICATION_MODAL);
	}
	
	public static Path getUserHome() {
		return Path.of(System.getProperty("user.home"));
		
		//String os = System.getProperty("os.name").toLowerCase();
		//if (os.contains("windows")) {
		//	return System.getProperty("user.home");
		//} else {
			
		//
		/*}
		String javaUserHome = System.getProperty("user.home");
		System.out.println("Java user home: "+javaUserHome);
		String xdgHome = System.getenv("HOME");
		String home = (xdgHome == null || xdgHome.isBlank()) ? "~" : xdgHome;
		
		return home;*/
	}
	
	public static Optional<Path> detectPlanetsDir() {
		try {
			Path defaultPlanetsDir = getUserHome().resolve("AppData").resolve("Local").resolve("Metroid");
			if (Files.exists(defaultPlanetsDir)) return Optional.of(defaultPlanetsDir);
			
			Path userHome = getUserHome();
			if (userHome.getNameCount() > 0) {
				String username = userHome.getName(userHome.getNameCount()-1).toString();
				Path noPrefixWinePlanetsDir = userHome.resolve(".wine").resolve("drive_c").resolve("users").resolve(username).resolve("AppData").resolve("Local").resolve("Metroid");
				if (Files.exists(noPrefixWinePlanetsDir)) return Optional.of(noPrefixWinePlanetsDir);
			}
			
		} catch (Throwable t) {}
		
		return Optional.empty();
	}
	
	public static enum OS {
		WINDOWS,
		LINUX,
		OSX,
		UNKNOWN;
	}
	
	/**
	 * Attempts to bin the operating system into mac/win/linux
	 * See https://stackoverflow.com/a/18417382
	 * @return The currently running host operating system
	 */
	public static OS getOS() {
		String os = System.getProperty("os.name", "unknown").toLowerCase();
		if (os.contains("mac") || os.contains("darwin"))return OS.OSX;
		if (os.contains("win")) return OS.WINDOWS;
		if (os.contains("linux") || os.contains("unix")) return OS.LINUX;
		return OS.UNKNOWN;
	}
	
	public static Path getPrefsPath() {
		return switch(getOS()) {
			case LINUX -> {
				String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
				if (xdgConfigHome != null && !xdgConfigHome.isBlank()) {
					yield Path.of(xdgConfigHome, "planet_inspector");
				} else {
					yield getUserHome().resolve(".config").resolve("planet_inspector");
				}
			}
			case OSX, UNKNOWN -> Path.of(".");
			case WINDOWS -> {
				yield getUserHome().resolve("AppData").resolve("Local").resolve("planet_inspector");
			}
			case null, default -> Path.of(".");
		};
	}
	
	/**
	 * Gets a nullable Path from a config, computes it if empty, nonexistant, or null, and then saves it back to the
	 * config object.
	 * 
	 * @param base The object to look for the Path in
	 * @param key  The key to find the Path at
	 * @param supplier A supplier to compute the Path if the key is absent or null
	 * @return The Path if available or computable, or empty if there is no available option.
	 */
	public static Optional<Path> readOrCompute(ObjectElement base, String key, Supplier<Optional<Path>> supplier) {
		Optional<Path> result = Optional.empty();
		try {
			result = base.getPrimitive(key).asString().map(Path::of);
			if (result.isPresent() && Files.exists(result.get())) return result;
			System.out.println("path doesn't exist? -> "+result);
		} catch (Throwable t) {
			System.out.println("... invalid path");
		}
		
		result = supplier.get();
		System.out.println("Computed "+key+" from supplier: "+result);
		base.put(key, PrimitiveElement.of(result.map(Path::toString).orElse(null)));
		
		return result;
	}
}
