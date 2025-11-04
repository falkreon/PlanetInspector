package blue.endless.pi;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;

public class BGM {
	public static final MusicSchema SCHEMA = new MusicSchema();
	private static List<String> bgm;
	
	public static void init() {
		Optional<ObjectElement> musicJson = Assets.readObject("music.json");
		if (musicJson.isPresent()) {
			List<String> list = new ArrayList<>();
			ArrayElement all = musicJson.get().getArray("all");
			for(ValueElement val : all) {
				if (val instanceof PrimitiveElement prim) {
					prim.asString().ifPresent(list::add);
				}
			}
			bgm = List.copyOf(list);
		} else {
			bgm = List.of();
		}
	}
	
	public static List<String> all() {
		return bgm;
	}
	
	private static class MusicSchema implements SchemaType.Editable<String> {
		@Override
		public String deserialize(ValueElement v) {
			if (v instanceof PrimitiveElement prim) {
				return prim.asString().orElse("");
			} else {
				return "";
			}
		}

		@Override
		public Optional<String> convert(String value) {
			return Optional.ofNullable(value);
		}
		
		@Override
		public JComponent createEditor(ObjectElement parent, String key, Runnable editCallback) {
			JComboBox<String> result = new JComboBox<>(bgm.toArray(new String[bgm.size()]));
			result.setEditable(true);
			String value = deserialize(parent.getPrimitive(key));
			result.setSelectedItem(value);
			result.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					put(parent, key, (String) result.getSelectedItem());
					editCallback.run();
				}
				
			});
			return result;
		}
		
		@Override
		public JComponent createEditor(ArrayElement parent, int index, Runnable editCallback) {
			JComboBox<String> result = new JComboBox<>(bgm.toArray(new String[bgm.size()]));
			result.setEditable(true);
			String value = deserialize(parent.getPrimitive(index));
			result.setSelectedItem(value);
			result.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					put(parent, index, (String) result.getSelectedItem());
					editCallback.run();
				}
				
			});
			return result;
		}
	}
}
