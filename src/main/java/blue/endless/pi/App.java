package blue.endless.pi;

import blue.endless.pi.gui.WorldEditor;
import blue.endless.pi.gui.view.RootFrame;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

import blue.endless.pi.enigma.util.EnemyType;
import blue.endless.pi.enigma.util.ItemType;
import blue.endless.pi.enigma.util.ObjectType;
import blue.endless.pi.gui.Tileset;

public class App {
	public static void main(String... args) {
		Preferences.init();
		Tileset.init();
		BGM.init();
		
		Assets.readObject("items/items.json").ifPresent(ItemType::load);
		Assets.readObject("enemies/enemies.json").ifPresent(EnemyType::load);
		Assets.readObject("objects/objects.json").ifPresent(ObjectType::load);
		
		
		/**
		 * Test: Open a game save!
		 */
		
		Path saveFile = Path.of(".", "ENIGMA-1.mp_save");
		ArrayList<byte[]> files = new ArrayList<>();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try(InputStream in = Files.newInputStream(saveFile, StandardOpenOption.READ)) {
			InflaterInputStream zin = new InflaterInputStream(new BufferedInputStream(in));
			
			// Buffer all world data. This might break for unusually big worlds!
			
			int data = 0;
			while(data != -1) {
				data = zin.read();
				if (data == 0) {
					if (bytes.size() > 0) {
						files.add(bytes.toByteArray());
						bytes.reset();
					}
				} else if (data == -1) {
					break;
				} else {
					bytes.write(data);
				}
			}
			
			if (bytes.size() > 0) {
				files.add(bytes.toByteArray());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		System.out.println("Number of files: " + files.size());
		if (files.size() >= 1) {
			String file = new String(files.get(0));
			System.out.println(file);
		}
		/*
		 * End Test!
		 */
		
		
		RootFrame viewer = new RootFrame();
		viewer.setTitle("Planet Inspector");
		viewer.setIconImage(Assets.getCachedImage("icon.png").orElseGet(Assets::missingImage));
		viewer.setVisible(true);
		
		viewer.setView(new WorldEditor(viewer));
	}
}