package blue.endless.pi.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Palette;

public record RoomInfo(ObjectElement json, ObjectElement general, List<ScreenInfo> screens) {
	public static RoomInfo of(ObjectElement roomJson) {
		ObjectElement general = roomJson.getObject("GENERAL");
		
		ArrayList<ScreenInfo> screens = new ArrayList<>();
		ArrayElement screensArray = roomJson.getArray("SCREENS");
		for(ValueElement elem : screensArray) {
			if (elem instanceof ObjectElement obj) {
				screens.add(new ScreenInfo(obj));
			}
		}
		
		return new RoomInfo(roomJson, general, List.copyOf(screens));
	}

	public String name() {
		return general.getPrimitive("name").asString().orElse("");
	}
	
	public Color getPaletteColor(int paletteId, int colorInPalette) {
		ArrayElement palettesArray = json.getArray("PALETTES");
		if (palettesArray.size() > paletteId) {
			ObjectElement paletteObj = palettesArray.getObject(paletteId);
			
			ArrayElement palette = paletteObj.getArray("f").getArray(0);
			if (palette.size() <= colorInPalette) return Palette.get(0);
			int color = palette.getPrimitive(colorInPalette).asInt().orElse(0);
			return Palette.get(color);
		}
		
		return Color.BLACK;
	}
}