package blue.endless.pi;

import java.util.Optional;


import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.gui.WorldEditor;
import blue.endless.pi.gui.view.ViewerFrame;
import blue.endless.pi.enigma.util.EnemyType;
import blue.endless.pi.enigma.util.ItemType;
import blue.endless.pi.gui.TestFrame;
import blue.endless.pi.gui.Tileset;

public class App {
	public static void main(String... args) {
		Preferences.init();
		Tileset.init();
		BGM.init();
		
		Assets.readObject("items/items.json").ifPresent(ItemType::load);
		Assets.readObject("enemies/enemies.json").ifPresent(EnemyType::load);
		
		/*
		TestFrame viewer = new TestFrame();
		viewer.setTitle("Planet Inspector Tests");
		viewer.setIconImage(Assets.getCachedImage("icon.png").orElseGet(Assets::missingImage));
		viewer.setVisible(true);
		*/
		
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