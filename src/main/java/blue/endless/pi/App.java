package blue.endless.pi;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Optional;

import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.StructuredDataReader;
import blue.endless.jankson.api.io.ValueElementReader;
import blue.endless.jankson.impl.io.objectwriter.RecordDeserializer;
import blue.endless.pi.gui.EditorFrame;
import blue.endless.pi.gui.Tileset;

public class App {
	public static final int PALETTE_MASK = 0x1F << 12; // Bits 12-16, with palettes 00-0F being user palettes and 10-1F being system palettes
	public static final int ANIMATION_MASK = 0x7 << 17; // Bits 17-19
	public static final int CONVEYOR_MASK = 0x03 << 20; // Bits 20 and 21
	public static final int MIRRORED = 1 << 28;
	public static final int FLIPPED = 1 << 29;
	public static final int ROTATED = 1 << 30;
	
	public static void main(String... args) {
		Tileset.init();
		Optional<ObjectElement> itemsJson = Assets.readObject("items/items.json");
		if (itemsJson.isPresent()) {
			ItemType.load(itemsJson.get());
		}
		
		EditorFrame editor = new EditorFrame();
		editor.setVisible(true); // Launch the app proper!
	}
	
	
	public static <T> ArrayList<T> unpackRecordList(ArrayElement arr, Class<T> clazz) throws IOException, SyntaxError {
		ArrayList<T> result = new ArrayList<>();
		for(ValueElement val : arr) {
			StructuredDataReader reader = ValueElementReader.of(val);
			RecordDeserializer<T> writer = new RecordDeserializer<>(clazz);
			reader.transferTo(writer);
			T t = writer.getResult();
			if (t != null) result.add(t);
		}
		return result;
	}
	
	public static <T> T unpackRecord(ValueElement val, Class<T> clazz) throws IOException, SyntaxError {
		StructuredDataReader reader = ValueElementReader.of(val);
		RecordDeserializer<T> writer = new RecordDeserializer<>(clazz);
		reader.transferTo(writer);
		T result = writer.getResult();
		if (result == null) throw new IOException();
		return result;
	}
	
	
}