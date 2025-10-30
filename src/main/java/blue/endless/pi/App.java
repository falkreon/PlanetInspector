package blue.endless.pi;

import java.util.Optional;


import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.EnemyType;
import blue.endless.pi.gui.WorldEditor;
import blue.endless.pi.gui.view.View;
import blue.endless.pi.gui.view.ViewerFrame;
import blue.endless.pi.gui.Tileset;

public class App {
	public static void main(String... args) {
		Preferences.init();
		Tileset.init();
		BGM.init();
		
		Optional<ObjectElement> itemsJson = Assets.readObject("items/items.json");
		if (itemsJson.isPresent()) {
			ItemType.load(itemsJson.get());
		}
		Optional<ObjectElement> enemiesJson = Assets.readObject("enemies/enemies.json");
		if (enemiesJson.isPresent()) {
			EnemyType.load(enemiesJson.get());
		}
		
		ViewerFrame viewer = new ViewerFrame();
		viewer.setTitle("Planet Inspector");
		viewer.setIconImage(Assets.getCachedImage("icon.png").orElseGet(Assets::missingImage));
		viewer.setVisible(true);
		
		viewer.setView(new WorldEditor(viewer));
		//EditorFrame editor = new EditorFrame();
		//editor.setVisible(true); // Launch the app proper!
		//ThemeSettings.showSettingsDialog(editor, Dialog.ModalityType.APPLICATION_MODAL);
	}
}