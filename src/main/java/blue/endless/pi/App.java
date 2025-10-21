package blue.endless.pi;

import java.util.Optional;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.EnemyType;
import blue.endless.pi.gui.EditorFrame;
import blue.endless.pi.gui.Tileset;

public class App {
	public static void main(String... args) {
		Tileset.init();
		Optional<ObjectElement> itemsJson = Assets.readObject("items/items.json");
		if (itemsJson.isPresent()) {
			ItemType.load(itemsJson.get());
		}
		Optional<ObjectElement> enemiesJson = Assets.readObject("enemies/enemies.json");
		if (enemiesJson.isPresent()) {
			EnemyType.load(enemiesJson.get());
		}
		
		EditorFrame editor = new EditorFrame();
		editor.setVisible(true); // Launch the app proper!
	}
}