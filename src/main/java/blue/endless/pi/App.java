package blue.endless.pi;

import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.Optional;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.io.ObjectReaderFactory;
import blue.endless.jankson.api.io.json.JsonWriterOptions;
import blue.endless.pi.gui.WorldEditor;
import blue.endless.pi.gui.view.ViewerFrame;
import blue.endless.pi.enigma.WorldMeta;
import blue.endless.pi.enigma.util.EnemyType;
import blue.endless.pi.enigma.util.EnigmaFormat;
import blue.endless.pi.enigma.util.ItemType;
import blue.endless.pi.enigma.util.ObjectType;
import blue.endless.pi.gui.TestFrame;
import blue.endless.pi.gui.Tileset;

public class App {
	public static void main(String... args) {
		
		try {
			StringWriter sw = new StringWriter();
			Jankson.writeJson(new WorldMeta(), new ObjectReaderFactory(), sw, JsonWriterOptions.STRICT);
			System.out.println(sw.toString());
		} catch (SyntaxError | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Preferences.init();
		Tileset.init();
		BGM.init();
		
		Assets.readObject("items/items.json").ifPresent(ItemType::load);
		Assets.readObject("enemies/enemies.json").ifPresent(EnemyType::load);
		Assets.readObject("objects/objects.json").ifPresent(ObjectType::load);
		
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