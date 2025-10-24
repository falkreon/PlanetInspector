package blue.endless.pi;

import java.awt.Color;
import java.awt.Dialog;
import java.util.Optional;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.settings.SettingsConfiguration;
import com.github.weisj.darklaf.settings.ThemeSettings;
import com.github.weisj.darklaf.theme.Theme;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.spec.AccentColorRule;
import com.github.weisj.darklaf.theme.spec.FontPrototype;
import com.github.weisj.darklaf.theme.spec.FontSizePreset;
import com.github.weisj.darklaf.theme.spec.FontSizeRule;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.EnemyType;
import blue.endless.pi.gui.EditorFrame;
import blue.endless.pi.gui.Tileset;

public class App {
	public static void main(String... args) {
		Preferences.init();
		
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
		//ThemeSettings.showSettingsDialog(editor, Dialog.ModalityType.APPLICATION_MODAL);
	}
}